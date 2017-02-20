package ch.zhaw.bait17.audio_signal_processing_toolbox;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by georgrem, stockan1 on 18.02.2017.
 *
 * Local unit tests of the FFT class.
 * @see ch.zhaw.bait17.audio_signal_processing_toolbox.FFT
 */
public class FFTTest {

    private static final double TOLERANCE = 1e-6;
    private static final int SAMPLE_SIZE = 8;
    private FFT fft = null;
    private double[] signal;

    /**
     * Sets up test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp() {
        signal = new double[SAMPLE_SIZE];
    }

    @Test
    public void testFFTWithHannWindow() {
        fft = new FFT(WindowType.HANN);
    }

    @Test
    public void testFFTWithHammingWindow() {
        fft = new FFT(WindowType.HAMMING);
    }

    @Test
    public  void testFFTWithBlackmanWindow() {
        fft = new FFT(WindowType.BLACKMAN);
    }

    /**
     * No window means rectangular window.
     */
    @Test
    public void testFFTEvenSampleLengthWithoutWindow() {
        fft = new FFT(WindowType.RECTANGLE);
        signal = new double[]{2,0,3,0,0,0,3,0};
        final double[] EXPECTED_DFT_REAL = new double[]{8,2,-4,2};
        final double[] EXPECTED_DFT_IMAG = new double[]{0,0,0,0};
        double[] dft = fft.getForwardTransform(signal);
        double[] Re = new double[4];
        double[] Im = new double[4];
        // Im[0] is 0, although JTransforms returns dft[1] as Re[n/2] = dft[0], which is DC value.
        for (int i = 0; i < dft.length; i++) {
            if (i % 2 == 0 && i >= 0 && i < dft.length) {
                Re[i/2] = dft[i];
            } else if (i % 2 != 0 && i > 2 && i < dft.length) {
                Im[(i-1)/2] = dft[i];
            }
            //System.out.println(dft[i]);
        }
        assertArrayEquals(Re, EXPECTED_DFT_REAL, TOLERANCE);
        assertArrayEquals(Im, EXPECTED_DFT_IMAG, TOLERANCE);
    }

    /**
     * No window means rectangular window.
     */
    @Test
    public void testFFTOddSampleLengthWithoutWindow() {
        fft = new FFT(WindowType.RECTANGLE);
        signal = new double[]{1,2,2,2,2,2,2};
        final double[] EXPECTED_DFT_REAL = new double[]{13,-1,-1,-1};
        final double[] EXPECTED_DFT_IMAG = new double[]{0,0,0,0};
        double[] dft = fft.getForwardTransform(signal);
        double[] Re = new double[4];
        double[] Im = new double[4];
        for (int i = 0; i < dft.length; i++) {
            if (i % 2 == 0 && i >= 0 && i < dft.length+1) {
                Re[i/2] = dft[i];
            } else if (i % 2 != 0 && i > 0 && i < dft.length-1) {
                Im[(i-1)/2] = dft[i];
            }
            //System.out.println(dft[i]);
        }
        assertArrayEquals(Re, EXPECTED_DFT_REAL, TOLERANCE);
        assertArrayEquals(Im, EXPECTED_DFT_IMAG, TOLERANCE);
    }

    private void fillSamplesWithZeros() {
        Arrays.fill(signal, 0.0d);
    }

    private void fillSamplesWithOnes() {
        Arrays.fill(signal, 1.0d);
    }

}
