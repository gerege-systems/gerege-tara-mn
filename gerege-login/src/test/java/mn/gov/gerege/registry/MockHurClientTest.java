package mn.gov.gerege.registry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MockHurClientTest {

    private final PersonRegistry registry = new MockHurClient();

    @Test
    void returnsKnownPerson() {
        Person p = registry.lookup("УУ00010101");
        assertNotNull(p);
        assertEquals("Бат-Эрдэнэ", p.givenName());
        assertEquals("Дорж", p.familyName());
    }

    @Test
    void unknownCodeGetsFallbackPerson() {
        Person p = registry.lookup("XX99999999");
        assertNotNull(p);
        assertEquals("XX99999999", p.personalCode());
    }

    @Test
    void blankOrNullReturnsNull() {
        assertNull(registry.lookup(""));
        assertNull(registry.lookup(null));
    }

    @Test
    void lookupIsCaseInsensitive() {
        Person p = registry.lookup("уу00010101");
        assertNotNull(p);
        assertEquals("Бат-Эрдэнэ", p.givenName());
    }
}
