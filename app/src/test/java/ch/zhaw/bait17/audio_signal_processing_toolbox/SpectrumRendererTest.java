package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.graphics.Paint;
import ch.zhaw.bait17.audio_signal_processing_toolbox.ui.SpectrumRenderer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * Local unit tests of the FFT class.
 * @see ch.zhaw.bait17.audio_signal_processing_toolbox.ui.SpectrumRenderer
 *
 * Created by georgrem, stockan1 on 04.03.2017.
 */
public class SpectrumRendererTest {

    private SpectrumRenderer spectrumRenderer = new SpectrumRenderer(new Paint());

    /**
     * Sets up test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp() {

    }

    @Test
    public void testGetCentreFrequencies() {
        double[] centreFrequencies = spectrumRenderer.getCentreFrequencies();
        assertNotNull(centreFrequencies);
        assertTrue(centreFrequencies.length > 0);
        System.out.println(Arrays.toString(centreFrequencies));
    }

    @Test
    public void testGetThirdOctaveFrequencyBands() {
        double[] bands = spectrumRenderer.getThirdOctaveFrequencyBands();
        assertNotNull(bands);
        assertTrue(bands.length > 0);
        System.out.println(Arrays.toString(bands));
    }

}
