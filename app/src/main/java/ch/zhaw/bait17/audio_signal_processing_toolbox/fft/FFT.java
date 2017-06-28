package ch.zhaw.bait17.audio_signal_processing_toolbox.fft;

import android.support.annotation.NonNull;

import org.jtransforms.fft.FloatFFT_1D;

import ch.zhaw.bait17.audio_signal_processing_toolbox.util.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.PCMUtil;

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

    private WindowType windowType = ApplicationContext.getPreferredWindow();
    private int fftResolution = ApplicationContext.getPreferredFFTResolution();
    private FloatFFT_1D fft_1D;
    private int sampleSize = 0;
    private float[] window = null;

    /**
     * Creates an instance of {@code FFT} with a Hamming window and default FFT resolution.
     */
    public FFT() {
        initialise();
    }

    /**
     * Creates an instance of {@code FFT} with the specified resolution and a
     * Hamming window by default.
     * <p>
     *     Although the FFT resolution can be any size it is usually a power of 2. </br>
     *     A window size (resolution) in the range [2^11, 2^15] is recommended.
     * </p>
     *
     * @param fftResolution                 the FFT resolution a.k.a the window size
     * @throws IllegalArgumentException     if fftResolution is <= 0
     */
    public FFT(int fftResolution) {
        if (fftResolution <= 0) {
            throw new IllegalArgumentException("FFT resolution must be greater than 0.");
        }
        this.fftResolution = fftResolution;
        initialise();
    }

    /**
     * Creates an instance of {@code FFT} with the specified window and default FFT resolution.
     *
     * @param windowType      the window type used to weigh the samples
     */
    public FFT(@NonNull WindowType windowType) {
        this.windowType = windowType;
        initialise();
    }

    /**
     * Creates an instance of {@code FFT} with the specified window type and resolution.
     * <p>
     *     Although the FFT resolution can be any size it is usually a power of 2. </br>
     *     A window size (resolution) in the range [2^11, 2^15] is recommended.
     * </p>
     *
     * @param fftResolution                 the FFT resolution a.k.a the window size
     * @param windowType                    the window type
     * @throws IllegalArgumentException     if fftResolution is <= 0
     */
    public FFT(int fftResolution, @NonNull WindowType windowType) {
        this(fftResolution);
        this.windowType = windowType;
    }

    /**
     * <p>
     *      Computes the power spectral density of the given audio samples. <br>
     *      The power spectral density is sometimes simply called power spectrum.
     * </p>
     * <p>
     *     Power spectral density is computed only for one channel (mono - left).
     * </p>
     *
     * @param samples   a block of PCM samples
     * @param channels  the number of channels contained in the PCM samples
     * @return          power spectrum
     * @throws IllegalArgumentException     - if samples block contains two channels
     *                                        but the block length is odd
     *                                      - if channels < 1 or channels > 2
     */
    public float[] getPowerSpectrum(@NonNull short[] samples, int channels) {
        if (channels < 1 || channels > 2) {
            throw new IllegalArgumentException("Channels must be 1 or 2.");
        }
        if (channels != 1 && samples.length % 2 != 0) {
            // Stereo, but sample block length is odd
            throw new IllegalArgumentException("Stereo: Sample block length must be even.");
        }

        final int windowSize = fftResolution;

        if (samples.length > windowSize) {
            /*
                We can't process the samples because the FFT resolution is too small.
                One solution would be to cut sample block into smaller pieces and process FFT.
                However, it is better to increase the fft resolution for better accuracy in the
                frequency domain as this is the goal for this app.
             */
            return new float[0];
        }

        if (sampleSize != samples.length || window == null) {
            onSampleSizeChanged(samples.length);
        }

        float[] x = new float[windowSize];
        float[] weightedSamples = applyWindowToSamples(PCMUtil.short2FloatArray(samples));
        System.arraycopy(weightedSamples, 0, x, 0, weightedSamples.length);

        // Compute FFT: Time domain -> Frequency domain
        fft_1D.realForward(x);
        float[] hMag = new float[windowSize / 2];
        for (int i = 0; i < windowSize / 2; i++) {
            hMag[i] = (x[2*i] * x[2*i]) + (x[(2*i) + 1] * x[(2*i) + 1]);
        }
        return hMag;
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
        if (sampleSize != samples.length || window == null) {
            onSampleSizeChanged(samples.length);
        }
        float[] weightedSamples = applyWindowToSamples(samples);
        // Zero-padding if necessary
        int paddingLength = getPaddingLength(sampleSize);
        if (paddingLength > 0) {
            weightedSamples = applyZeroPaddingToSamples(samples, paddingLength);
        }
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
        if (sampleSize != samples.length || window == null) {
            onSampleSizeChanged(samples.length);
        }
        float[] weightedSamples = applyWindowToSamples(samples);
        // Zero-padding if necessary
        int paddingLength = getPaddingLength(sampleSize);
        if (paddingLength > 0) {
            weightedSamples = applyZeroPaddingToSamples(samples, paddingLength);
        }
        FloatFFT_1D fft = new FloatFFT_1D(weightedSamples.length);
        fft.realForwardFull(weightedSamples);
        return weightedSamples;
    }

    /**
     * Returns the FFT resolution.
     *
     * @return  the FFT resolution
     */
    public int getFFTResolution() {
        return fftResolution;
    }

    /**
     * Sets the FFT resolution.
     *
     * @param fftResolution fft resolution a.k.a window size
     */
    public void setFFTResolution(int fftResolution) {
        this.fftResolution = fftResolution;
    }

    /**
     * Returns the window type used by this instance of {@code FFT}.
     *
     * @return  {@code WindowType}
     */
    public WindowType getWindowType() {
        return windowType;
    }

    /**
     * Sets the window type.
     *
     * @param windowType    the type of window to be used when applying weighting to the sample blocks
     */
    public void setWindowType(WindowType windowType) {
        this.windowType = windowType;
        createWindow();
    }

    private void initialise() {
        fft_1D = new FloatFFT_1D(fftResolution);
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
        int exponent;
        if (fftResolution <= sampleLength) {
            exponent = (int) Math.ceil(Math.log(sampleLength) / Math.log(2));
        } else {
            exponent = (int) Math.ceil(Math.log(fftResolution) / Math.log(2));
        }
        return ((int) Math.pow(2, exponent)) - sampleLength;
    }

    /**
     * Adds zero padding of specified length to a sample block.
     *
     * @param samples       the input sample block
     * @param paddingLength the padding length
     * @return              zero padded sample block
     * @throws IllegalArgumentException     if paddingLength less than 0
     */
    private float[] applyZeroPaddingToSamples(float[] samples, int paddingLength)
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
        for (int i = 0; i < samples.length; i++) {
            samples[i] = samples[i] * window[i];
        }
        return samples;
    }

    private void onSampleSizeChanged(int sampleSize) {
        this.sampleSize = sampleSize;
        createWindow();
    }

    private void createWindow() {
        if (sampleSize > 0) {
            window = new Window(windowType).getWindow(sampleSize);
        } else {
            window = null;
        }
    }
}
