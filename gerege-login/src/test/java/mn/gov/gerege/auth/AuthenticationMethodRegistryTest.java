package mn.gov.gerege.auth;

import mn.gov.gerege.AuthMethod;
import mn.gov.gerege.registry.MockHurClient;
import mn.gov.gerege.registry.PersonRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationMethodRegistryTest {

    private final PersonRegistry hur = new MockHurClient();
    private final AuthenticationMethodRegistry registry = new AuthenticationMethodRegistry(List.of(
            new MockMobileIdHandler(hur),
            new MockEidHandler(hur),
            new MockSmartIdHandler(hur),
            new MockBiometricHandler(hur)));

    @Test
    void registersAllFourMethods() {
        assertEquals(4, registry.available().size());
        for (AuthMethod method : AuthMethod.values()) {
            assertTrue(registry.available().contains(method), "Дутуу арга: " + method);
        }
    }

    @Test
    void getReturnsCorrectHandler() {
        assertEquals(AuthMethod.MOBILE_ID, registry.get(AuthMethod.MOBILE_ID).method());
        assertEquals(AuthMethod.BIOMETRIC, registry.get(AuthMethod.BIOMETRIC).method());
    }

    @Test
    void mobileIdHandlerReportsHighAssurance() {
        assertEquals("HIGH", registry.get(AuthMethod.MOBILE_ID).assuranceLevel().name());
    }
}
