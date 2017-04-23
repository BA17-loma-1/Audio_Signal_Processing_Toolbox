package ch.zhaw.bait17.audio_signal_processing_toolbox;

import java.util.Arrays;

/**
 * Creates a window used in the Fast Fourier Transform.
 *  @see ch.zhaw.bait17.audio_signal_processing_toolbox.FFT
 *
 * Consult enum {@link ch.zhaw.bait17.audio_signal_processing_toolbox.WindowType} for all supported
 * window types.
 *
 * @author georgrem, stockan1
 */

public class Window {

    private WindowType windowType;

    public Window(WindowType type) {
        windowType = type;
    }

    /**
     * Creates a window of specified type with length L = size and returns it as a float array.
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    public float[] getWindow(int size) {
        switch (windowType) {
            case HANN:
                return getHannWindow(size);
            case HAMMING:
                return getHammingWindow(size);
            case BLACKMAN:
                return getBlackmanWindow(size);
            default:
                return getRectangularWindow(size);
        }
    }

    /**
     * Returns the window type.
     *
     * @return  {@code WindowType}
     */
    public WindowType getWindowType() {
        return windowType;
    }

    @Override
    public String toString() {
        return windowType.toString();
    }

    /**
     * Creates a rectangular window with length L = size.
     *
     * @param size  size of the window
     * @return      a {@code float} array containing only coefficients of value 1.0
     */
    private float[] getRectangularWindow(int size) {
        float[] rectWindow = new float[size];
        Arrays.fill(rectWindow, 1.0f);
        return rectWindow;
    }

    /**
     * Creates a Hamming window with length L = size.
     * See <a href="http://mathworks.com/help/signal/ref/hamming.html">mathworks.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getHammingWindow(int size) {
        double alpha = 0.53836;
        double beta = 1 - alpha;
        float[] hammingWindow = new float[size];
        for (int n = 0; n < size; n++) {
            hammingWindow[n] = (float) (alpha - (beta * Math.cos(2 * Math.PI * n / (size - 1))));
        }
        return hammingWindow;
    }

    /**
     * Creates a Hann window with length L = size.
     * See <a href="https://ch.mathworks.com/help/signal/ref/hann.html">mathworks.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getHannWindow(int size) {
        float[] hannWindow = new float[size];
        for (int n = 0; n < size; n++) {
            hannWindow[n] = (float) (0.5 * (1 - Math.cos(2 * Math.PI * n / (size - 1))));
        }
        return hannWindow;
    }

    /**
     * Creates a Blackman window with length L = size.
     * The Blackman window is useful for single tone measurement.
     * See <a href="http://zone.ni.com/reference/en-XX/help/371361E-01/lvanlsconcepts/char_smoothing_windows/">zone.ni.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getBlackmanWindow(int size) {
        float[] blackmanWindow = new float[size];
        for (int n = 0; n < size; n++) {
            blackmanWindow[n] = (float) (0.42 - (0.5 * Math.cos(2 * Math.PI * n / (size - 1)))
                                     + (0.08 * Math.cos(4 * Math.PI * n / (size - 1))));
        }
        return blackmanWindow;
    }

}
