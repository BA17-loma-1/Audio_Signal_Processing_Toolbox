package ch.zhaw.bait17.audio_signal_processing_toolbox;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by georgrem, stockan1 on 18.02.2017.
 *
 * Local unit tests of the FFT class.
 * @see ch.zhaw.bait17.audio_signal_processing_toolbox.Window
 */
public class WindowTest {

    private static final int SAMPLE_SIZE = 127;
    private static final double BLACKMAN_COEFFICIENT_0 = 0.42 - 0.5 + 0.08;
    private static final double HAMMING_ALPHA = 0.53836;
    private static final double HAMMING_BETA = 1 - HAMMING_ALPHA;
    private List<Window> windows = new ArrayList<>();

    /**
     * Sets up test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp() {
        windows.clear();
        for (WindowType type : WindowType.values()) {
            windows.add(new Window(type));
        }
    }

    @Test
    public void testWindowTypeNotEmptyString() {
        for (Window window : windows) {
            assertFalse(window.toString().equals(""));
        }
    }

    @Test
    public void testWindowSizeEqualSampleSize() {
        int windowSize = SAMPLE_SIZE;
        for (Window window : windows) {
            assertEquals(window.getWindow(windowSize).length, SAMPLE_SIZE);
        }
    }

    @Test
    public void testWindowSizeNotEqualSampleSize() {
        int windowSize = SAMPLE_SIZE / 2;
        for (Window window : windows) {
            assertNotEquals(window.getWindow(windowSize).length, SAMPLE_SIZE);
        }
    }

    /**
     * All coefficients must be 1.
     */
    @Test
    public void testRectangularWindow() {
        double[] rect = new double[0];
        for (Window window : windows) {
            if (window.toString().equals(WindowType.RECTANGLE.toString())) {
                System.out.println("Window type: rectangular");
                rect = window.getWindow(SAMPLE_SIZE);
            }
        }
        // Can't use DoubleStream in API 19  :(
        double sum = 0;
        for (int i = 0; i < rect.length; i++) {
            sum += rect[i];
            assertEquals(rect[i], 1.0, 0.0);
        }
        assertEquals(sum, rect.length, 0.0);
    }

    @Test
    public void testBlackmanWindow() {
        for (Window window : windows) {
            if (window.toString().equals(WindowType.BLACKMAN.toString())) {
                System.out.println("Window type: blackman");
                double[] blackman = window.getWindow(SAMPLE_SIZE);
                assertTrue(Double.compare(blackman[0], BLACKMAN_COEFFICIENT_0) == 0);
            }
        }
    }

    @Test
    public void testFirstAndLastCoefficientHammingWindow() {
        for (Window window : windows) {
            if (window.toString().equals(WindowType.HAMMING.toString())) {
                System.out.println("Window type: hamming");
                double[] hamming = window.getWindow(SAMPLE_SIZE);
                assertTrue(Double.compare(hamming[0], HAMMING_ALPHA - HAMMING_BETA) == 0);
                assertTrue(Double.compare(hamming[hamming.length-1], HAMMING_ALPHA - HAMMING_BETA) == 0);
            }
        }
    }

    @Test
    public void testFirstAndLastCoefficientHannWindow() {
        for (Window window : windows) {
            if (window.toString().equals(WindowType.HANN.toString())) {
                System.out.println("Window type: " + window.toString());
                double[] hann = window.getWindow(SAMPLE_SIZE);
                assertTrue(hann[0] == 0.0);
                assertTrue(hann[hann.length-1] == 0.0);
            }
        }
    }

    /**
     * Applies to all windows but Blackman
     */
    @Test
    public void testCenterCoefficientEqualsOne() {
        int center = (SAMPLE_SIZE-1) / 2;
        System.out.println("Center n = " + center);
        for (Window window : windows) {
            if (!(window.toString().equals(WindowType.BLACKMAN.toString()))) {
                double[] win = window.getWindow(SAMPLE_SIZE);
                assertTrue(Double.compare(win[center], 1.0) == 0);
            }
        }
    }

    /**
     * Check if windows are normalised.
     */
    @Test
    public void testAllWindowsNormalised() {
        for (Window window : windows) {
            double[] win = window.getWindow(SAMPLE_SIZE);
            for (int i = 0; i < win.length; i++) {
                assertTrue(Double.compare(win[i], 1.0) <= 0);
            }
        }
    }

}
