package mn.gov.gerege;

/**
 * Нэвтрэлт эхлүүлэх хүсэлт — иргэний оруулсан мэдээлэл + Hydra-гийн challenge.
 *
 * @param loginChallenge Hydra-гийн login_challenge (энэ нэвтрэлтийг тодорхойлно)
 * @param personalCode   иргэний регистрийн дугаар (РД)
 * @param phoneNumber    утасны дугаар (Mobile-ID-д шаардлагатай; бусдад null байж болно)
 */
public record AuthRequest(String loginChallenge, String personalCode, String phoneNumber) {
}
