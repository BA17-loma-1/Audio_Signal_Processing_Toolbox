package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * Saturation effect. By Bram de Jong, MusicDSP forum (www.musicdsp.com)
 *
 */

public class Saturation implements AudioEffect {

    private static final String LABEL = "Saturation";
    private static final String DESCRIPTION = "";
    private float threshold;


    /**
     * Creates a new {@code Saturation} instance.
     *
     * @param threshold     value > 1.0
     */
    public Saturation(float threshold) {
        this.threshold = threshold;
    }

    /**
     *
     * @param input         an array of {@code float} containing the input samples
     *                      {@code float} values must be normalised in the range [-1,1]
     * @param output        an array of {@code float} of same length as the input samples array
     */
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; i++) {
                if (Math.abs(input[i]) >= threshold) {
                    if (input[i] > 0) {
                        output[i] = threshold + (1f - threshold) *
                                sigmoid((input[i] - threshold) / ((1 - threshold) * 1.5f));
                    } else {
                        output[i] = -(threshold + (1f - threshold) *
                                sigmoid((-input[i] - threshold) / ((1 - threshold) * 1.5f)));
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

    protected Saturation(Parcel in) {
        this.threshold = in.readFloat();
    }

    public static final Creator<Saturation> CREATOR = new Creator<Saturation>() {
        @Override
        public Saturation createFromParcel(Parcel source) {
            return new Saturation(source);
        }

        @Override
        public Saturation[] newArray(int size) {
            return new Saturation[size];
        }
    };

    /**
     * Sigmoid function. By Bram de Jong.
     * See <a href="https://en.wikipedia.org/wiki/Sigmoid_function">Sigmoid function on Wikipedia</a>
     *
     * @param   x input
     * @return  function output
     */
    protected static float sigmoid(float x) {
        if (Math.abs(x) < 1) {
            return x * (1.5f - (0.5f * x *x ));
        } else {
            return x > 0.f ? 1.f : -1.f;
        }
    }

}
