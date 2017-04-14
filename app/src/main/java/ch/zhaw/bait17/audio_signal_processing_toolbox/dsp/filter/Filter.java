package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * <p>
 *     An interface representing a digital filter.
 * </p>
 * @author georgrem, stockan1
 */

public interface Filter extends Parcelable {

    /**
     * <p>
     *     Applies the filter to a block of PCM samples.
     *     Input and output sample arrays must have the same length.
     * </p>
     *
     * @param input     array of {@code float} input samples
     * @param output    arary of {@code float} output samples must be of same length as input array
     */
    void apply(@NonNull float[] input, @NonNull float[] output);

    /**
     * Returns the filter specification.
     *
     * @return
     */
    FilterSpec getFilterSpec();

}
