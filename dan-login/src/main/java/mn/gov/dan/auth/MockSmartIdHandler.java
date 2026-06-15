package mn.gov.dan.auth;

import mn.gov.dan.AuthMethod;
import mn.gov.dan.registry.PersonRegistry;
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
