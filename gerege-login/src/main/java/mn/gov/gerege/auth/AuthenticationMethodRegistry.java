package mn.gov.gerege.auth;

import mn.gov.gerege.AuthMethod;
import mn.gov.gerege.AuthenticationMethodHandler;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Бүртгэгдсэн нэвтрэлтийн аргуудын бүртгэл.
 *
 * <p>Spring нь {@link AuthenticationMethodHandler} төрлийн бүх bean-ийг автоматаар
 * энд цуглуулна. Шинэ арга нэмэх = шинэ {@code @Component} handler бичих — энэ
 * бүртгэл болон контроллерт хүрэхгүй. Энэ нь TARA-гийн өргөтгөх загвар.</p>
 */
@Component
public class AuthenticationMethodRegistry {

    private final Map<AuthMethod, AuthenticationMethodHandler> handlers = new LinkedHashMap<>();

    public AuthenticationMethodRegistry(List<AuthenticationMethodHandler> handlerBeans) {
        for (AuthenticationMethodHandler handler : handlerBeans) {
            handlers.put(handler.method(), handler);
        }
    }

    public AuthenticationMethodHandler get(AuthMethod method) {
        AuthenticationMethodHandler handler = handlers.get(method);
        if (handler == null) {
            throw new IllegalArgumentException("Дэмжигдээгүй нэвтрэлтийн арга: " + method);
        }
        return handler;
    }

    public Set<AuthMethod> available() {
        return handlers.keySet();
    }
}
