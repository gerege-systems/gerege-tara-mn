package mn.gov.dan.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * Нүүр хуудас + ДЕМО нэвтрэлтийн урсгал.
 *
 * <p>{@code /} нь нэвтрэлтийн endpoint биш тул найрсаг танилцуулга хуудас харуулна.
 * {@code /demo/start} → {@code /demo/callback} нь жижиг демо үйлчилгээ (RP) болж
 * бүрэн OIDC урсгалыг үзүүлнэ — иргэн нэвтрээд өөрийн id_token-ий мэдээллийг харна.</p>
 *
 * <p>⚠️ Демо хэсэг нь зөвхөн үзүүлбэрийн зориулалттай; production-д хасна.</p>
 */
@Controller
public class HomeController {

    private final String issuerPublic;
    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final ObjectMapper mapper;
    private final RestClient http = RestClient.create();

    public HomeController(
            @Value("${dan.demo.issuer-public}") String issuerPublic,
            @Value("${dan.demo.token-url}") String tokenUrl,
            @Value("${dan.demo.client-id}") String clientId,
            @Value("${dan.demo.client-secret}") String clientSecret,
            @Value("${dan.demo.redirect-uri}") String redirectUri,
            ObjectMapper mapper) {
        this.issuerPublic = issuerPublic;
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.mapper = mapper;
    }

    /** Нүүр хуудас. */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /** Демо нэвтрэлт эхлүүлэх — браузерыг Hydra-гийн authorize руу чиглүүлнэ. */
    @GetMapping("/demo/start")
    public String demoStart(HttpSession session) {
        String state = UUID.randomUUID().toString().replace("-", "");
        String nonce = UUID.randomUUID().toString().replace("-", "");
        // CSRF хамгаалалт: state-ийг сесст хадгалж, callback дээр тулгана.
        session.setAttribute("demo_oauth_state", state);
        String authorizeUrl = UriComponentsBuilder.fromHttpUrl(issuerPublic + "/oauth2/auth")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .queryParam("nonce", nonce)
                .encode()
                .build()
                .toUriString();
        return "redirect:" + authorizeUrl;
    }

    /** Демо callback — кодыг токен болгож солиод id_token-ий мэдээллийг харуулна. */
    @GetMapping("/demo/callback")
    public String demoCallback(@RequestParam(value = "code", required = false) String code,
                               @RequestParam(value = "state", required = false) String state,
                               @RequestParam(value = "error", required = false) String error,
                               HttpSession session,
                               Model model) {
        if (error != null || code == null) {
            model.addAttribute("error", error != null ? error : "no-code");
            model.addAttribute("description", "Нэвтрэлт цуцлагдсан эсвэл код ирээгүй.");
            return "error";
        }

        // CSRF шалгалт: callback-ийн state нь эхлүүлэхэд хадгалсантай тохирох ёстой.
        Object expectedState = session.getAttribute("demo_oauth_state");
        session.removeAttribute("demo_oauth_state");   // нэг удаагийн — дахин ашиглахгүй
        if (expectedState == null || !expectedState.equals(state)) {
            model.addAttribute("error", "invalid_state");
            model.addAttribute("description", "State тохирохгүй байна (CSRF хамгаалалт).");
            return "error";
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", redirectUri);

        @SuppressWarnings("unchecked")
        Map<String, Object> token = http.post()
                .uri(tokenUrl + "/oauth2/token")
                .headers(h -> h.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        Map<String, Object> claims = decodeJwtClaims((String) token.get("id_token"));
        model.addAttribute("claims", claims);
        return "demo-result";
    }

    /** id_token (JWT)-ий payload хэсгийг задлан claim-уудыг авах. */
    private Map<String, Object> decodeJwtClaims(String idToken) {
        try {
            String payload = idToken.split("\\.")[1];
            byte[] json = Base64.getUrlDecoder().decode(payload);
            return mapper.readValue(new String(json, StandardCharsets.UTF_8), Map.class);
        } catch (Exception e) {
            return Map.of("error", "id_token задлахад алдаа гарлаа");
        }
    }
}
