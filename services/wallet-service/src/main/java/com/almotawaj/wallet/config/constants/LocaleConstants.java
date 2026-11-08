package com.almotawaj.wallet.config.constants;

import java.util.List;
import java.util.Locale;

public final class LocaleConstants {
    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale ARABIC = Locale.forLanguageTag("ar");
    public static final List<Locale> SUPPORTED = List.of(ENGLISH, ARABIC);

    private LocaleConstants() {
    }
}
