package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.support.annotation.NonNull;

/**
 * Quantizer / decimator with smooth control. By David Lowenfels, MusicDSP forum (www.musicdsp.com)
 *
 */

public class Bitcrusher extends Distortion {

    /**
     * @param input     an array of {@code float} containing the input samples
     * @param output    an array of {@code float} of same length as the input samples array
     * @param normFreq  frequency / sampleRate, a value in the range [0,1]
     * @param bits      the number of bits in the range [1,16]
     */
    public static void apply(@NonNull float[] input, @NonNull float[] output,
                                  float normFreq, int bits) {
        if (input.length == output.length) {
            double step = Math.pow(0.5, bits);
            double phasor = 0;
            double last = 0;
            for (int i = 0; i < input.length; i++) {
                phasor += normFreq;
                if (phasor >= 1) {
                    phasor -= 1;
                    // Quantize
                    last = step * Math.floor((input[i] / step) + 0.5);
                }
                // Sample and hold
                output[i] = (float) last;
            }
        }
    }

}
