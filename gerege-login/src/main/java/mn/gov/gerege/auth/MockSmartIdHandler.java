package mn.gov.gerege.auth;

import mn.gov.gerege.AuthMethod;
import mn.gov.gerege.registry.PersonRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Smart-ID / Апп — баталгааны код ашигладаг, LoA SUBSTANTIAL. TARA-гийн Smart-ID-ийн дүйцэл.
 */
@Component
public class MockSmartIdHandler extends AbstractMockHandler {

    public MockSmartIdHandler(PersonRegistry registry) {
        super(registry);
    }

    @Override
    public AuthMethod method() {
        return AuthMethod.SMART_ID;
    }

    @Override
    protected Duration mockDelay() {
        return Duration.ofSeconds(3);
    }

    @Override
    protected boolean usesControlCode() {
        return true;
    }
}
