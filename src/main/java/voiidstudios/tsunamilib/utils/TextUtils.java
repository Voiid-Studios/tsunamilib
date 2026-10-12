package voiidstudios.tsunamilib.utils;

public final class TextUtils {
    private TextUtils() {}
    
    public static boolean isBlank(String text) {
        return text.codePoints().allMatch(Character::isWhitespace);
    }

    public static String toLegacy(String message) {
        return message.replace("&", "\u00A7");
    }
}
