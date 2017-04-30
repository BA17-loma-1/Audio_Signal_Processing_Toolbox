package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.support.annotation.Nullable;

/**
 * @author georgrem, stockan1
 */

public enum VisualisationType {
    PRE_FX("pre FX"), POST_FX("post FX"), BOTH("pre and post FX");

    private String text;

    VisualisationType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    /**
     * Looks up the text value of {@code VisualisationType}.
     *
     * @param text      text value of {@code VisualisationType}
     * @return          the {@code VisualisationType} that corresponds to {@code text}
     */
    public static @Nullable VisualisationType fromString(String text) {
        for (VisualisationType type : VisualisationType.values()) {
            if (type.toString().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }
}
