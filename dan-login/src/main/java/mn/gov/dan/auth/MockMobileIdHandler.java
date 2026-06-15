package mn.gov.dan.auth;

import mn.gov.dan.AuthMethod;
import mn.gov.dan.registry.PersonRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Mobile-ID (SIM-PKI) — баталгааны код ашигладаг, LoA HIGH. TARA-гийн Mobile-ID-ийн дүйцэл.
 */
@Component
public class MockMobileIdHandler extends AbstractMockHandler {

    public MockMobileIdHandler(PersonRegistry registry) {
        super(registry);
    }

    @Override
    public AuthMethod method() {
        return AuthMethod.MOBILE_ID;
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
