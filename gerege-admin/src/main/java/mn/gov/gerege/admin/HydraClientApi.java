package mn.gov.gerege.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Hydra-гийн OAuth2 client (RP) удирдлагын admin API клиент.
 *
 * @see <a href="https://www.ory.sh/docs/hydra/reference/api">Hydra Admin API — /admin/clients</a>
 */
@Component
public class HydraClientApi {

    private final RestClient http;

    public HydraClientApi(@Value("${gerege.hydra.admin-url}") String adminUrl) {
        this.http = RestClient.builder().baseUrl(adminUrl).build();
    }

    /** Бүртгэгдсэн бүх RP-г жагсаах. */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listClients() {
        return http.get()
                .uri(u -> u.path("/admin/clients").queryParam("page_size", 200).build())
                .retrieve()
                .body(List.class);
    }

    /** Нэг RP-г ID-аар авах. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getClient(String clientId) {
        return http.get()
                .uri("/admin/clients/{id}", clientId)
                .retrieve()
                .body(Map.class);
    }

    /**
     * Шинэ RP бүртгэх. {@code client_secret} илгээхгүй тул Hydra нэгийг үүсгэж,
     * хариунд буцаана (зөвхөн нэг удаа харагдана).
     *
     * @return үүсгэсэн client (client_secret-тэй)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> createClient(Map<String, Object> body) {
        return http.post()
                .uri("/admin/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    /** RP-г устгах. */
    public void deleteClient(String clientId) {
        http.delete()
                .uri("/admin/clients/{id}", clientId)
                .retrieve()
                .toBodilessEntity();
    }
}
