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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device (emulator).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @see <a href="http://www.vogella.com/tutorials/AndroidTesting/article.html#androidtesting">Developing Android unit and instrumentation tests</a>
 */

@RunWith(AndroidJUnit4.class)
public class InstrumentedFFTTest {

    // Context of the app under test.
    private Context context = InstrumentationRegistry.getTargetContext();
    private static final Map<Integer,Integer> WAVEFORM_RESOURCES = new HashMap<>();
    private static final double TOLERANCE = 1e-6;
    private static final int SAMPLING_FREQUENCY = 48000;
    private static final int LOOKUP_TABLE_SIZE = 256;
    private static final double DELTA_FREQUENCY = 1.0 * SAMPLING_FREQUENCY / LOOKUP_TABLE_SIZE;

    static {
        WAVEFORM_RESOURCES.put(1, R.raw.waveform_m1);
        WAVEFORM_RESOURCES.put(2, R.raw.waveform_m2);
        WAVEFORM_RESOURCES.put(10, R.raw.waveform_m10);
        WAVEFORM_RESOURCES.put(25, R.raw.waveform_m25);
    }

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
    public void testSineWaveTransformM1() throws Exception {
        for (Map.Entry<Integer,Integer> entry : WAVEFORM_RESOURCES.entrySet()) {
            float[] signal = getSamples(entry.getValue());
            FFT fft = new FFT(WindowType.RECTANGLE);
            float[] fullDFT = fft.getForwardTransform(signal);
            // Only lower half of DFT is of interest
            float[] dft = new float[fullDFT.length / 2];
            System.arraycopy(fullDFT, 0, dft, 0, fullDFT.length / 2);
            // Can't use Arrays.stream(dft).max().getAsDouble() in API 19  :(
            float max = Float.MIN_VALUE;
            int bin = 0;
            for (int i = 0; i < dft.length; i++) {
                if (dft[i] > max) {
                    max = dft[i];
                    bin = i / 2;
                }
            }
            double frequency = bin * DELTA_FREQUENCY;
            assertEquals(entry.getKey() * DELTA_FREQUENCY, frequency, TOLERANCE);
        }
    }

    private float[] getSamples(int resource) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(resource)));
        List<Float> samples = new ArrayList<>();
        for (String s : br.readLine().split(",")) {
            samples.add(Float.parseFloat(s));
        }
        br.close();

        float[] signal = new float[samples.size()];
        Iterator<Float> iter = samples.iterator();
        int index = 0;
        while (iter.hasNext()) {
            signal[index++] = (float) iter.next();
        }
        return signal;
    }

}
