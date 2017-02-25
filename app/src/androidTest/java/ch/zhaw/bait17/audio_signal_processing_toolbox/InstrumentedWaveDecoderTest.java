package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.*;

/**
 * Created by georgrem, stockan1 on 21.02.2017.
 *
 * Instrumented tests of the WaveDecoder class.
 * @see ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder
 */
@RunWith(Parameterized.class)
public class InstrumentedWaveDecoderTest {

    // Context of the app under test.
    private static Context context = InstrumentationRegistry.getTargetContext();
    private WaveDecoder decoder;
    private static final int LINEAR_PCM_ENCODING = AudioCodingFormat.LINEAR_PCM.getValue();
    private static final int CHANNELS = 1;
    private static final int SAMPLE_RATE = 48000;
    private static final int BITS_PER_SAMPLE = 16;
    private int idResource;

    /**
     * Constructor.
     * The JUnit test runner will instantiate this class once for every element in the Collection
     * returned by the method annotated with @Parameterized.Parameters.
     * @param idResource
     */
    public InstrumentedWaveDecoderTest(int idResource) {
        this.idResource = idResource;
    }

    /**
     * Test data generator.
     * This method is called by the JUnit parameterized test runner and returns a Collection of Integers.
     * Each Integer in the Collection corresponds to a parameter in the constructor.
     * @return A collection of resource id's
     */
    @Parameterized.Parameters
    public static Collection<Integer> generatedData() {
        return Arrays.asList(new Integer[]{
                R.raw.sawtooth,
                R.raw.square,
                R.raw.sine
        });
    }

    /**
     * Sets up test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp() throws DecoderException {
        decoder = new WaveDecoder(context.getResources().openRawResource(idResource));
    }

    @Test
    public void testHeaderNotNull() {
        assertNotNull(decoder.getHeader());
    }

    @Test
    public void testEncodingFormat() {
        assertEquals(LINEAR_PCM_ENCODING, decoder.getHeader().getEncodingFormat());
    }

    @Test
    public void testChannels() {
        assertEquals(CHANNELS, decoder.getHeader().getChannels());
    }

    @Test
    public void testSampleRate() {
        assertEquals(SAMPLE_RATE, decoder.getHeader().getSampleRate());
    }

    @Test
    public void testBitsPerSample() {
        assertEquals(BITS_PER_SAMPLE, decoder.getHeader().getBitsPerSample());
    }

    @Test
    public void testBytesPerSample() {
        assertTrue(decoder.getHeader().getBytesPerSample() > 0);
    }

    @Test
    public void testDataSizeNotNull() {
        assertNotEquals(0, decoder.getHeader().getDataSize());
    }

    @Test
    public void testGetRawPCM() throws DecoderException, IOException {
        byte[] pcm = decoder.getRawPCM();
        assertNotNull(pcm);
        assertTrue(pcm.length != 0);
    }

    @Test
    public void testGetFloat() throws DecoderException, IOException {
        float[] values = decoder.getFloat();
        assertNotNull(values);
        assertTrue(values.length != 0);
    }

}
