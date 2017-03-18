package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.support.annotation.NonNull;
import ch.zhaw.bait17.audio_signal_processing_toolbox.FFT;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.PCMUtil;

/**
 * <p>Computes the power spectral density of the given audio samples. <br>
 * The power spectral density is sometimes simply called power spectrum.</p>
 *
 * @author georgrem, stockan1
 */

public class PowerSpectrum {

    private final float[] COMPUTED_POWER_SPECTRUM;

    public PowerSpectrum(@NonNull short[] samples) {
        COMPUTED_POWER_SPECTRUM = getPowerSpectrum(samples);
    }

    /**
     * Returns the computed power spectrum.
     * @return A float array containing the power spectrum.
     */
    public float[] getPowerSpectrum() {
        float[] spectrum = new float[COMPUTED_POWER_SPECTRUM.length];
        System.arraycopy(COMPUTED_POWER_SPECTRUM, 0, spectrum, 0, COMPUTED_POWER_SPECTRUM.length);
        return spectrum;
    }

    private float[] getPowerSpectrum(@NonNull short[] samples) {
        float[] hComplex = new FFT().getForwardTransform(PCMUtil.short2FloatArray(samples));
        final int FFT_SIZE = hComplex.length;
        float[] hMag = new float[FFT_SIZE / 2];
        for (int i = 0; i < FFT_SIZE / 2; i++) {
            hMag[i] = (hComplex[2*i] * hComplex[2*i]) + (hComplex[(2*i) + 1] * hComplex[(2*i) + 1]);
        }
        return hMag;
    }

}
