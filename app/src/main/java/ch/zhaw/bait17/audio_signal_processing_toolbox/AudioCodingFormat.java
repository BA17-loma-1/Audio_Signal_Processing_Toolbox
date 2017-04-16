package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * @author georgrem, stockan1
 */

public enum AudioCodingFormat {

    LINEAR_PCM(1);

    private String stringValue = "";
    private int intValue = 0;

    AudioCodingFormat(int value) {
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
