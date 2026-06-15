package mn.gov.gerege.admin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * RP бүртгэлийн формыг Hydra-гийн client body болгон хөрвүүлэх цэвэр функцууд.
 * (Контроллероос тусгаарласан нь нэгж тест бичихэд хялбар.)
 */
final class ClientForms {

    private ClientForms() {
    }

    /**
     * Бүртгэлийн формоос Hydra /admin/clients body үүсгэх.
     *
     * @param clientId      үйлчилгээний таних нэр (slug)
     * @param clientName    харагдах нэр
     * @param redirectRaw   redirect URI-ууд (мөр болгонд нэг)
     * @param profileScope  "profile" scope нэмэх эсэх ("openid" үргэлж байна)
     */
    static Map<String, Object> toCreateBody(String clientId, String clientName,
                                            String redirectRaw, boolean profileScope) {
        List<String> redirectUris = parseLines(redirectRaw);
        String scope = profileScope ? "openid profile" : "openid";

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("client_id", clientId.trim());
        body.put("client_name", clientName.trim());
        body.put("grant_types", List.of("authorization_code", "refresh_token"));
        body.put("response_types", List.of("code"));
        body.put("scope", scope);
        body.put("redirect_uris", redirectUris);
        body.put("token_endpoint_auth_method", "client_secret_basic");
        return body;
    }

    /** Мөр бүрийг тусад нь салгаж, хоосон болон давхардлыг арилгах. */
    static List<String> parseLines(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null) {
            return result;
        }
        for (String line : raw.split("[\\r\\n]+")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !result.contains(trimmed)) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
