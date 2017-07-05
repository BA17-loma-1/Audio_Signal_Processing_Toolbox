package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp;

import android.support.annotation.NonNull;

/**
 * <p>
 *     Limiter </br>
 *
 *     Provides control over the highest peaks in the audio signal and at the same time
 *     changes the dynamics of the audio signal as little as possible. </br>
 *
 *     The output level will never exceed the threshold {@link #limit}. </br>
 * </p>
 * <p>
 *     From DAFX Digital Audio Effects, second edition, page 109. </br>
 *     Author: Martin Holters
 * </p>
 */
public class Limiter {

    private static final int DELAY = 5;

    private float[] buffer = new float[DELAY];
    private float limit = 1.0f;
    private float gain = 1.0f;

    public Limiter() {

    }

    /**
     * <p>
     *     Applies limiting to a block of PCM samples.
     * </p>
     *
     * @param input         array of {@code float} input samples
     * @param inputGain     input gain applied to each sample
     */
    public void apply(@NonNull float[] input, float inputGain) {
        float attackTime = 0.0002f;
        float releaseTime = 0.001f;
        float peak = 0.f;
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            input[i] *= inputGain;

            float coeff = releaseTime;
            float a = Math.abs(input[i]);

            if (a > peak) {
                coeff = attackTime;
            }
            peak = (1 - coeff) * peak + coeff * a;
            float f = Math.min(1.0f, limit / peak);
            if (f < gain) {
                coeff = attackTime;
            } else {
                coeff = releaseTime;
            }
            gain = (1 - coeff) * gain + coeff * f;
            output[i] = gain * buffer[buffer.length - 1];
            System.arraycopy(buffer, 0, buffer, 1, buffer.length - 1);
            buffer[0] = input[i];
        }
        System.arraycopy(output, 0, input, 0, input.length);
    }
}