package mn.gov.gerege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Гэрэгэ — нэвтрэлтийн үйлчилгээ (Hydra-гийн login/consent provider).
 *
 * <p>Эстонийн TARA-Login-ийн дүйцэл: Ory Hydra нь OIDC/OAuth2-г бүрэн хариуцаж,
 * нэвтрэлтийн UI болон арга (Mobile-ID гэх мэт)-ыг энэ webapp руу даатгана.</p>
 */
@SpringBootApplication
public class GeregeLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeregeLoginApplication.class, args);
    }
}
