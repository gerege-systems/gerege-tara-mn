package mn.gov.gerege.web;

import mn.gov.gerege.AuthRequest;
import mn.gov.gerege.AuthResult;
import mn.gov.gerege.AuthSession;
import mn.gov.gerege.auth.MockMobileIdHandler;
import mn.gov.gerege.hydra.HydraAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Нэвтрэлтийн урсгал — Hydra-гийн login provider.
 *
 * <p>Урсгал:</p>
 * <ol>
 *   <li>GET /auth/login?login_challenge=… → арга сонгох хуудас</li>
 *   <li>POST /auth/login → Mobile-ID эхлүүлж, баталгааны код харуулах хуудас</li>
 *   <li>GET /auth/login/mobile-id/poll → JS-ээс төлөв шалгах; SUCCESS бол redirect_to</li>
 * </ol>
 */
@Controller
@RequestMapping("/auth")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final HydraAdminClient hydra;
    private final MockMobileIdHandler mobileId;
    private final SessionStore sessions;

    public LoginController(HydraAdminClient hydra, MockMobileIdHandler mobileId, SessionStore sessions) {
        this.hydra = hydra;
        this.mobileId = mobileId;
        this.sessions = sessions;
    }

    /** 1-р алхам: арга сонгох хуудсыг харуулах. */
    @GetMapping("/login")
    public String showLogin(@RequestParam("login_challenge") String loginChallenge, Model model) {
        Map<String, Object> loginRequest = hydra.getLoginRequest(loginChallenge);
        String clientName = clientName(loginRequest);
        log.info("Login challenge {} — хүсэгч үйлчилгээ: {}", loginChallenge, clientName);

        model.addAttribute("loginChallenge", loginChallenge);
        model.addAttribute("clientName", clientName);
        return "login";
    }

    /** 2-р алхам: Mobile-ID нэвтрэлт эхлүүлж, баталгааны кодын хуудас руу. */
    @PostMapping("/login")
    public String startMobileId(@RequestParam("login_challenge") String loginChallenge,
                                @RequestParam("personalCode") String personalCode,
                                @RequestParam("phoneNumber") String phoneNumber,
                                Model model) {
        AuthSession session = mobileId.initiate(new AuthRequest(loginChallenge, personalCode, phoneNumber));
        sessions.save(session);
        log.info("Mobile-ID эхэллээ: sessionId={}, controlCode={}", session.sessionId(), session.controlCode());

        model.addAttribute("sessionId", session.sessionId());
        model.addAttribute("controlCode", session.controlCode());
        model.addAttribute("phoneNumber", phoneNumber);
        return "mobileid-verify";
    }

    /** 3-р алхам: JS-ээс дуудах poll endpoint. JSON буцаана. */
    @GetMapping("/login/mobile-id/poll")
    @ResponseBody
    public Map<String, String> poll(@RequestParam("sessionId") String sessionId) {
        AuthSession session = sessions.get(sessionId);
        if (session == null) {
            return Map.of("status", "FAILED", "reason", "session-not-found");
        }

        AuthResult result = mobileId.poll(session);
        if (result.status() == AuthResult.Status.RUNNING) {
            return Map.of("status", "RUNNING");
        }
        if (result.status() == AuthResult.Status.FAILED) {
            sessions.remove(sessionId);
            return Map.of("status", "FAILED");
        }

        // SUCCESS — иргэний мэдээллийг context-оор consent шат руу дамжуулна.
        Map<String, Object> context = new HashMap<>();
        context.put("given_name", result.givenName());
        context.put("family_name", result.familyName());
        context.put("loa", result.loa().name());
        context.put("amr", "mobile-id");

        String redirectTo = hydra.acceptLogin(session.loginChallenge(), result.subject(), context);
        sessions.remove(sessionId);
        log.info("Нэвтрэлт амжилттай: subject={}, loa={}", result.subject(), result.loa());

        return Map.of("status", "SUCCESS", "redirectTo", redirectTo);
    }

    @SuppressWarnings("unchecked")
    private String clientName(Map<String, Object> loginRequest) {
        Object client = loginRequest.get("client");
        if (client instanceof Map<?, ?> c) {
            Object name = ((Map<String, Object>) c).get("client_name");
            if (name != null && !name.toString().isBlank()) {
                return name.toString();
            }
            Object id = ((Map<String, Object>) c).get("client_id");
            return id != null ? id.toString() : "Үл мэдэгдэх үйлчилгээ";
        }
        return "Үл мэдэгдэх үйлчилгээ";
    }
}
