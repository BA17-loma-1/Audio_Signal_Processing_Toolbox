package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * Created by georgrem, stockan1 on 22.02.2017.
 */

public enum AudioCodingFormat {
    // Linear pulse-code modulation
    LINEAR_PCM(1);

    private String stringValue = "";
    private int intValue = 0;

    private AudioCodingFormat(int value) {
        intValue = value;
        if (value == 1) {
            stringValue = "LPCM";
        }
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int getValue() {
        return intValue;
    }

}
