package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.support.annotation.NonNull;

/**
 * Waveshaper effect.
 *
 */

public class Waveshaper {

    /**
     * @param input         an array of {@code float} containing the input samples
     *                      {@code float} values must be normalised in the range [-1,1]
     * @param output        an array of {@code float} of same length as the input samples array
     * @param threshold     value > 1.0
     */
    public static void apply(@NonNull float[] input, @NonNull float[] output, float threshold) {
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

}
