package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @see <a href="http://www.vogella.com/tutorials/AndroidTesting/article.html#androidtesting">Developing Android unit and instrumentation tests</a>
 */

@RunWith(AndroidJUnit4.class)
public class InstrumentedFFTTest {

    private static final double TOLERANCE = 1e-6;
    private static final int SAMPLING_FREQUENCY = 48000;
    private static final int LOOKUP_TABLE_SIZE = 1024;
    private static final double DELTA_FREQUENCY = SAMPLING_FREQUENCY / LOOKUP_TABLE_SIZE;

    // Context of the app under test.
    private Context context = InstrumentationRegistry.getTargetContext();

    /**
     * Sets up test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp() {

    }

    @Test
    public void useAppContext() throws Exception {
        assertEquals("ch.zhaw.bait17.audio_signal_processing_toolbox", context.getPackageName());
    }

    @Test
    public void testSineWaveTransform() throws Exception {
        int waveformResource = R.raw.waveform_m1;
        double[] signal = getSamples(waveformResource);
        FFT fft = new FFT(WindowType.RECTANGLE);
        double[] fullDFT = fft.getForwardTransform(signal);
        // Only lower half of DFT is of interest
        double[] dft = new double[fullDFT.length/2];
        System.arraycopy(fullDFT, 0, dft, 0, fullDFT.length/2);
        // Can't use Arrays.stream(dft).max().getAsDouble() in API 19  :(
        double max = Double.MIN_VALUE;
        double bin = 0;
        for (int i = 0; i < dft.length; i++) {
            if (dft[i] > max) {
                max = dft[i];
                bin = i/2;
            }
        }
        double frequency = bin * DELTA_FREQUENCY;
        assertEquals(DELTA_FREQUENCY, frequency, TOLERANCE);
    }

    private double[] getSamples(int resource) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(resource)));
        List<Double> samples = new ArrayList<>();
        for (String s : br.readLine().split(",")) {
            samples.add(Double.parseDouble(s));
        }
        br.close();

        double[] signal = new double[samples.size()];
        Iterator<Double> iter = samples.iterator();
        int index = 0;
        while (iter.hasNext()) {
            signal[index] = (double) iter.next();
            index++;
        }
        return signal;
    }

}
