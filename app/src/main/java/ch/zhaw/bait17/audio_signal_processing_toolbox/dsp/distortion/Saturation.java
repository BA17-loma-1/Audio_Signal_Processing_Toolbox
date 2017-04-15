package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.support.annotation.NonNull;

/**
 * Saturation effect. By Bram de Jong, MusicDSP forum (www.musicdsp.com)
 *
 */

public class Saturation extends Distortion {

    /**
     *
     * @param input         an array of {@code float} containing the input samples
     *                      {@code float} values must be normalised in the range [-1,1]
     * @param output        an array of {@code float} of same length as the input samples array
     * @param threshold     value > 1.0
     */
    public void saturate(@NonNull float[] input, @NonNull float[] output,  float threshold) {
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

}
