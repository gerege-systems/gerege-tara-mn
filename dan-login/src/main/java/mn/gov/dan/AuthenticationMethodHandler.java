package mn.gov.dan;

/**
 * ДАН — Нэвтрэлтийн аргын нэгдсэн гэрээ (contract).
 *
 * <p>Эстонийн TARA-Login-ийн загвараар: нэвтрэлтийн арга бүр (Mobile-ID, eID карт,
 * Smart-ID, биометр) энэ интерфейсийг хэрэгжүүлнэ. Ингэснээр ДАН-гийн цөм (Hydra
 * login/consent урсгал) нь аргын дотоод нарийн ширийнийг мэдэлгүйгээр бүх аргатай
 * нэг ижил байдлаар харьцана. Шинэ арга нэмэх = шинэ адаптер бичих, цөмд хүрэхгүй.</p>
 *
 * @see <a href="https://github.com/e-gov/TARA-Login">Эх загвар: e-gov/TARA-Login (MIT)</a>
 */
public interface AuthenticationMethodHandler {

    /** Энэ адаптер ямар аргыг хэрэгжүүлж байгаа. */
    AuthMethod method();

    /** Энэ арга ямар итгэмжлэлийн түвшин (LoA) олгох вэ. */
    LevelOfAssurance assuranceLevel();

    /**
     * Нэвтрэлт эхлүүлэх: жнь иргэнд QR/баталгааны код харуулах, эсвэл оператор/ХУР
     * руу хүсэлт илгээх. Хариу нь polling/callback-д ашиглах сессийг буцаана.
     */
    AuthSession initiate(AuthRequest request);

    /**
     * Нэвтрэлтийн төлөв шалгах. Амжилттай бол баталгаажсан иргэний мэдээллийг
     * (РД, нэр, LoA) агуулсан үр дүнг буцаана — энэ нь Hydra-д {@code accept login}
     * болж хувирна.
     */
    AuthResult poll(AuthSession session);
}
