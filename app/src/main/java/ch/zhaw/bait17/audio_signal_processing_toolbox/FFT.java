package ch.zhaw.bait17.audio_signal_processing_toolbox;

import org.jtransforms.fft.FloatFFT_1D;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * <p>
 *     Calculates the Fourier spectrum of a signal.<br>
 *     If not specified explicitly a hamming window is used to weight the sampled data.
 * </p>
 * <p>
 *     The FFT window size is determined by the size of the input sample array.
 *     FFT windows size: the input sample array length rounded up to the next higher power of 2.
 * </p>
 *
 * @author georgrem, stockan1
 */
public class FFT {

    private static final String TAG = FFT.class.getSimpleName();
    private static final WindowType DEFAULT_WINDOW_TYPE = WindowType.HAMMING;
    private Window win;
    private int fftWindowSize = Constants.DEFAULT_FFT_RESOLUTION;

    /**
     * Creates an instance of FFT with a Hamming window.
     */
    public FFT() {
        win = new Window(DEFAULT_WINDOW_TYPE);
    }

    /**
     * Creates an instance of FFT with the specified windows size.
     * @param fftWindowSize
     */
    public FFT(int fftWindowSize) {
        this();
        this.fftWindowSize = fftWindowSize;
    }

    /**
     * Creates an instance of FFT with the specified window and size.
     * @throws IllegalArgumentException
     * @param type The window type used to weight the samples
     */
    public FFT(@NonNull WindowType type) {
        if (type == null)
            throw new IllegalArgumentException("Invalid window type.");
        win = new Window(type);
    }

    /**
     * Creates an instance of FFT with the specified window.
     * @param fftWindowSize
     * @param type
     */
    public FFT(int fftWindowSize, @NonNull WindowType type) {
        this(type);
        this.fftWindowSize = fftWindowSize;
    }

    /**
     * <p>
     *     Computes the FFT of real input data.
     *     Sample input data is filtered with the specified window.
     *     Samples are automatically zero-padded if the sample size is not a power of 2.
     *     Returns only first half of DFT spectrum, second half is ignored because of symmetry.
     * </p>
     *
     * @param samples The data to transform
     * @return The transformed data as a float array
     */
    public float[] getForwardTransform(float[] samples) {
        //Log.d(TAG, "Sample block size: " + samples.length);
        float[] weightedSamples = applyWindowToSamples(samples);
        int paddingLength = getPaddingLength(weightedSamples.length);
        // Zero-padding if necessary
        if (paddingLength > 0) {
            weightedSamples = getZeroPaddedSamples(samples, paddingLength);
        }
        FloatFFT_1D fft = new FloatFFT_1D(weightedSamples.length);
        fft.realForward(weightedSamples);
        //Log.d(TAG, "FFT window size: " + weightedSamples.length);
        return weightedSamples;
    }

    /**
     * <p>
     *     Computes the FFT of real input data.
     *     Sample input data is filtered with the specified window.
     *     Samples are automatically zero-padded if the sample size is not a power of 2.
     *     Returns the full DFT spectrum.
     * </p>
     *
     * @param samples A float array
     * @return The transformed data as a float array
     */
    public float[] getForwardTransformFull(float[] samples) {
        float[] weightedSamples = applyWindowToSamples(samples);
        int paddingLength = getPaddingLength(weightedSamples.length);
        // Zero-padding if necessary
        if (paddingLength > 0) {
            weightedSamples = getZeroPaddedSamples(samples, paddingLength);
        }
        FloatFFT_1D fft = new FloatFFT_1D(weightedSamples.length / 2);
        fft.realForwardFull(weightedSamples);
        return weightedSamples;
    }

    /**
     * <p>
     *     Computes the inverse FFT of real input data and returns the result in a float array.
     * </p>
     * @param spectrum An array containing the data to transform
     * @param scale If true scaling is performed
     * @return The transformed data as a float array
     */
    public float[] getBackwardTransform(float[] spectrum, boolean scale) {
        FloatFFT_1D ifft = new FloatFFT_1D(spectrum.length);
        ifft.complexInverse(spectrum, scale);
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
     * Computes and returns the necessary padding length.
     * @param sampleLength
     * @return the padding length to be applied to the input samples prior to transformation
     * @throws IllegalArgumentException if sample length is less than 0
     */
    private int getPaddingLength(int sampleLength) throws IllegalArgumentException {
        if (sampleLength < 0) {
            throw new IllegalArgumentException("Illegal sample length: " + sampleLength);
        }
        //int exponent = (int) Math.ceil(Math.log(sampleLength) / Math.log(2));
        if (sampleLength >= fftWindowSize) {
            return sampleLength;
        }
        //return ((int) Math.pow(2, exponent)) - sampleLength;
        return fftWindowSize - sampleLength;
    }

    /**
     * Adds zero padding of specified length to samples.
     * @throws IllegalArgumentException
     * @param samples The samples input
     * @param paddingLength The padding length
     * @return Zero padded samples as a float array
     * @throws IllegalArgumentException if paddingLength less than 0
     */
    private float[] getZeroPaddedSamples(float[] samples, int paddingLength)
            throws IllegalArgumentException {
        if (paddingLength < 0) {
            throw new IllegalArgumentException("Illegal padding length: " + paddingLength);
        }
        float[] zeroPaddedSamples = new float[samples.length + paddingLength];
        System.arraycopy(samples, 0, zeroPaddedSamples, 0, samples.length);
        return zeroPaddedSamples;
    }

    /**
     * Applies the window function to the sample data.
     * @param samples Input sample data
     * @return The filtered sample data
     */
    private float[] applyWindowToSamples(float[] samples) {
        float[] window = win.getWindow(samples.length);
        for (int i = 0; i < samples.length; i++) {
            samples[i] = samples[i] * window[i];
        }
        return samples;
    }

}
