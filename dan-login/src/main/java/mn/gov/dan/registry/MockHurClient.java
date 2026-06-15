package mn.gov.dan.registry;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/**
 * ХУР (Монголын X-Road) клиентийн mock хувилбар.
 *
 * <p>Бодит системд энэ нь ХУР-ын security server-ээр дамжуулан УБЕГ-ийн иргэний
 * бүртгэлээс РД-аар нэр, төрсөн огноог татна. Одоогоор хөгжүүлэлтэд тогтмол жишиг
 * өгөгдөл буцаана — интерфейс нь хэвээр тул дараа нь зөвхөн энэ классыг бодит
 * X-Road дуудлагаар солино.</p>
 */
@Component
public class MockHurClient implements PersonRegistry {

    /** Хэдэн жишиг иргэн (демо). Бусад РД-д автоматаар нэр үүсгэнэ. */
    private static final Map<String, Person> SAMPLES = Map.of(
            "УУ00010101", new Person("УУ00010101", "Бат-Эрдэнэ", "Дорж", LocalDate.of(1990, 1, 1)),
            "УБ95021545", new Person("УБ95021545", "Сараа", "Ганболд", LocalDate.of(1995, 2, 15)),
            "ОО88112233", new Person("ОО88112233", "Тэмүүлэн", "Бат", LocalDate.of(1988, 11, 22))
    );

    @Override
    public Person lookup(String personalCode) {
        if (personalCode == null || personalCode.isBlank()) {
            return null;
        }
        Person sample = SAMPLES.get(personalCode.trim().toUpperCase());
        if (sample != null) {
            return sample;
        }
        // Тодорхойлогдоогүй РД-д тогтвортой mock нэр (демо зориулалт).
        return new Person(personalCode.trim().toUpperCase(), "Иргэн", "ДАН", LocalDate.of(2000, 1, 1));
    }
}
