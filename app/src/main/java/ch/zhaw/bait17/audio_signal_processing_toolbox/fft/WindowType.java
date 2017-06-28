package ch.zhaw.bait17.audio_signal_processing_toolbox.fft;

import android.support.annotation.Nullable;

/**
 * Window types supported by the application.
 *
 * @author georgrem, stockan1
 */

public enum WindowType {
    RECTANGULAR("Rectangular", 0), TRIANGULAR("Triangular", 1), HAMMING("Hamming", 2),
    HANN("Hann", 3), BLACKMAN("Blackman", 4), NUTTAL("Nuttal", 5), WELCH("Welch", 6),
    BARTLETT("Bartlett", 7), BARTLETT_HANN("Bartlett-Hann", 8),
    BLACKMAN_HARRIS("Blackman-Harris", 9), BLACKMAN_NUTTAL("Blackman-Nuttal", 10);

    private String stringValue = "";
    private int intValue = 0;

    WindowType(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int getValue() {
        return intValue;
    }

    /**
     * Looks up the text value of {@code WindowType}.
     * If no window type can be found,
     *
     * @param text      text value of {@code WindowType}
     * @return          the {@code WindowType} that corresponds to {@code text}
     */
    @Nullable
    public static WindowType fromString(String text) {
        for (WindowType windowType : WindowType.values()) {
            if (windowType.toString().equalsIgnoreCase(text)) {
                return windowType;
            }
        }
        return null;
    }

    /**
     * Looks up the integer value of {@code WindowType}.
     * If no window type can be found,
     *
     * @param value     text value of {@code WindowType}
     * @return          the {@code WindowType} that corresponds to {@code text}
     */
    @Nullable
    public static WindowType fromValue(int value) {
        for (WindowType windowType : WindowType.values()) {
            if (windowType.intValue == value) {
                return windowType;
            }
        }
        return null;
    }
}
