package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.support.annotation.NonNull;

/**
 * Another waveshaper function. Algorithm by Laurent de Soras from Ohm Force (www.ohmforce.com).
 *
 */

public class GloubiBoulga {

    private static final double GLOUBI_BOULGA_CONST = 0.686306;

    /**
     * @param input     an array of {@code float} containing the input samples
     *                  {@code float} values must be normalised in the range [-1,1]
     * @param output    an array of {@code float} of same length as the input samples array
     */
    public static void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; i++) {
                double x = input[i] * GLOUBI_BOULGA_CONST;
                double a = 1 + Math.exp(Math.sqrt(Math.abs(x)) * -0.75);
                output[i] = (float) ((Math.exp(x) - Math.exp(-x * a)) / (Math.exp(x) + Math.exp(-x)));
            }
        }
    }

}
