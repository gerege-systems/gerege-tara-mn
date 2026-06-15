package mn.gov.gerege.audit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AuditLoggerTest {

    @Test
    void masksMiddleOfPersonalCode() {
        assertEquals("УУ******01", AuditLogger.mask("УУ00010101"));
    }

    @Test
    void shortOrNullCodeFullyMasked() {
        assertEquals("****", AuditLogger.mask(null));
        assertEquals("****", AuditLogger.mask("12"));
    }

    @Test
    void maskNeverContainsFullCode() {
        String code = "УБ95021545";
        assertFalse(AuditLogger.mask(code).contains("9502"), "Дунд хэсэг задрах ёсгүй");
    }
}
