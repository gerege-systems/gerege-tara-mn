package mn.gov.dan.web;

import mn.gov.dan.AuthMethod;
import mn.gov.dan.LevelOfAssurance;

/**
 * Нэвтрэлтийн аргыг хуудсанд харуулах загвар.
 *
 * @param labelKey i18n түлхүүр (messages_*.properties) — олон хэлэнд орчуулагдана
 * @param iconName флат SVG icon-ийн фрагмент нэр (templates/fragments/icons.html)
 */
public record MethodView(String name, String labelKey, String iconName, String loa, boolean needsPhone) {

    public static MethodView of(AuthMethod method) {
        String loa = LevelOfAssurance.forMethod(method).name();
        String key = "method." + method.name();
        return switch (method) {
            case MOBILE_ID -> new MethodView(method.name(), key, "smartphone", loa, true);
            case EID_CARD  -> new MethodView(method.name(), key, "card", loa, false);
            case SMART_ID  -> new MethodView(method.name(), key, "grid", loa, false);
            case BIOMETRIC -> new MethodView(method.name(), key, "face", loa, false);
        };
    }
}
