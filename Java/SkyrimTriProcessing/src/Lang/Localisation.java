package Lang;

import java.util.Locale;

public class Localisation {
    public static Strings getStrings() {
        String lang = Locale.getDefault().getLanguage();

        if (lang.equals("en")) {
            return new StringsEN();
        }

        return new StringsEN(); // fallback
    }
}