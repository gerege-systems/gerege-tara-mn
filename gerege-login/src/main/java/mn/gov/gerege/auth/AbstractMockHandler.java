package mn.gov.gerege.auth;

import mn.gov.gerege.AuthRequest;
import mn.gov.gerege.AuthResult;
import mn.gov.gerege.AuthSession;
import mn.gov.gerege.AuthenticationMethodHandler;
import mn.gov.gerege.LevelOfAssurance;
import mn.gov.gerege.registry.Person;
import mn.gov.gerege.registry.PersonRegistry;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Mock нэвтрэлтийн аргуудын нийтлэг суурь.
 *
 * <p>Бодит арга бүр (Mobile-ID, eID, Smart-ID, биометр) async урсгалтай: эхлүүлээд
 * (initiate) иргэн төхөөрөмж дээрээ баталгаажуулна, энэ хооронд төлвийг шалгана (poll).
 * Энэ суурь тэр хэлбэрийг загварчилж, дэд классууд зөвхөн саатал, баталгааны код
 * ашиглах эсэх зэргээ тодорхойлно. Иргэний нэрийг {@link PersonRegistry} (ХУР)-аас
 * татна — нэвтрэлт ба иргэний мэдээллийн ялгааг хадгална.</p>
 */
public abstract class AbstractMockHandler implements AuthenticationMethodHandler {

    private final PersonRegistry registry;

    protected AbstractMockHandler(PersonRegistry registry) {
        this.registry = registry;
    }

    /** Mock баталгаажуулалт хэр удаан "болох" вэ. */
    protected abstract Duration mockDelay();

    /** Энэ арга баталгааны код (control code) ашигладаг уу (жнь Mobile-ID тийм, eID үгүй). */
    protected boolean usesControlCode() {
        return false;
    }

    @Override
    public LevelOfAssurance assuranceLevel() {
        return LevelOfAssurance.forMethod(method());
    }

    @Override
    public AuthSession initiate(AuthRequest request) {
        String sessionId = UUID.randomUUID().toString();
        String controlCode = usesControlCode() ? controlCodeFor(sessionId) : null;
        return new AuthSession(
                sessionId,
                method(),
                request.loginChallenge(),
                request.personalCode(),
                controlCode,
                Instant.now());
    }

    @Override
    public AuthResult poll(AuthSession session) {
        boolean stillWaiting = Duration.between(session.startedAt(), Instant.now())
                .compareTo(mockDelay()) < 0;
        if (stillWaiting) {
            return AuthResult.running();
        }
        Person person = registry.lookup(session.personalCode());
        if (person == null) {
            return AuthResult.failed();
        }
        return new AuthResult(
                AuthResult.Status.SUCCESS,
                person.personalCode(),
                person.givenName(),
                person.familyName(),
                assuranceLevel());
    }

    /** Баталгааны 4 оронтой кодыг сессээс тогтвортойгоор гаргах (демо). */
    private String controlCodeFor(String sessionId) {
        return String.format("%04d", Math.abs(sessionId.hashCode()) % 10000);
    }
}
