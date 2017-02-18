package ch.zhaw.bait17.audio_signal_processing_toolbox;

import java.util.Arrays;

/**
 * Created by georgrem, stockan1 on 18.02.2017.
 *
 * Creates a window used in the Fast Fourier Transform.
 *  @see ch.zhaw.bait17.audio_signal_processing_toolbox.FFT
 *
 * Consult enum {@link ch.zhaw.bait17.audio_signal_processing_toolbox.WindowType} for all supported
 * window types.
 */

public class Window {

    private WindowType windowType;

    public Window(WindowType type) {
        windowType = type;
    }

    /**
     * Creates a window of specified type with length L = size and returns it as a double array.
     * @param size Size of the window.
     * @return a double array containing the coefficients of the window.
     */
    public double[] getWindow(int size) {
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

    @Override
    public String toString() {
        return windowType.toString();
    }

    /**
     * Creates a rectangular window with length L = size.
     * @param size Size of the window
     * @return a double array containing only coefficients of value 1.0.
     */
    private double[] getRectangularWindow(int size) {
        double[] rectWindow = new double[size];
        Arrays.fill(rectWindow, 1.0d);
        return rectWindow;
    }

    /**
     * Creates a Hamming window with length L = size.
     * See: http://mathworks.com/help/signal/ref/hamming.html
     * @param size Size of the window
     * @return a double array containing the coefficients of the window
     */
    private double[] getHammingWindow(int size) {
        double alpha = 0.53836;
        double beta = 1 - alpha;
        double[] hammingWindow = new double[size];
        for (int n = 0; n < size; n++) {
            hammingWindow[n] = alpha - (beta * Math.cos(2 * Math.PI * n / (size - 1)));
        }
        return hammingWindow;
    }

    /**
     * Creates a Hann window with length L = size.
     * See: https://ch.mathworks.com/help/signal/ref/hann.html
     * @param size Size of the window
     * @return a double array containing the coefficients of the window
     */
    private double[] getHannWindow(int size) {
        double[] hannWindow = new double[size];
        for (int n = 0; n < size; n++) {
            hannWindow[n] = 0.5 * (1 - Math.cos(2 * Math.PI * n / (size - 1)));
        }
        return hannWindow;
    }

    /**
     * Creates a Blackman window with length L = size.
     * The Blackman window is useful for single tone measurement.
     * See: http://zone.ni.com/reference/en-XX/help/371361E-01/lvanlsconcepts/char_smoothing_windows/
     * @param size Size of the window
     * @return a double array containing the coefficients of the window
     */
    private double[] getBlackmanWindow(int size) {
        double[] blackmanWindow = new double[size];
        for (int n = 0; n < size; n++) {
            blackmanWindow[n] = 0.42 - (0.5 * Math.cos(2 * Math.PI * n / (size - 1)))
                                     + (0.08 * Math.cos(4 * Math.PI * n / (size - 1)));
        }
        return blackmanWindow;
    }

}
