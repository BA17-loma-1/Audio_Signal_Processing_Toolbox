package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.support.annotation.NonNull;

/**
 * Overdrive effect. By, MusicDSP forum (www.musicdsp.com)
 *
 */

public class Overdrive {

    private static final float ONE_THIRD = 1/3.0f;
    private static final float TWO_THIRDS = 2 * ONE_THIRD;

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

}
