package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * Waveshaper effect.
 *
 */

public class Waveshaper extends AudioEffect {

    private static final String LABEL = "Waveshaper";
    private static final String DESCRIPTION = "Basic waveshaper algorithm with shaping function f(x,a) = x*(abs(x) + a)/(x^2 + (a-1)*abs(x) + 1)";
    private float threshold;

    /**
     * Creates a new {@code Waveshaper} instance.
     *
     * @param threshold     value > 1.0
     */
    public Waveshaper(float threshold) {
        this.threshold = threshold;
    }

    protected Waveshaper(Parcel in) {
        this.threshold = in.readFloat();
    }

    /**
     * Sets the threshold value.
     *
     * @param threshold    value > 1.0
     */
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    /**
     * <p>
     *     Applies the {@code AudioEffect} to a block of PCM samples.
     *     Input and output sample arrays must have the same length.
     * </p>
     *
     * @param input     array of {@code float} input samples
     * @param output    array of {@code float} output samples must be of same length as input array
     */
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; i++) {
                output[i] = input[i] * (Math.abs(input[i]) + threshold) /
                        ((input[i] * input[i]) + (threshold - 1) * Math.abs(input[i]) + 1);
            }
        }
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.threshold);
    }

    public static final Creator<Waveshaper> CREATOR = new Creator<Waveshaper>() {
        @Override
        public Waveshaper createFromParcel(Parcel source) {
            return new Waveshaper(source);
        }

        @Override
        public Waveshaper[] newArray(int size) {
            return new Waveshaper[size];
        }
    };
}
