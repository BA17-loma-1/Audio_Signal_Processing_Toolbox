package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.support.annotation.NonNull;

import org.jtransforms.fft.FloatFFT_1D;

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
    private int fftResolution = Constants.DEFAULT_FFT_RESOLUTION;

    /**
     * Creates an instance of {@code FFT} with a Hamming window by default.
     */
    public FFT() {
        win = new Window(DEFAULT_WINDOW_TYPE);
    }

    /**
     * Creates an instance of {@code FFT} with the specified resolution.
     * <p>
     *     Although the FFT resolution can be any size it is usually a power of 2. </br>
     *     A window size (resolution) in the range [2^11, 2^15] is recommended.
     * </p>
     *
     * @param fftResolution                 the FFT resolution a.k.a the window size
     * @throws IllegalArgumentException     if fftResolution is <= 0
     */
    public FFT(int fftResolution) {
        this();
        if (fftResolution <= 0) {
            throw new IllegalArgumentException("FFT resolution must be greater than 0.");
        }
        this.fftResolution = fftResolution;
    }

    /**
     * * Creates an instance of {@code FFT} with the specified window.
     *
     * @param type      the window type used to weigh the samples
     */
    public FFT(@NonNull WindowType type) {
        win = new Window(type);
    }

    /**
     * Creates an instance of {@code FFT} with the specified window type and resolution.
     * <p>
     *     Although the FFT resolution can be any size it is usually a power of 2. </br>
     *     A window size (resolution) in the range [2^11, 2^15] is recommended.
     * </p>
     *
     * @param fftResolution                 the FFT resolution a.k.a the window size
     * @param type                          the window type
     * @throws IllegalArgumentException     if fftResolution is <= 0
     */
    public FFT(int fftResolution, @NonNull WindowType type) {
        this(fftResolution);
        win = new Window(type);
    }

    /**
     * <p>
     *     Computes the FFT of real input data.
     *     Sample input data is filtered with the specified window.
     *     Samples are automatically zero-padded if the sample size is not a power of 2.
     *     Returns only first half of DFT spectrum, second half is ignored because of symmetry.
     * </p>
     *
     * @param samples   the sample block to transform
     * @return          the transformed data
     */
    public float[] getForwardTransform(float[] samples) {
        float[] weightedSamples = applyWindowToSamples(samples);
        int paddingLength = getPaddingLength(weightedSamples.length);
        // Zero-padding if necessary
        if (paddingLength > 0) {
            weightedSamples = getZeroPaddedSamples(samples, paddingLength);
        }
        FloatFFT_1D fft = new FloatFFT_1D(weightedSamples.length);
        fft.realForward(weightedSamples);
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
     * @param samples   an array of {@code float}
     * @return          the transformed data in an array of {@code float}
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
     *     Computes the inverse FFT of real input data and returns the result in a {@code float} array.
     * </p>
     *
     * @param spectrum  an array containing the data to transform
     * @param scale     if true scaling is performed
     * @return          the transformed data in an array of {@code float}
     */
    public float[] getBackwardTransform(float[] spectrum, boolean scale) {
        FloatFFT_1D ifft = new FloatFFT_1D(spectrum.length);
        ifft.complexInverse(spectrum, scale);
        return spectrum;
    }

    /**
     * Returns the window type used by this instance of {@code FFT}.
     *
     * @return  the window type
     */
    public String getWindowType() {
        return win.toString();
    }

    /**
     * Computes and returns the necessary padding length.
     *
     * @param sampleLength  the length of the sample block
     * @return              the padding length to be applied to the input samples
     *                      prior to transformation
     * @throws IllegalArgumentException     if sampleLength is less than 0
     */
    private int getPaddingLength(int sampleLength) throws IllegalArgumentException {
        if (sampleLength < 0) {
            throw new IllegalArgumentException("Illegal sample length: " + sampleLength);
        }
        int exponent = (int) Math.ceil(Math.log(sampleLength) / Math.log(2));
        /*
        if (sampleLength >= fftWindowSize) {
            return sampleLength;
        }
        */
        return ((int) Math.pow(2, exponent)) - sampleLength;
        //return fftWindowSize - sampleLength;
    }

    /**
     * Adds zero padding of specified length to a sample block.
     *
     * @param samples       the input sample block
     * @param paddingLength the padding length
     * @return              zero padded sample block
     * @throws IllegalArgumentException     if paddingLength less than 0
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
     * Applies the window function to the sample block.
     *
     * @param samples   input samples
     * @return          the samples weighted by the window function
     */
    private float[] applyWindowToSamples(float[] samples) {
        float[] window = win.getWindow(samples.length);
        for (int i = 0; i < samples.length; i++) {
            samples[i] = samples[i] * window[i];
        }
        return samples;
    }

}
