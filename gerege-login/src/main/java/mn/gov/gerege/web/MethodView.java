package mn.gov.gerege.web;

import mn.gov.gerege.AuthMethod;
import mn.gov.gerege.LevelOfAssurance;

/**
 * Нэвтрэлтийн аргыг хуудсанд харуулах загвар (нэр, дүрс, LoA).
 */
public record MethodView(String name, String label, String icon, String loa, boolean needsPhone) {

    public static MethodView of(AuthMethod method) {
        String loa = LevelOfAssurance.forMethod(method).name();
        return switch (method) {
            case MOBILE_ID -> new MethodView(method.name(), "Mobile-ID", "📱", loa, true);
            case EID_CARD  -> new MethodView(method.name(), "e-Иргэний үнэмлэх", "🪪", loa, false);
            case SMART_ID  -> new MethodView(method.name(), "Smart-ID / Апп", "📲", loa, false);
            case BIOMETRIC -> new MethodView(method.name(), "Нүүр таниулалт", "🙂", loa, false);
        };
    }
}
