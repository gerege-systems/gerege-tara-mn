package mn.gov.gerege;

import java.time.Instant;

/**
 * Эхлүүлсэн нэвтрэлтийн сесс — initiate() үүсгэж, poll() шалгана.
 *
 * <p>Mobile-ID шиг async аргуудад: иргэн утсан дээрээ {@code controlCode}-ыг хараад
 * баталгаажуулна; энэ хооронд webapp нь poll()-оор төлвийг шалгаж байдаг.</p>
 *
 * @param sessionId      сессийн дотоод дугаар
 * @param method         нэвтрэлтийн арга
 * @param loginChallenge Hydra-д буцаах login_challenge
 * @param personalCode   иргэний РД
 * @param controlCode    баталгааны код (иргэн утсан дээрхтэй тулгана)
 * @param startedAt      эхэлсэн агшин (mock саатлыг тооцоход ашиглана)
 */
public record AuthSession(
        String sessionId,
        AuthMethod method,
        String loginChallenge,
        String personalCode,
        String controlCode,
        Instant startedAt) {
}
