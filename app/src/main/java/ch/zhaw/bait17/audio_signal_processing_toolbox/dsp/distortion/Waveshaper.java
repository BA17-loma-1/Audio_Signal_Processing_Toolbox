package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * Waveshaper effect.
 *
 */

public class Waveshaper implements AudioEffect {

    private static final String LABEL = "Waveshaper";
    private static final String DESCRIPTION = "";
    private float threshold;

    /**
     * Creates a new {@code Waveshaper} instance.
     *
     * @param threshold     value > 1.0
     */
    public Waveshaper(float threshold) {
        this.threshold = threshold;
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
                if (Math.abs(input[i]) >= threshold) {
                    if (input[i] > 0f) {
                        output[i] = threshold + (1f - threshold) *
                                (float) (Math.tanh((input[i] - threshold) / (1 - threshold)));
                    } else {
                        output[i] = -(threshold + (1f - threshold) *
                                (float) Math.tanh((-input[i] - threshold) / (1 - threshold)));
                    }
                }
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

    protected Waveshaper(Parcel in) {
        this.threshold = in.readFloat();
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
