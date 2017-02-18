package ch.zhaw.bait17.audio_signal_processing_toolbox;

import org.jtransforms.fft.DoubleFFT_1D;
import android.util.Log;

/**
 * Created by georgrem, stockan1 on 16.02.2017.
 * Calculates the Fourier spectrum.
 * If not specified a hamming window is used to weight the sampled data.
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
     * Calculates the FFT for a given array of input data.
     * Returns a approximation of the frequencies in sample.
     * Improvement: Sample input data is filtered with a Hamming window.
     *
     * @param samples the data to transform
     * @param samplingFrequency in kSPS or Hertz
     * @return spectrum, the FFT transformation of the sample data
     */
    public double[] forwardTransform(double[] samples, int samplingFrequency) {
        // apply window to sample input data
        double[] windowCoefficients = win.getWindow(samples.length);
        System.arraycopy(applyWindowToSamples(samples, windowCoefficients), 0, samples, 0, samples.length);
        double[] spectrum = new double[samples.length];
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
        fft.realForward(spectrum);
        return spectrum;
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
        double[] filtered = new double[samples.length];

        if (samples.length != window.length) {
            // filtered contains values 0.0
            Log.e(TAG, "Sample and window length do not match.");
        } else {
            for (int i = 0; i < samples.length; i++) {
                filtered[i] = (double) samples[i] * window[i];
            }
        }
        return filtered;
    }

}
