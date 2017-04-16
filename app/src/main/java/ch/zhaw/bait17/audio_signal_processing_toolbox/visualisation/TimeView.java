package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Represents a time-based view.
 *
 * @author georgrem, stockan1
 */

public abstract class TimeView extends AudioView {

    public TimeView(Context context) {
        super(context);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets the data to be displayed in the {@code TimeView}.
     *
     * @param samples   array of {@code short}
     */
    public abstract void setSamples(@NonNull short[] samples);

}
