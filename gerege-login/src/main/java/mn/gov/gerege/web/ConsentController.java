package mn.gov.gerege.web;

import mn.gov.gerege.hydra.HydraAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Зөвшөөрлийн урсгал — Hydra-гийн consent provider.
 *
 * <p>Төрийн нэгдсэн нэвтрэлтэд (first-party) зөвшөөрлийг автоматаар олгоно — TARA
 * мөн ийм байдаг. Бид login шатнаас ирсэн {@code context} (нэр, LoA)-г id_token-ий
 * claim болгож хийнэ.</p>
 */
@Controller
@RequestMapping("/auth")
public class ConsentController {

    private static final Logger log = LoggerFactory.getLogger(ConsentController.class);

    private final HydraAdminClient hydra;

    public ConsentController(HydraAdminClient hydra) {
        this.hydra = hydra;
    }

    @GetMapping("/consent")
    public String consent(@RequestParam("consent_challenge") String consentChallenge) {
        Map<String, Object> consentRequest = hydra.getConsentRequest(consentChallenge);

        List<String> requestedScopes = asStringList(consentRequest.get("requested_scope"));
        List<String> requestedAudience = asStringList(consentRequest.get("requested_access_token_audience"));
        Map<String, Object> context = asMap(consentRequest.get("context"));

        Map<String, Object> idTokenClaims = buildIdTokenClaims(requestedScopes, context);

        String redirectTo = hydra.acceptConsent(consentChallenge, requestedScopes, requestedAudience, idTokenClaims);
        log.info("Зөвшөөрөл олгов: scopes={}, claims={}", requestedScopes, idTokenClaims.keySet());

        return "redirect:" + redirectTo;
    }

    /** Шаардсан scope-ийн дагуу id_token-д орох claim-уудыг бүрдүүлэх. */
    private Map<String, Object> buildIdTokenClaims(List<String> scopes, Map<String, Object> context) {
        Map<String, Object> claims = new HashMap<>();
        // "loa" нь аль ч scope-оос үл хамааран чухал тул үргэлж нэмнэ.
        if (context.containsKey("loa")) {
            claims.put("loa", context.get("loa"));
        }
        if (context.containsKey("amr")) {
            claims.put("amr", List.of(String.valueOf(context.get("amr"))));
        }
        if (scopes.contains("profile")) {
            String given = String.valueOf(context.getOrDefault("given_name", ""));
            String family = String.valueOf(context.getOrDefault("family_name", ""));
            claims.put("given_name", given);
            claims.put("family_name", family);
            claims.put("name", (given + " " + family).trim());
        }
        return claims;
    }

    @SuppressWarnings("unchecked")
    private List<String> asStringList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }
}
