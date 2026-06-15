package mn.gov.dan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LevelOfAssuranceTest {

    @Test
    void mobileIdAndEidCardAreHigh() {
        assertEquals(LevelOfAssurance.HIGH, LevelOfAssurance.forMethod(AuthMethod.MOBILE_ID));
        assertEquals(LevelOfAssurance.HIGH, LevelOfAssurance.forMethod(AuthMethod.EID_CARD));
    }

    @Test
    void smartIdAndBiometricAreSubstantial() {
        assertEquals(LevelOfAssurance.SUBSTANTIAL, LevelOfAssurance.forMethod(AuthMethod.SMART_ID));
        assertEquals(LevelOfAssurance.SUBSTANTIAL, LevelOfAssurance.forMethod(AuthMethod.BIOMETRIC));
    }

    @Test
    void everyMethodHasALevel() {
        // forMethod нь бүх арга дээр null биш утга буцаах ёстой (switch бүрэн эсэх).
        for (AuthMethod method : AuthMethod.values()) {
            assertNotNull(LevelOfAssurance.forMethod(method), "LoA дутуу: " + method);
        }
    }
}
