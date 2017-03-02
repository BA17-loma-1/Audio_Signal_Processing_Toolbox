package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * Window types.
 * Created by georgrem, stockan1 on 18.02.2017.
 */

public enum WindowType {
    HANN("Hann", 0), HAMMING("Hamming", 1), BLACKMAN("Blackman", 2), RECTANGLE("Rectangle", 3);

    private String stringValue = "";
    private int intValue = 0;

    private WindowType(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

}
