package mn.gov.gerege.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import mn.gov.gerege.AuthSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Эхлүүлсэн нэвтрэлтийн сессийг Redis-д хадгалах сан.
 *
 * <p>TARA нь сессээ Apache Ignite-д тархсан байдлаар хадгалдаг; бид Redis ашиглав.
 * Энэ нь олон instance хооронд сесс хуваалцах боломж олгохоос гадна {@link #TTL}
 * хугацаа дуусгалтаар сессийн аюулгүй байдлыг (хаягдсан нэвтрэлт автоматаар
 * устах) хангана — TARA-гийн богино сессийн зарчим.</p>
 */
@Component
public class SessionStore {

    /** Сесс хүчинтэй байх хугацаа (TARA-гийн ~300с-тэй ойролцоо). */
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final String KEY_PREFIX = "gerege:session:";

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    public SessionStore(StringRedisTemplate redis, ObjectMapper mapper) {
        this.redis = redis;
        this.mapper = mapper;
    }

    public void save(AuthSession session) {
        redis.opsForValue().set(key(session.sessionId()), toJson(session), TTL);
    }

    public AuthSession get(String sessionId) {
        String json = redis.opsForValue().get(key(sessionId));
        return json == null ? null : fromJson(json);
    }

    public void remove(String sessionId) {
        redis.delete(key(sessionId));
    }

    private String key(String sessionId) {
        return KEY_PREFIX + sessionId;
    }

    private String toJson(AuthSession session) {
        try {
            return mapper.writeValueAsString(session);
        } catch (Exception e) {
            throw new IllegalStateException("Сесс сериалчлахад алдаа гарлаа", e);
        }
    }

    private AuthSession fromJson(String json) {
        try {
            return mapper.readValue(json, AuthSession.class);
        } catch (Exception e) {
            throw new IllegalStateException("Сесс уншихад алдаа гарлаа", e);
        }
    }
}
