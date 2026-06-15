package mn.gov.gerege.auth;

import mn.gov.gerege.AuthMethod;
import mn.gov.gerege.registry.PersonRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Нүүр таниулалт / биометр — баталгааны кодгүй, LoA SUBSTANTIAL.
 * TARA-д БАЙХГҮЙ, Монголын шинэ нэмэлт (ХУР/ДАН биометр сан).
 */
@Component
public class MockBiometricHandler extends AbstractMockHandler {

    public MockBiometricHandler(PersonRegistry registry) {
        super(registry);
    }

    @Override
    public AuthMethod method() {
        return AuthMethod.BIOMETRIC;
    }

    @Override
    protected Duration mockDelay() {
        return Duration.ofSeconds(2);
    }
}
