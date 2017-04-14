package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.support.annotation.NonNull;

/**
 * Simple Fold-back distortion filter. By hellfire@upb.de, MusicDSP forum (www.musicdsp.com)
 *
 */
public class FoldBackDistortion {

    /**
     * @param input             an array of {@code float} containing the input samples
     *                          {@code float} values must be normalised in the range [-1,1]
     * @param output            an array of {@code float} of same length as the input samples array
     * @param threshold         value > 0
     */
    public static void apply(@NonNull float[] input, @NonNull float[] output,
                                          float threshold) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; i++) {
                if (input[i] > threshold || input[i] < -threshold) {
                    output[i] = Math.abs((input[i] - threshold % 4 * threshold) -
                            (2 * threshold)) - threshold;
                }
            }
        }
    }

}
