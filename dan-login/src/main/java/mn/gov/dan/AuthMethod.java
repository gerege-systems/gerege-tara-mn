package mn.gov.dan;

/**
 * ДАН дэмжих нэвтрэлтийн аргууд.
 *
 * <p>TARA-гийн аргуудаас (ID-card, Mobile-ID, Smart-ID, eIDAS) Монголд тохирохыг нь
 * авч, eIDAS-ийн оронд BIOMETRIC-ийг (Монгол нэмэлт) оруулсан.</p>
 */
public enum AuthMethod {
    /** Чипт иргэний үнэмлэх (ҮДШ) — TARA-гийн ID-card-ийн дүйцэл. */
    EID_CARD,
    /** Үүрэн операторын SIM-PKI — TARA-гийн Mobile-ID-ийн дүйцэл. ← MVP. */
    MOBILE_ID,
    /** Апп суурьтай (банк / E-Mongolia, QR/push) — TARA-гийн Smart-ID-ийн дүйцэл. */
    SMART_ID,
    /** Нүүр таниулалт / биометр — TARA-д БАЙХГҮЙ, Монголын шинэ нэмэлт. */
    BIOMETRIC
}
