package mn.gov.gerege.web;

import mn.gov.gerege.AuthSession;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Эхлүүлсэн нэвтрэлтийн сессийг HTTP хүсэлтүүдийн хооронд хадгалах энгийн санах ой.
 *
 * <p>MVP-д энгийн in-memory map. Production-д энэ нь Redis (эсвэл TARA шиг Apache
 * Ignite) болж, олон instance хооронд тархсан байх ёстой.</p>
 */
@Component
public class SessionStore {

    private final Map<String, AuthSession> sessions = new ConcurrentHashMap<>();

    public void save(AuthSession session) {
        sessions.put(session.sessionId(), session);
    }

    public AuthSession get(String sessionId) {
        return sessions.get(sessionId);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }
}
