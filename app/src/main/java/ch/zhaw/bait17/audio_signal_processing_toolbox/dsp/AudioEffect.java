package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Constants;

/**
 * @author georgrem, stockan1
 */

public abstract class AudioEffect implements Parcelable {

    private int samplingFrequency = Constants.DEFAULT_SAMPLE_RATE;

    /**
     * <p>
     * Applies the {@code AudioEffect} to a block of PCM samples.
     * Input and output sample arrays must have the same length.
     * </p>
     *
     * @param input  array of {@code float} input samples
     * @param output arary of {@code float} output samples must be of same length as input array
     */
    public abstract void apply(@NonNull float[] input, @NonNull float[] output);

    /**
     * <p>
     * Returns the label of the {@code AudioEffect}. </br>
     * This label will be displayed in the {@code AudioEffect} drop down list.
     * </p>
     *
     * @return label
     */
    public abstract String getLabel();

    /**
     * <p>
     * Returns the description of the {@code AudioEffect}. </br>
     * This description will be displayed in the {@code AudioEffect} drop down list.
     * </p>
     *
     * @return description
     */
    public abstract String getDescription();

    /**
     * <p>
     * Sets the sampling frequency.
     * </p>
     *
     * @param samplingFrequency sampling frequency
     */
    public void setSamplingFrequency(int samplingFrequency) {
        if (samplingFrequency > 0) {
            this.samplingFrequency = samplingFrequency;
        }
    }

    /**
     * <p>
     * Returns the sampling frequency
     * </p>
     *
     * @return  sampling frequency
     */
    protected int getSamplingFrequency() {
        return samplingFrequency;
    }
}
