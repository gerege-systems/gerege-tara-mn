package mn.gov.dan.web;

import mn.gov.dan.AuthMethod;
import mn.gov.dan.AuthRequest;
import mn.gov.dan.AuthResult;
import mn.gov.dan.AuthSession;
import mn.gov.dan.AuthenticationMethodHandler;
import mn.gov.dan.audit.AuditLogger;
import mn.gov.dan.auth.AuthenticationMethodRegistry;
import mn.gov.dan.hydra.HydraAdminClient;
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
import java.util.List;
import java.util.Map;

/**
 * Нэвтрэлтийн урсгал — Hydra-гийн login provider (бүх аргад ерөнхий).
 *
 * <ol>
 *   <li>GET /auth/login?login_challenge=… → арга сонгох хуудас</li>
 *   <li>POST /auth/login → сонгосон аргыг эхлүүлж, баталгаажуулалтын хуудас</li>
 *   <li>GET /auth/login/poll → JS-ээс төлөв шалгах; SUCCESS бол accept login</li>
 * </ol>
 */
@Controller
@RequestMapping("/auth")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    /** Табын харагдах эрэмбэ (Mobile-ID эхэндээ, default). */
    private static final List<AuthMethod> DISPLAY_ORDER = List.of(
            AuthMethod.MOBILE_ID, AuthMethod.EID_CARD, AuthMethod.SMART_ID, AuthMethod.BIOMETRIC);

    private final HydraAdminClient hydra;
    private final AuthenticationMethodRegistry methods;
    private final SessionStore sessions;
    private final AuditLogger audit;

    public LoginController(HydraAdminClient hydra, AuthenticationMethodRegistry methods,
                           SessionStore sessions, AuditLogger audit) {
        this.hydra = hydra;
        this.methods = methods;
        this.sessions = sessions;
        this.audit = audit;
    }

    /** 1-р алхам: боломжит аргуудтай нэвтрэх хуудсыг харуулах. */
    @GetMapping("/login")
    public String showLogin(@RequestParam("login_challenge") String loginChallenge, Model model) {
        Map<String, Object> loginRequest = hydra.getLoginRequest(loginChallenge);
        String clientName = clientName(loginRequest);
        log.info("Login challenge — хүсэгч үйлчилгээ: {}", clientName);

        List<MethodView> methodViews = methods.available().stream()
                .sorted(java.util.Comparator.comparingInt(DISPLAY_ORDER::indexOf))
                .map(MethodView::of)
                .toList();

        model.addAttribute("loginChallenge", loginChallenge);
        model.addAttribute("clientName", clientName);
        model.addAttribute("methods", methodViews);
        return "login";
    }

    /** 2-р алхам: сонгосон аргаар нэвтрэлт эхлүүлж, баталгаажуулалтын хуудас руу. */
    @PostMapping("/login")
    public String start(@RequestParam("login_challenge") String loginChallenge,
                        @RequestParam("method") String methodName,
                        @RequestParam("personalCode") String personalCode,
                        @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                        Model model) {
        AuthMethod method = AuthMethod.valueOf(methodName);
        AuthenticationMethodHandler handler = methods.get(method);

        AuthSession session = handler.initiate(new AuthRequest(loginChallenge, personalCode, phoneNumber));
        sessions.save(session);
        log.info("{} эхэллээ: sessionId={}, controlCode={}", method, session.sessionId(), session.controlCode());

        MethodView view = MethodView.of(method);
        model.addAttribute("sessionId", session.sessionId());
        model.addAttribute("controlCode", session.controlCode());
        model.addAttribute("methodLabelKey", view.labelKey());
        model.addAttribute("methodIconName", view.iconName());
        return "verify";
    }

    /** 3-р алхам: JS-ээс дуудах poll endpoint. JSON буцаана. */
    @GetMapping("/login/poll")
    @ResponseBody
    public Map<String, String> poll(@RequestParam("sessionId") String sessionId) {
        AuthSession session = sessions.get(sessionId);
        if (session == null) {
            return Map.of("status", "FAILED", "reason", "session-not-found");
        }

        AuthenticationMethodHandler handler = methods.get(session.method());
        AuthResult result = handler.poll(session);

        if (result.status() == AuthResult.Status.RUNNING) {
            return Map.of("status", "RUNNING");
        }
        if (result.status() == AuthResult.Status.FAILED) {
            sessions.remove(sessionId);
            audit.authenticationFailed(session.method(), "registry-or-verification-failed");
            return Map.of("status", "FAILED");
        }

        // SUCCESS — иргэний мэдээллийг context-оор consent шат руу дамжуулна.
        // Тэмдэглэл: "amr" нь OIDC-ийн хамгаалагдсан claim тул Hydra кастом утгыг
        // шүүж хаядаг. Иймд аргын нэрийг кастом "auth_method" claim-аар дамжуулна.
        String authMethod = session.method().name().toLowerCase().replace('_', '-');
        Map<String, Object> context = new HashMap<>();
        context.put("given_name", result.givenName());
        context.put("family_name", result.familyName());
        context.put("loa", result.loa().name());
        context.put("auth_method", authMethod);

        String redirectTo = hydra.acceptLogin(session.loginChallenge(), result.subject(), context);
        sessions.remove(sessionId);
        audit.authenticationSucceeded(result.subject(), session.method(), result.loa());
        log.info("Нэвтрэлт амжилттай: method={}, loa={}", session.method(), result.loa());

        return Map.of("status", "SUCCESS", "redirectTo", redirectTo);
    }

    @SuppressWarnings("unchecked")
    private String clientName(Map<String, Object> loginRequest) {
        Object client = loginRequest.get("client");
        if (client instanceof Map<?, ?> c) {
            Map<String, Object> cm = (Map<String, Object>) c;
            Object name = cm.get("client_name");
            if (name != null && !name.toString().isBlank()) {
                return name.toString();
            }
            Object id = cm.get("client_id");
            return id != null ? id.toString() : "Үл мэдэгдэх үйлчилгээ";
        }
        return "Үл мэдэгдэх үйлчилгээ";
    }
}
