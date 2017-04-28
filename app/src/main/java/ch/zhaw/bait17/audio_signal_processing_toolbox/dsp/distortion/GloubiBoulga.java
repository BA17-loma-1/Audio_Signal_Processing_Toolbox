package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * Another waveshaper function. Algorithm by Laurent de Soras from Ohm Force (www.ohmforce.com).
 *
 */

public class GloubiBoulga extends AudioEffect {

    private static final double GLOUBI_BOULGA_CONST = 0.686306;
    private static final String LABEL = "Waveshaper";
    private static final String DESCRIPTION = "Another waveshaper function. Algorithm by Laurent de Soras from Ohm Force (www.ohmforce.com)";

    public GloubiBoulga() {

    }

    /**
     * @param input     an array of {@code float} containing the input samples
     *                  {@code float} values must be normalised in the range [-1,1]
     * @param output    an array of {@code float} of same length as the input samples array
     */
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; i++) {
                double x = input[i] * GLOUBI_BOULGA_CONST;
                double a = 1 + Math.exp(Math.sqrt(Math.abs(x)) * -0.75);
                output[i] = (float) ((Math.exp(x) - Math.exp(-x * a)) / (Math.exp(x) + Math.exp(-x)));
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

    }

    protected GloubiBoulga(Parcel in) {

    }

    public static final Creator<GloubiBoulga> CREATOR = new Creator<GloubiBoulga>() {
        @Override
        public GloubiBoulga createFromParcel(Parcel source) {
            return new GloubiBoulga(source);
        }

        @Override
        public GloubiBoulga[] newArray(int size) {
            return new GloubiBoulga[size];
        }
    };

}
