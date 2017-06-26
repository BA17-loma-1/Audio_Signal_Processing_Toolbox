package ch.zhaw.bait17.audio_signal_processing_toolbox.fft;

/**
 * Window types supported by the application.
 *
 * @author georgrem, stockan1
 */

public enum WindowType {
    TRIANGULAR("Triangular", 0), HAMMING("Hamming", 1), HANN("Hann", 2), BLACKMAN("Blackman", 3),
    BLACKMAN_HARRIS("Blackman-Harris", 4), BARTLETT("Bartlett", 5), RECTANGULAR("Rectangular", 6);

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
}
