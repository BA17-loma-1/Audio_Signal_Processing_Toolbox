package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * Created by georgrem, stockan1 on 22.02.2017.
 */

public enum AudioCodingFormat {
    // Linear pulse-code modulation
    LINEAR_PCM("LPCM", 1);

    private String stringValue = "";
    private int intValue = 0;

    private AudioCodingFormat(String toString, int value) {
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

}
