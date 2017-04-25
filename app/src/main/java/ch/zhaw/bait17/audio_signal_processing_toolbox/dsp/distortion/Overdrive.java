package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * Overdrive effect. By, MusicDSP forum (www.musicdsp.com)
 *
 */

public class Overdrive implements AudioEffect {

    private static final String LABEL = "Overdrive";
    private static final String DESCRIPTION = "A nonlinear system, spectral components (distortion products) appear that were not part of the original signal";
    private static final float ONE_THIRD = 1/3.0f;
    private static final float TWO_THIRDS = 2 * ONE_THIRD;

    public Overdrive() {

    }

    /**
     * @param input     an array of {@code float} containing the input samples
     * @param output    an array of {@code float} of same length as the input samples array
     */
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; i++) {
                if (input[i] >= 0 && input[i] <= ONE_THIRD) {
                    output[i] = 2 * input[i];
                } else if (input[i] >= ONE_THIRD && input[i] <= TWO_THIRDS) {
                    output[i] = ((3f - (float) Math.pow((2 - 3 * input[i]), 2)) / 3) * input[i];
                } else {
                    output[i] = 0.95f;
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

    }

    protected Overdrive(Parcel in) {

    }

    public static final Creator<Overdrive> CREATOR = new Creator<Overdrive>() {
        @Override
        public Overdrive createFromParcel(Parcel source) {
            return new Overdrive(source);
        }

        @Override
        public Overdrive[] newArray(int size) {
            return new Overdrive[size];
        }
    };

}
