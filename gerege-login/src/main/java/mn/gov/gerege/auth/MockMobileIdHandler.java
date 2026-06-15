package mn.gov.gerege.auth;

import mn.gov.gerege.AuthMethod;
import mn.gov.gerege.AuthRequest;
import mn.gov.gerege.AuthResult;
import mn.gov.gerege.AuthSession;
import mn.gov.gerege.AuthenticationMethodHandler;
import mn.gov.gerege.LevelOfAssurance;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Mock Mobile-ID handler — бодит оператор холбохоос өмнөх хөгжүүлэлтийн загвар.
 *
 * <p>Эстонийн TARA нь хөгжүүлэлтэд TARA-Mock ашигладаг. Энэ нь яг тэр зорилготой:
 * бодит SK/оператор үйлчилгээгүйгээр бүх OIDC урсгалыг туршина.</p>
 *
 * <p>Бодит Mobile-ID нь async: {@link #initiate} утсанд push илгээж, иргэн утсан
 * дээрээ баталгааны кодыг хараад зөвшөөрнө; энэ хооронд {@link #poll} төлвийг шалгана.
 * Манай mock яг ижил хэлбэрийг дуурайж, {@value #MOCK_DELAY_SECONDS} секундын дараа
 * амжилттай гэж буцаана — тиймээс бодит оператор холбоход зөвхөн энэ классын дотрыг
 * солих бөгөөд урсгал, контроллерууд хэвээр үлдэнэ.</p>
 */
@Component
public class MockMobileIdHandler implements AuthenticationMethodHandler {

    private static final long MOCK_DELAY_SECONDS = 3;

    @Override
    public AuthMethod method() {
        return AuthMethod.MOBILE_ID;
    }

    @Override
    public LevelOfAssurance assuranceLevel() {
        return LevelOfAssurance.forMethod(AuthMethod.MOBILE_ID);
    }

    @Override
    public AuthSession initiate(AuthRequest request) {
        String sessionId = UUID.randomUUID().toString();
        String controlCode = controlCodeFor(sessionId);
        return new AuthSession(
                sessionId,
                AuthMethod.MOBILE_ID,
                request.loginChallenge(),
                request.personalCode(),
                controlCode,
                Instant.now());
    }

    @Override
    public AuthResult poll(AuthSession session) {
        boolean stillWaiting = Duration.between(session.startedAt(), Instant.now())
                .compareTo(Duration.ofSeconds(MOCK_DELAY_SECONDS)) < 0;
        if (stillWaiting) {
            return AuthResult.running();
        }
        // Mock амжилт: бодит системд энэ мэдээлэл операторын гэрчилгээ + ХУР-аас ирнэ.
        return new AuthResult(
                AuthResult.Status.SUCCESS,
                session.personalCode(),
                mockGivenName(session.personalCode()),
                mockFamilyName(session.personalCode()),
                assuranceLevel());
    }

    /** Баталгааны 4 оронтой кодыг сессээс тогтвортойгоор гаргах (демо зориулалт). */
    private String controlCodeFor(String sessionId) {
        int code = Math.abs(sessionId.hashCode()) % 10000;
        return String.format("%04d", code);
    }

    private String mockGivenName(String personalCode) {
        return "Бат-Эрдэнэ";
    }

    private String mockFamilyName(String personalCode) {
        return "Дорж";
    }
}
