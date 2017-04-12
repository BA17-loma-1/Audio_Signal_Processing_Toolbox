package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

/**
 * Enumerator for filter types.
 *
 * @author georgrem, stockan1
 */

public enum FilterType {

    LOWPASS("lowpass"), HIGHPASS("highpass"), BANDPASS("bandpass"), BANDSTOP("bandstop"), UNKNOWN("unknown");

    private String type;

    FilterType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String getLabel(FilterType type) {
        String label;
        switch (type) {
            case LOWPASS:
                label = "Low pass filter";
                break;
            case HIGHPASS:
                label = "High pass filter";
                break;
            case BANDPASS:
                label = "Band pass filter";
                break;
            case BANDSTOP:
                label = "Band stop filter";
                break;
            default:
                label = "No filter";
                break;
        }
        return label;
    }

}
