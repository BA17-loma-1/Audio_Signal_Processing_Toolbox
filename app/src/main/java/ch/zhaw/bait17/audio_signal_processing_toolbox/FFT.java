package ch.zhaw.bait17.audio_signal_processing_toolbox;

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
    private Window win;

    public FFT() {
        win = new Window(WindowType.HAMMING);
    }

    public FFT(WindowType type) {
        win = new Window(type);
    }

    /**
     * <p>
     *     Calculates the spectrum for a given array of input signal data.
     *     This method assumes that the sample data is real.
     *     Sample input data is filtered with the specified window.
     *     Returns only first half of DFT spectrum, second half is ignored because of symmetry.
     * </p>
     *
     * @param samples the data to transform
     * @return weightedSamples, the transformed sample data
     */
    public float[] getForwardTransform(float[] samples) {
        // Apply window to sample input data
        float[] windowCoefficients = win.getWindow(samples.length);
        float[] weightedSamples = applyWindowToSamples(samples, windowCoefficients);
        int N = weightedSamples.length;
        FloatFFT_1D fft = new FloatFFT_1D(N);
        fft.realForward(weightedSamples);
        return weightedSamples;
    }

    /**
     * <p>
     *     Calculates the spectrum for a given array of input signal data.
     *     This method assumes that the sample data is real.
     *     Sample input data is filtered with the specified window.
     *     Returns the full DFT spectrum.
     * </p>
     *
     * @param samples
     * @return zeroPaddedSamples, the transformed data as a float array
     */
    public float[] getForwardTransformFull(float[] samples) {
        // Apply window to sample input data
        float[] windowCoefficients = win.getWindow(samples.length);
        float[] weightedSamples = applyWindowToSamples(samples, windowCoefficients);
        // Pad with zeros
        float[] zeroPaddedSamples = addZeroPadding(weightedSamples, weightedSamples.length);
        int N = zeroPaddedSamples.length/2;
        FloatFFT_1D fft = new FloatFFT_1D(N);
        fft.realForwardFull(zeroPaddedSamples);
        return zeroPaddedSamples;
    }


    /**
     *
     * @param spectrum
     * @param samplingFrequency
     * @return
     */
    public float[] backwardTransform(float[] spectrum, int samplingFrequency) {
        float[] samples = new float[spectrum.length];


        return samples;
    }

    /**
     * Adds zero padding of specified length to samples.
     * @param samples The samples input.
     * @param paddingLength The padding length.
     * @return Zero padded samples as a float array.
     */
    public float[] addZeroPadding(float[] samples, int paddingLength) {
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

    /**
     *
     * @param type The type of window to be used in this instance of FFT.
     */
    public void setWindowType(WindowType type) {
        win = new Window(type);
    }

    /**
     * Applies a window function to the sample data.
     * @param samples Input sample data.
     * @return Filtered sample data.
     */
    private float[] applyWindowToSamples(float[] samples, float[] window) {
        if (samples.length != window.length) {
            Log.e(TAG, "Sample and window length do not match.");
        } else {
            for (int i = 0; i < samples.length; i++) {
                samples[i] = samples[i] * window[i];
            }
        }
        return samples;
    }

}
