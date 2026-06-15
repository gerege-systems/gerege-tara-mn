package mn.gov.gerege.audit;

import mn.gov.gerege.AuthMethod;
import mn.gov.gerege.LevelOfAssurance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Нэвтрэлтийн аудит лог.
 *
 * <p>TARA нь нэвтрэлт бүрийг (хэн, хэзээ, ямар аргаар) бүртгэдэг — Монголын Кибер
 * аюулгүй байдал болон Хүний хувийн мэдээлэл хамгаалах тухай хуулийн шаардлага.
 * Энд тусгай "gerege.audit" logger-ээр бүтэцлэгдсэн мөр гаргана; production-д үүнийг
 * өөрчлөлтгүй (append-only) хадгалах сан руу чиглүүлнэ.</p>
 *
 * <p>⚠️ Хувийн нууцлал: РД-г бүтнээр нь логлохгүй — маскална.</p>
 */
@Component
public class AuditLogger {

    private static final Logger audit = LoggerFactory.getLogger("gerege.audit");

    public void authenticationSucceeded(String personalCode, AuthMethod method, LevelOfAssurance loa) {
        audit.info("event=AUTH_SUCCESS method={} loa={} subject={}", method, loa, mask(personalCode));
    }

    public void authenticationFailed(AuthMethod method, String reason) {
        audit.info("event=AUTH_FAILED method={} reason={}", method, reason);
    }

    /** РД-г маскална: эхний 2 + сүүлийн 2 тэмдэгт (жнь "УУ******01"). */
    static String mask(String personalCode) {
        if (personalCode == null || personalCode.length() < 4) {
            return "****";
        }
        String head = personalCode.substring(0, 2);
        String tail = personalCode.substring(personalCode.length() - 2);
        return head + "******" + tail;
    }
}
