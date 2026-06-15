package mn.gov.gerege;

/**
 * Нэвтрэлтийн төлөв шалгасны үр дүн.
 *
 * <p>SUCCESS үед агуулсан мэдээлэл (subject, нэр, LoA) нь Hydra-д {@code accept login}
 * болж, цаашид id_token-ийн claim болж хувирна.</p>
 *
 * @param status     одоогийн төлөв
 * @param subject    баталгаажсан иргэний дугаар (РД) — id_token-ий "sub"-ийн эх
 * @param givenName  өөрийн нэр
 * @param familyName овог
 * @param loa        хүрсэн итгэмжлэлийн түвшин
 */
public record AuthResult(
        Status status,
        String subject,
        String givenName,
        String familyName,
        LevelOfAssurance loa) {

    public enum Status {
        /** Иргэн хараахан баталгаажуулаагүй — дахин poll хийнэ. */
        RUNNING,
        /** Амжилттай нэвтэрлээ. */
        SUCCESS,
        /** Амжилтгүй (татгалзсан, хугацаа дууссан, алдаа). */
        FAILED
    }

    /** Үргэлжилж буй (хараахан дуусаагүй) үр дүн. */
    public static AuthResult running() {
        return new AuthResult(Status.RUNNING, null, null, null, null);
    }

    /** Амжилтгүй болсон үр дүн. */
    public static AuthResult failed() {
        return new AuthResult(Status.FAILED, null, null, null, null);
    }
}
