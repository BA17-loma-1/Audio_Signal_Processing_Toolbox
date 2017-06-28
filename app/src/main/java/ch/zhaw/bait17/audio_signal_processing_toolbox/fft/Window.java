package ch.zhaw.bait17.audio_signal_processing_toolbox.fft;

import java.util.Arrays;

/**
 * Creates a window used in the Fast Fourier Transform.
 * @see FFT
 *
 * Consult enum {@link WindowType} for all supported
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
            case TRIANGULAR:
                return getTriangularWindow(size);
            case HANN:
                return getHannWindow(size);
            case HAMMING:
                return getHammingWindow(size);
            case BLACKMAN:
                return getBlackmanWindow(size);
            case NUTTAL:
                return getNuttalWindow(size);
            case WELCH:
                return getWelchWindow(size);
            case BARTLETT:
                return getBartlettWindow(size);
            case BARTLETT_HANN:
                return getBartlettHannWindow(size);
            case BLACKMAN_HARRIS:
                return getBlackmanHarrisWindow(size);
            case BLACKMAN_NUTTAL:
                return getBlackmanNuttalWindow(size);
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
     * Creates a triangular window with length L = size.
     * The triangular window is very similar to the Bartlett window.
     * While the Bartlett window is zero at samples 0 and L, the triangular window is nonzero
     * at those points.
     * See <a href="https://ch.mathworks.com/help/signal/r">mathworks.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getTriangularWindow(int size) {
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        float[] triangularWindow = new float[size];
        for (int n = 0; n < size; n++) {
            if (size % 2 == 0) {
                // even
                if (n >= 1 && n <= size / 2) {
                    triangularWindow[n] = ((2*n) - 1) / (float) size;
                } else {
                    triangularWindow[n] = 2 - (((2*n) - 1) / (float) size);
                }
            } else {
                // odd
                if (n >= 1 && n <= (size + 1) / 2) {
                    triangularWindow[n] = (2*n) / (float) (size + 1);
                } else {
                    triangularWindow[n] = 2 - ((2*n) / (float) (size + 1));
                }
            }
        }
        return triangularWindow;
    }

    /**
     * Creates a Hamming window with length L = size.
     * See <a href="http://mathworks.com/help/signal/ref/hamming.html">mathworks.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getHammingWindow(int size) {
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        final double alpha = 0.53836;
        final double beta = 1 - alpha;
        final int N = size - 1;
        float[] hammingWindow = new float[size];
        for (int n = 0; n < size; n++) {
            hammingWindow[n] = (float) (alpha - (beta * Math.cos(2 * Math.PI * n / (float) N)));
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
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        float[] hannWindow = new float[size];
        final int N = size - 1;
        for (int n = 0; n < size; n++) {
            hannWindow[n] = (float) (0.5 * (1 - Math.cos(2 * Math.PI * n / (float) N)));
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
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        float[] blackmanWindow = new float[size];
        final int N = size - 1;
        for (int n = 0; n < size; n++) {
            float t = n / (float) N;
            blackmanWindow[n] = (float) (0.42 - (0.5 * Math.cos(2 * Math.PI * t))
                                     + (0.08 * Math.cos(4 * Math.PI * t)));
        }
        return blackmanWindow;
    }

    /**
     * Creates a Nuttal window with length L = size.
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getNuttalWindow(int size) {
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        final double a0 = 0.355768;
        final double a1 = 0.487396;
        final double a2 = 0.144232;
        final double a3 = 0.012604;
        final int N = size - 1;
        float[] nuttalWindow = new float[size];
        for (int n = 0; n < size; n++) {
            float t = n / (float) N;
            nuttalWindow[n] = (float) (a0 - a1 * Math.cos(2 * Math.PI * t)
                    + a2 * Math.cos(4 * Math.PI * t) - a3 * Math.cos(6 * Math.PI * t));
        }
        return nuttalWindow;
    }

    /**
     * Creates a Welch window with length L = size.
     * See <a href="http://zone.ni.com/reference/en-XX/help/371361E-01/lvanlsconcepts/char_smoothing_windows/">zone.ni.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getWelchWindow(int size) {
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        float[] welchWindow = new float[size];
        final int N = size - 1;
        for (int n = 0; n < size; n++) {
            welchWindow[n] = (float) (1 - Math.pow((n - (N / 2.0f)) / (N / 2.0f), 2));
        }
        return welchWindow;
    }

    /**
     * Creates a Bartlett window with length L = size.
     * The Bartlett window is very similar to the triangular window, but has zeros at the first and
     * last samples.
     * See <a href="https://ch.mathworks.com/help/signal/ref/bartlett.html">mathworks.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getBartlettWindow(int size) {
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        float[] bartlettWindow = new float[size];
        final int N = size - 1;
        for (int n = 0; n < size; n++) {
            float t = 2*n / (float) N;
            if (n >= 0 && n <= N/2) {
                bartlettWindow[n] = t;
            } else {
                bartlettWindow[n] = 2.0f - t;
            }
        }
        return bartlettWindow;
    }

    /**
     * Creates a Bartlett-Hann window with length L = size.
     * See <a href="https://ch.mathworks.com/help/signal/ref/barthannwin.html">mathworks.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getBartlettHannWindow(int size) {
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        float[] bartlettHannWindow = new float[size];
        final int N = size - 1;
        for (int n = 0; n < size; n++) {
            float t = (n / (float) N) - 0.5f;
            bartlettHannWindow[n] = (float) (0.62 - 0.48 * Math.abs(t) + 0.38 * Math.cos(2 * Math.PI * t));
        }
        return bartlettHannWindow;
    }

    /**
     * Creates a Blackman-Harris window with length L = size.
     * See <a href="https://ch.mathworks.com/help/signal/ref/blackmanharris.html">mathworks.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getBlackmanHarrisWindow(int size) {
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        final double a0 = 0.35875;
        final double a1 = 0.48829;
        final double a2 = 0.14128;
        final double a3 = 0.01168;
        final int N = size - 1;
        float[] blackmanHarrisWindow = new float[size];
        for (int n = 0; n < size; n++) {
            float t = n / (float) N;
            blackmanHarrisWindow[n] = (float) (a0 - a1 * Math.cos(2 * Math.PI * t)
                    + a2 * Math.cos(4 * Math.PI * t) - a3 * Math.cos(6 * Math.PI * t));
        }
        return blackmanHarrisWindow;
    }

    /**
     * Creates a Blackman-Nuttal window with length L = size.
     * See <a href="https://ch.mathworks.com/help/signal/ref/nuttallwin.html">mathworks.com</a>
     *
     * @param size  size of the window
     * @return      a {@code float} array containing the coefficients of the window
     */
    private float[] getBlackmanNuttalWindow(int size) {
        if (size <= 1) {
            return getWindowOfSizeOne();
        }

        final double a0 = 0.3635819;
        final double a1 = 0.4891775;
        final double a2 = 0.1365995;
        final double a3 = 0.0106411;
        final int N = size - 1;
        float[] blackmanNuttalWindow = new float[size];
        for (int n = 0; n < size; n++) {
            float t = n / (float) N;
            blackmanNuttalWindow[n] = (float) (a0 - a1 * Math.cos(2 * Math.PI * t)
                    + a2 * Math.cos(4 * Math.PI * t) - a3 * Math.cos(6 * Math.PI * t));
        }
        return blackmanNuttalWindow;
    }

    private float[] getWindowOfSizeOne() {
        return new float[] {1};
    }
}
