package ch.zhaw.bait17.audio_signal_processing_toolbox.fft;

/**
 * Supported window types.
 *
 * @author georgrem, stockan1
 */

public enum WindowType {
    HANN("Hann", 0), HAMMING("Hamming", 1), BLACKMAN("Blackman", 2), RECTANGLE("Rectangle", 3);

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
