package ch.zhaw.bait17.audio_signal_processing_toolbox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.fft.Window;
import ch.zhaw.bait17.audio_signal_processing_toolbox.fft.WindowType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by georgrem, stockan1 on 18.02.2017.
 *
 * Local unit tests of the Window class.
 * @see Window
 */

@RunWith(Parameterized.class)
public class WindowTest {

    private int sampleSize;
    private static final float BLACKMAN_COEFFICIENT_0 = (float) (0.42 - 0.5 + 0.08);
    private static final double HAMMING_ALPHA = 0.53836;
    private static final double HAMMING_BETA = 1 - HAMMING_ALPHA;
    private List<Window> windows = new ArrayList<>();

    /**
     * Constructor.
     * The JUnit test runner will instantiate this class once for every element in the Collection
     * returned by the method annotated with @Parameterized.Parameters.
     * @param sampleSize
     */
    public WindowTest(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * Test data generator.
     * This method is called by the JUnit parameterized test runner and returns a Collection of Integers.
     * Each Integer in the Collection corresponds to a parameter in the constructor.
     * @return A collection of sample sizes as integers
     */
    @Parameterized.Parameters
    public static Collection<Integer> generatedData() {
        return Arrays.asList(new Integer[]{7, 8, 64, 127, 128});
    }

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
        int windowSize = sampleSize;
        for (Window window : windows) {
            assertEquals(window.getWindow(windowSize).length, sampleSize);
        }
    }

    @Test
    public void testWindowSizeNotEqualSampleSize() {
        int windowSize = sampleSize / 2;
        for (Window window : windows) {
            assertNotEquals(window.getWindow(windowSize).length, sampleSize);
        }
    }

    /**
     * All coefficients must be 1.
     */
    @Test
    public void testRectangularWindow() {
        float[] rect = new float[0];
        for (Window window : windows) {
            if (window.toString().equals(WindowType.RECTANGLE.toString())) {
                System.out.println("Window type: rectangular");
                rect = window.getWindow(sampleSize);
            }
        }
        // Can't use DoubleStream in API 19  :(
        float sum = 0;
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
                float[] blackman = window.getWindow(sampleSize);
                assertTrue(Float.compare(blackman[0], BLACKMAN_COEFFICIENT_0) == 0);
            }
        }
    }

    @Test
    public void testFirstAndLastCoefficientHammingWindow() {
        for (Window window : windows) {
            if (window.toString().equals(WindowType.HAMMING.toString())) {
                System.out.println("Window type: hamming");
                float[] hamming = window.getWindow(sampleSize);
                assertTrue(Float.compare(hamming[0], (float) (HAMMING_ALPHA - HAMMING_BETA)) == 0);
                assertTrue(Float.compare(hamming[hamming.length-1], (float) (HAMMING_ALPHA - HAMMING_BETA)) == 0);
            }
        }
    }

    /**
     * First and last window coefficients must be 0.
     */
    @Test
    public void testFirstAndLastCoefficientHannWindow() {
        for (Window window : windows) {
            if (window.toString().equals(WindowType.HANN.toString())) {
                System.out.println("Window type: " + window.toString());
                float[] hann = window.getWindow(sampleSize);
                assertTrue(hann[0] == 0.0f);
                assertTrue(hann[hann.length-1] == 0.0f);
            }
        }
    }

    /**
     * Applies to all windows but Blackman
     */
    @Test
    public void testCenterCoefficientLessOrEqualOne() {
        int center = sampleSize / 2;
        if (sampleSize % 2 != 0) {
            center = (sampleSize-1) / 2;
        }
        System.out.println("Center n = " + center);
        for (Window window : windows) {
            if (!(window.toString().equals(WindowType.BLACKMAN.toString()))) {
                float[] win = window.getWindow(sampleSize);
                assertTrue(Float.compare(win[center], 1.0f) <= 0);
                if (sampleSize % 2 == 0) {
                    assertTrue(Float.compare(win[center], win[center-1]) == 0);
                }
            }
        }
    }

    /**
     * Check if windows are normalised.
     */
    @Test
    public void testAllWindowsNormalised() {
        for (Window window : windows) {
            float[] win = window.getWindow(sampleSize);
            for (int i = 0; i < win.length; i++) {
                assertTrue(Float.compare(win[i], 1.0f) <= 0);
            }
        }
    }

}
