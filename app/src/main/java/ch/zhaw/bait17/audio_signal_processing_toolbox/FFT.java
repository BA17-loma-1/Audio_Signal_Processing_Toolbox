package ch.zhaw.bait17.audio_signal_processing_toolbox;

import org.jtransforms.fft.DoubleFFT_1D;
import android.util.Log;

/**
 * Created by georgrem, stockan1 on 16.02.2017.
 * <p>Calculates the Fourier spectrum of a signal.<br>
 * If not specified explicitly a hamming window is used to weight the sampled data.</p>
 */
public class FFT {

    private static final String TAG = FFT.class.getSimpleName();
    private Window win = null;

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
    public double[] getForwardTransform(double[] samples) {
        // Apply window to sample input data
        double[] windowCoefficients = win.getWindow(samples.length);
        double[] weightedSamples = applyWindowToSamples(samples, windowCoefficients);
        int N = weightedSamples.length;
        DoubleFFT_1D fft = new DoubleFFT_1D(N);
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
     * @return zeroPaddedSamples, the transformed data
     */
    public double[] getForwardTransformFull(double[] samples) {
        // Apply window to sample input data
        double[] windowCoefficients = win.getWindow(samples.length);
        double[] weightedSamples = applyWindowToSamples(samples, windowCoefficients);
        // Pad with zeros
        double[] zeroPaddedSamples = addZeroPadding(weightedSamples, weightedSamples.length);
        int N = zeroPaddedSamples.length/2;
        DoubleFFT_1D fft = new DoubleFFT_1D(N);
        fft.realForwardFull(zeroPaddedSamples);
        return zeroPaddedSamples;
    }


    /**
     *
     * @param spectrum
     * @param samplingFrequency
     * @return
     */
    public double[] backwardTransform(double[] spectrum, int samplingFrequency) {
        double[] samples = new double[spectrum.length];


        return samples;
    }

    public double[] addZeroPadding(double[] samples, int paddingLength) {
        double[] zeroPaddedSamples = new double[samples.length + paddingLength];
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
     * Applies a window function to the sample data
     * @param samples input sample data
     * @return filtered sample data
     */
    private double[] applyWindowToSamples(double[] samples, double[] window) {
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
