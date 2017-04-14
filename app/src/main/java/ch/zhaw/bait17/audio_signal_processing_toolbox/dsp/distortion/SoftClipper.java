package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.support.annotation.NonNull;

/**
 * Soft clipping function. By scoofy@inf.elte.hu, MusicDSP forum (www.musicdsp.com)
 *
 */

public class SoftClipper {

    /**
     * @param input             an array of {@code float} containing the input samples
     *                          {@code float} values must be normalised in the range [-1,1]
     * @param output            an array of {@code float} of same length as the input samples array
     * @param clippingFactor    value should be in the range [1,1000]
     */
    public static void softClipper(@NonNull float[] input, @NonNull float[] output,
                                   float clippingFactor) {
        if (input.length == output.length) {
            float invAtanShape = 1.0f / (float) Math.atan(clippingFactor);
            for (int i = 0; i < input.length; i++) {
                output[i] = invAtanShape * (float) Math.atan(input[i] * clippingFactor);
            }
        }
    }

}
