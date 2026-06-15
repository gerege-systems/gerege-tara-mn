package mn.gov.gerege.registry;

import java.time.LocalDate;

/**
 * Иргэний бүртгэлийн мэдээлэл (ХУР/УБЕГ-аас ирэх).
 *
 * @param personalCode регистрийн дугаар (РД)
 * @param givenName    өөрийн нэр
 * @param familyName   овог
 * @param birthDate    төрсөн огноо
 */
public record Person(String personalCode, String givenName, String familyName, LocalDate birthDate) {
}
