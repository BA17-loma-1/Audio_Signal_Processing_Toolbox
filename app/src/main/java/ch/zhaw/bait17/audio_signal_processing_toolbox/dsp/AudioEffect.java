package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp;

import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * @author georgrem, stockan1
 */

public interface AudioEffect extends Parcelable {

    /**
     * <p>
     *     Applies the {@code AudioEffect} to a block of PCM samples.
     *     Input and output sample arrays must have the same length.
     * </p>
     *
     * @param input     array of {@code float} input samples
     * @param output    arary of {@code float} output samples must be of same length as input array
     */
    void apply(@NonNull float[] input, @NonNull float[] output);

    /**
     * <p>
     *      Returns the label of the {@code AudioEffect}. </br>
     *      This label will be displayed in the {@code AudioEffect} drop down list.
     * </p>
     *
     * @return      label
     */
    String getLabel();

    /**
     * <p>
     *      Returns the description of the {@code AudioEffect}. </br>
     *      This description will be displayed in the {@code AudioEffect} drop down list.
     * </p>
     *
     * @return      description
     */
    String getDescription();

}
