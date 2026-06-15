package mn.gov.dan;

/**
 * Итгэмжлэлийн түвшин (Level of Assurance, LoA).
 *
 * <p>Эстонийн TARA нь eIDAS стандартын LOW / SUBSTANTIAL / HIGH түвшнийг ашигладаг.
 * Энэ түвшин нь "энэ нэвтрэлт хэр найдвартай вэ" гэдгийг илэрхийлдэг ба үйлчилгээ
 * (RP) бүр өөрийн шаардлагын дагуу "доод тал нь SUBSTANTIAL" гэх мэт шаардаж болно.</p>
 *
 * <p>Жишээ: банкны гүйлгээ HIGH шаардана; зүгээр мэдээлэл харах LOW хангалттай.</p>
 *
 * @see <a href="https://e-gov.github.io/TARA-Doku/TechnicalSpecification">Эх загвар: TARA техникийн тодорхойлолт</a>
 */
public enum LevelOfAssurance {

    /** Бага — баталгаажуулалт сул (жнь зөвхөн нэр/нууц үг). eIDAS "low". */
    LOW,

    /** Дунд — нэмэлт хүчин зүйлтэй, гэхдээ хатуу криптограф биш. eIDAS "substantial". */
    SUBSTANTIAL,

    /** Өндөр — хатуу криптограф (SIM-PKI, чипт карт + PIN). eIDAS "high". */
    HIGH;

    /**
     * Нэвтрэлтийн арга бүрд итгэмжлэлийн түвшин оноох.
     *
     * <p>Энэ нь аюулгүй байдлын бодлогын шийдвэр. TARA-гийн загварыг дагав:</p>
     * <ul>
     *   <li><b>EID_CARD</b> — физик чип + PIN → HIGH (TARA-д ID-card = high)</li>
     *   <li><b>MOBILE_ID</b> — хувийн түлхүүр SIM чипд, PIN-тэй → HIGH (TARA-д Mobile-ID = high)</li>
     *   <li><b>SMART_ID</b> — апп суурьтай; төхөөрөмж тулгуурласан тул → SUBSTANTIAL
     *       (зөвшөөрөгдсөн "qualified" хувилбарт HIGH болгож өргөтгөж болно)</li>
     *   <li><b>BIOMETRIC</b> — нүүр таниулалт дангаараа, хуурамчлах эрсдэлтэй тул → SUBSTANTIAL.
     *       Өөр хүчин зүйлтэй (multi-factor) хослуулбал HIGH болгож болно. TARA-д байхгүй,
     *       Монголын бодлогоор тодорхойлно.</li>
     * </ul>
     *
     * @param method нэвтрэлтийн арга
     * @return тухайн арга өгөх итгэмжлэлийн түвшин
     */
    public static LevelOfAssurance forMethod(AuthMethod method) {
        return switch (method) {
            case EID_CARD, MOBILE_ID -> HIGH;
            case SMART_ID            -> SUBSTANTIAL;
            case BIOMETRIC           -> SUBSTANTIAL;
        };
    }
}
