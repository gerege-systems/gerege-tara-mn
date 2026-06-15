package mn.gov.dan.admin;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientFormsTest {

    @Test
    void parsesMultipleRedirectUrisAndTrims() {
        List<String> uris = ClientForms.parseLines("  https://a.mn/cb \n https://b.mn/cb \n");
        assertEquals(List.of("https://a.mn/cb", "https://b.mn/cb"), uris);
    }

    @Test
    void dropsBlankAndDuplicateUris() {
        List<String> uris = ClientForms.parseLines("https://a.mn/cb\n\nhttps://a.mn/cb\n");
        assertEquals(List.of("https://a.mn/cb"), uris);
    }

    @Test
    void buildsBodyWithOpenidProfileScope() {
        Map<String, Object> body = ClientForms.toCreateBody("e-mongolia", "Е-Монгол", "https://x.mn/cb", true);
        assertEquals("e-mongolia", body.get("client_id"));
        assertEquals("openid profile", body.get("scope"));
        assertEquals(List.of("authorization_code", "refresh_token"), body.get("grant_types"));
        assertTrue(((List<?>) body.get("redirect_uris")).contains("https://x.mn/cb"));
    }

    @Test
    void openidOnlyWhenProfileNotSelected() {
        Map<String, Object> body = ClientForms.toCreateBody("svc", "Үйлчилгээ", "https://x.mn/cb", false);
        assertEquals("openid", body.get("scope"));
    }
}
