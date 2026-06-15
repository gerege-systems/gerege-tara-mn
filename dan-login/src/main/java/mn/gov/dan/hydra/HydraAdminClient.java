package mn.gov.dan.hydra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Ory Hydra-гийн ADMIN API (default :4445) клиент.
 *
 * <p>Энэ нь TARA-Login → Hydra харилцааны цөм. Hydra "хэн нэвтэрснийг би мэдэхгүй"
 * гээд бидэнд login/consent challenge өгдөг; бид иргэнийг таниад энд буцааж
 * "accept" гэж хэлнэ. Hydra хариуд redirect_to URL өгөх ба бид иргэнийг тийш чиглүүлнэ.</p>
 *
 * <p>⚠️ Admin портыг production-д интернетэд ХЭЗЭЭ Ч нээж болохгүй.</p>
 *
 * @see <a href="https://www.ory.sh/docs/hydra/reference/api">Hydra Admin API</a>
 */
@Component
public class HydraAdminClient {

    private final RestClient http;

    public HydraAdminClient(@Value("${dan.hydra.admin-url}") String adminUrl) {
        this.http = RestClient.builder().baseUrl(adminUrl).build();
    }

    // ─── LOGIN ──────────────────────────────────────────────────────────────

    /** login_challenge-ийн дэлгэрэнгүйг авах (хүсэгч үйлчилгээ, шаардсан scope г.м.). */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getLoginRequest(String loginChallenge) {
        return http.get()
                .uri(u -> u.path("/admin/oauth2/auth/requests/login")
                        .queryParam("login_challenge", loginChallenge).build())
                .retrieve()
                .body(Map.class);
    }

    /**
     * Нэвтрэлтийг зөвшөөрөх. {@code context}-д иргэний мэдээллийг (нэр, LoA) хийж
     * өгснөөр Hydra түүнийг consent шатанд буцааж дамжуулна.
     *
     * @return иргэнийг чиглүүлэх redirect_to URL
     */
    @SuppressWarnings("unchecked")
    public String acceptLogin(String loginChallenge, String subject, Map<String, Object> context) {
        Map<String, Object> resp = http.put()
                .uri(u -> u.path("/admin/oauth2/auth/requests/login/accept")
                        .queryParam("login_challenge", loginChallenge).build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "subject", subject,
                        "context", context,
                        "remember", false,
                        "remember_for", 0))
                .retrieve()
                .body(Map.class);
        return (String) resp.get("redirect_to");
    }

    // ─── CONSENT ────────────────────────────────────────────────────────────

    /** consent_challenge-ийн дэлгэрэнгүйг авах (шаардсан scope, audience, context). */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getConsentRequest(String consentChallenge) {
        return http.get()
                .uri(u -> u.path("/admin/oauth2/auth/requests/consent")
                        .queryParam("consent_challenge", consentChallenge).build())
                .retrieve()
                .body(Map.class);
    }

    /**
     * Зөвшөөрлийг олгох. {@code idTokenClaims} нь id_token-д орох нэмэлт claim
     * (given_name, family_name, loa г.м.).
     *
     * @return иргэнийг буцааж RP руу чиглүүлэх redirect_to URL
     */
    @SuppressWarnings("unchecked")
    public String acceptConsent(String consentChallenge,
                                List<String> grantScope,
                                List<String> grantAudience,
                                Map<String, Object> idTokenClaims) {
        Map<String, Object> resp = http.put()
                .uri(u -> u.path("/admin/oauth2/auth/requests/consent/accept")
                        .queryParam("consent_challenge", consentChallenge).build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "grant_scope", grantScope,
                        "grant_access_token_audience", grantAudience == null ? List.of() : grantAudience,
                        "session", Map.of("id_token", idTokenClaims),
                        "remember", false,
                        "remember_for", 0))
                .retrieve()
                .body(Map.class);
        return (String) resp.get("redirect_to");
    }

    // ─── LOGOUT ─────────────────────────────────────────────────────────────

    /** logout_challenge-ийг зөвшөөрч, redirect_to URL-ийг буцаах. */
    @SuppressWarnings("unchecked")
    public String acceptLogout(String logoutChallenge) {
        Map<String, Object> resp = http.put()
                .uri(u -> u.path("/admin/oauth2/auth/requests/logout/accept")
                        .queryParam("logout_challenge", logoutChallenge).build())
                .retrieve()
                .body(Map.class);
        return (String) resp.get("redirect_to");
    }
}
