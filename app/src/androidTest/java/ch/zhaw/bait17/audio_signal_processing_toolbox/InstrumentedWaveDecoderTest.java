package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by georgrem, stockan1 on 21.02.2017.
 *
 * Instrumented tests of the WaveDecoder class.
 * @see ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder
 */
public class InstrumentedWaveDecoderTest {

    // Context of the app under test.
    private static Context context = InstrumentationRegistry.getTargetContext();
    private static List<InputStream> audioResources = new ArrayList<>();
    private WaveDecoder decoder;
    private static final int LINEAR_PCM_ENCODING = AudioCodingFormat.LINEAR_PCM.getValue();
    private static final int CHANNELS = 1;
    private static final int SAMPLE_RATE = 48000;
    private static final int BITS_PER_SAMPLE = 16;

    static {
        audioResources.add(context.getResources().openRawResource(R.raw.sawtooth));
        audioResources.add(context.getResources().openRawResource(R.raw.square));
        audioResources.add(context.getResources().openRawResource(R.raw.sine));
    }

    /**
     * Sets up test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp() throws DecoderException {
        decoder = new WaveDecoder(context.getResources().openRawResource(R.raw.sawtooth));
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
    public void testDataSizeNotNull() {
        assertNotEquals(0, decoder.getHeader().getDataSize());
    }

    @Test
    public void testGetRawPCM() throws DecoderException, IOException {
        for (InputStream in : audioResources) {
            decoder = new WaveDecoder(in);
            float[] pcm = decoder.getRawPCM();
            assertNotNull(pcm);
            assertTrue(pcm.length != 0);
        }
    }

}
