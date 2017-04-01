package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * <p>
 *     Interface representing a filter.
 * </p>
 * @author georgrem, stockan1
 */

public interface Filter extends Parcelable {

    /**
     * <p>
     *     Apply the filter to a block of PCM samples.
     * </p>
     * @param input
     * @return
     */
    float[] apply(@NonNull float[] input);

    FilterSpec getFilterSpec();

}
