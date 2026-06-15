package mn.gov.gerege.auth;

import mn.gov.gerege.AuthMethod;
import mn.gov.gerege.registry.PersonRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * e-Иргэний үнэмлэх (чипт карт + PIN) — баталгааны кодгүй, LoA HIGH.
 * TARA-гийн ID-card-ийн дүйцэл.
 */
@Component
public class MockEidHandler extends AbstractMockHandler {

    public MockEidHandler(PersonRegistry registry) {
        super(registry);
    }

    @Override
    public AuthMethod method() {
        return AuthMethod.EID_CARD;
    }

    @Override
    protected Duration mockDelay() {
        return Duration.ofSeconds(1);
    }
}
