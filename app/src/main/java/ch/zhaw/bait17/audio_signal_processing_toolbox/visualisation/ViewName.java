package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.support.annotation.Nullable;

/**
 * @author georgrem, stockan1
 */

public enum ViewName {
    NO_VIEW(1, "No view"), WAVEFORM(2, "Waveform"), SPECTROGRAM(3, "Spectrogram"),
    SPECTRUM(4, "Spectrum");

    private String text;
    private int value;

    ViewName(int value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public int getValue() {
        return value;
    }

    /**
     * Looks up the text value of {@code ViewName}.
     *
     * @param text      text value of {@code ViewName}
     * @return          the {@code ViewName} that corresponds to {@code text}
     */
    public static @Nullable ViewName fromString(String text) {
        for (ViewName viewName : ViewName.values()) {
            if (viewName.toString().equalsIgnoreCase(text)) {
                return viewName;
            }
        }
        return null;
    }
}
