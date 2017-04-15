package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

/**
 * Enumerator for filter types.
 *
 * @author georgrem, stockan1
 */

public enum FilterType {

    LOWPASS("lowpass"), HIGHPASS("highpass"), BANDPASS("bandpass"), BANDSTOP("bandstop");

    private String type;

    FilterType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        String label;
        FilterType ft = null;

        for (FilterType filterType : FilterType.values()) {
            if (filterType.type.equalsIgnoreCase(this.type)) {
                ft = filterType;
            }
        }

        switch (ft) {
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
