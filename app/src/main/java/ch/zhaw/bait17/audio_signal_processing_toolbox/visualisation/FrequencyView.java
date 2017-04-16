package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Represents a frequency-based view.
 *
 * @author georgrem, stockan1
 */

public abstract class FrequencyView extends AudioView {

    public FrequencyView(Context context) {
        super(context);
    }

    public FrequencyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrequencyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets the data to be displayed in the {@code FrequencyView}.
     *
     * @param magnitudes    an array of {@code float}
     */
    public abstract void setMagnitudes(@NonNull float[] magnitudes);

}
