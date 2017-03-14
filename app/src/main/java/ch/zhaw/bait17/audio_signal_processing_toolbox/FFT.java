package ch.zhaw.bait17.audio_signal_processing_toolbox;

import org.apache.commons.math3.stat.descriptive.WeightedEvaluation;
import org.jtransforms.fft.FloatFFT_1D;
import android.util.Log;

/**
 * <p>Calculates the Fourier spectrum of a signal.<br>
 * If not specified explicitly a hamming window is used to weight the sampled data.</p>
 *
 * Created by georgrem, stockan1 on 16.02.2017.
 */
public class FFT {

    private static final String TAG = FFT.class.getSimpleName();
    private static final int MIN_WINDOW_SIZE = 4096;
    private static final WindowType DEFAULT_WINDOW_TYPE = WindowType.HAMMING;
    private Window win;
    private int windowSize;

    /**
     * Creates an instance of FFT with a Hamming window.
     */
    public FFT() {
        win = new Window(DEFAULT_WINDOW_TYPE);
    }

    /**
     * Creates an instance of FFT.
     * @throws IllegalArgumentException
     * @param type The window type used to weight the samples.
     */
    public FFT(WindowType type) {
        if (type == null)
            throw new IllegalArgumentException("Invalid window type.");
        win = new Window(type);
    }

    /**
     * <p>
     *     Computes the FFT of real input data.
     *     Sample input data is filtered with the specified window.
     *     Returns only first half of DFT spectrum, second half is ignored because of symmetry.
     *     Samples are automatically zero-padded if sample size doesn't meet minimum required
     *     FFT window size of {@value #MIN_WINDOW_SIZE} samples.
     * </p>
     *
     * @param samples The data to transform.
     * @return The transformed data as a float array.
     */
    public float[] getForwardTransform(float[] samples) {
        float[] weightedSamples = applyWindowToSamples(samples);
        // Zero-padding if necessary
        if (weightedSamples.length < MIN_WINDOW_SIZE) {
            weightedSamples = addZeroPadding(samples, MIN_WINDOW_SIZE - weightedSamples.length);
        }
        FloatFFT_1D fft = new FloatFFT_1D(weightedSamples.length);
        fft.realForward(weightedSamples);

        // Log.i(TAG, String.format("FFT window size: %d", weightedSamples.length));

        return weightedSamples;
    }

    /**
     * <p>
     *     Computes the FFT of real input data.
     *     Sample input data is filtered with the specified window.
     *     Returns the full DFT spectrum.
     *     Samples are automatically zero-padded if sample size doesn't meet minimum required
     *     FFT window size of {@value #MIN_WINDOW_SIZE} samples.
     * </p>
     *
     * @param samples A float array.
     * @return The transformed data as a float array.
     */
    public float[] getForwardTransformFull(float[] samples) {
        float[] weightedSamples = applyWindowToSamples(samples);
        int paddingLength = weightedSamples.length;
        if (2 * paddingLength < MIN_WINDOW_SIZE) {
            paddingLength += MIN_WINDOW_SIZE - paddingLength;
        }
        float[] zeroPaddedSamples = addZeroPadding(weightedSamples, paddingLength);
        int N = zeroPaddedSamples.length / 2;
        FloatFFT_1D fft = new FloatFFT_1D(N);
        fft.realForwardFull(zeroPaddedSamples);

        //Log.i(TAG, String.format("FFT window size: %d", zeroPaddedSamples.length));

        return zeroPaddedSamples;
    }

    /**
     * <p>
     *     Computes the inverse FFT of real input data and returns the result in a float array.
     * </p>
     * @param spectrum An array containing the data to transform.
     * @param scale If true scaling is performed.
     * @return The transformed data as a float array.
     */
    public float[] getBackwardTransform(float[] spectrum, boolean scale) {
        FloatFFT_1D ifft = new FloatFFT_1D(spectrum.length);
        ifft.complexInverse(spectrum, scale);
        return spectrum;
    }

    /**
     * Adds zero padding of specified length to samples.
     * @throws IllegalArgumentException
     * @param samples The samples input.
     * @param paddingLength The padding length.
     * @return Zero padded samples as a float array.
     */
    private float[] addZeroPadding(float[] samples, int paddingLength) {
        if (paddingLength < 0) {
            throw new IllegalArgumentException(String.format("Invalid padding length (%d).", paddingLength));
        }
        float[] zeroPaddedSamples = new float[samples.length + paddingLength];
        System.arraycopy(samples, 0, zeroPaddedSamples, 0, samples.length);
        return zeroPaddedSamples;
    }

    /**
     *
     * @return String value of the window type used in this instance of FFT.
     */
    public String getWindowType() {
        return win.toString();
    }

    public int getMinWindowSize() {
        return MIN_WINDOW_SIZE;
    }

    /**
     * Applies the window function to the sample data.
     * @param samples Input sample data.
     * @return The filtered sample data.
     */
    private float[] applyWindowToSamples(float[] samples) {
        float[] window = win.getWindow(samples.length);
        for (int i = 0; i < samples.length; i++) {
            samples[i] = samples[i] * window[i];
        }
        return samples;
    }

}
