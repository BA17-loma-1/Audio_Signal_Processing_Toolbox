package ch.zhaw.bait17.audio_signal_processing_toolbox;

import org.junit.Before;
import org.junit.Test;

import ch.zhaw.bait17.audio_signal_processing_toolbox.fft.FFT;
import ch.zhaw.bait17.audio_signal_processing_toolbox.fft.WindowType;

import static org.junit.Assert.assertArrayEquals;

/**
 * Local unit tests of the FFT class.
 * See class under test: {@link FFT}
 *
 * @author georgrem, stockan1.
 */
public class FFTTest {

    private static final float TOLERANCE = 1e-6f;
    private static final int SAMPLE_SIZE = 8;
    private FFT fft = null;
    private float[] signal;

    /**
     * Sets up test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp() {
        signal = new float[SAMPLE_SIZE];
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
        signal = new float[]{2,0,3,0,0,0,3,0};
        final float[] EXPECTED_DFT_REAL = new float[]{8,2,-4,2};
        final float[] EXPECTED_DFT_IMAG = new float[]{0,0,0,0};
        float[] dft = fft.getForwardTransform(signal);
        float[] Re = new float[4];
        float[] Im = new float[4];
        // Im[0] is 0, although JTransforms returns dft[1] as Re[n/2] = fs/2 = dft[0], which is DC value.
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
        signal = new float[]{1,2,2,2,2,2,2};
        final float[] EXPECTED_DFT_REAL = new float[]{13,-1,-1,-1};
        final float[] EXPECTED_DFT_IMAG = new float[]{0,0,0,0};
        float[] dft = fft.getForwardTransform(signal);
        float[] Re = new float[4];
        float[] Im = new float[4];
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

}
