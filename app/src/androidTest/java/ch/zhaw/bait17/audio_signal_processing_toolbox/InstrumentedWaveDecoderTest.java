package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import ch.zhaw.bait17.audio_signal_processing_toolbox.decoder.AudioCodingFormat;
import ch.zhaw.bait17.audio_signal_processing_toolbox.decoder.WaveDecoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented tests of the {@code WaveDecoder} class.
 * See class under test: {@link WaveDecoder}
 *
 * @author georgrem, stockan1
 */

@RunWith(Parameterized.class)
public class InstrumentedWaveDecoderTest {

    // Context of the app under test.
    private static Context context = InstrumentationRegistry.getTargetContext();
    private WaveDecoder decoder = WaveDecoder.getInstance();
    private static final int LINEAR_PCM_ENCODING = AudioCodingFormat.LINEAR_PCM.getValue();
    private static final int BITS_PER_SAMPLE = 16;
    private int idResource;

    /**
     * Constructor.
     * The JUnit test runner will instantiate this class once for every element in the Collection
     * returned by the method annotated with @Parameterized.Parameters.
     * @param idResource    Resource ID
     */
    public InstrumentedWaveDecoderTest(int idResource) {
        this.idResource = idResource;
    }

    /**
     * Test data generator.
     * This method is called by the JUnit parameterized test runner and returns a Collection of Integers.
     * Each Integer in the Collection corresponds to a parameter in the constructor.
     * @return  a collection of Resource ID's
     */
    @Parameterized.Parameters
    public static Collection<Integer> generatedData() {
        return Arrays.asList(R.raw.sawtooth, R.raw.square, R.raw.cosine);
    }

    /**
     * Sets up test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp() {
        System.out.println("file under test: " + context.getResources().getString(idResource));
        decoder.setSource(context.getResources().openRawResource(idResource));
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
    public void testChannelsNotZero() {
        assertFalse(decoder.getChannels() == 0);
    }

    @Test
    public void testChannels() {
        assertTrue(decoder.getChannels() == 1 || decoder.getChannels() == 2);
    }

    @Test
    public void testSampleRateNotZero() {
        assertFalse(decoder.getSampleRate() == 0);
    }

    @Test
    public void testSampleRate() {
        assertTrue(decoder.getSampleRate() >= 8000 && decoder.getSampleRate() <= 48000);
    }

    @Test
    public void testBitsPerSampleNotZero() {
        assertFalse(decoder.getHeader().getBitsPerSample() == 0);
    }

    @Test
    public void testBitsPerSample() {
        assertEquals(BITS_PER_SAMPLE, decoder.getHeader().getBitsPerSample());
    }

    @Test
    public void testBytesPerSampleNotZero() {
        assertFalse(decoder.getHeader().getBytesPerSample() == 0);
    }

    @Test
    public void testBytesPerSample() {
        int bytesPerSample = decoder.getHeader().getBitsPerSample() * decoder.getChannels() / 8;
        assertTrue(decoder.getHeader().getBytesPerSample() == bytesPerSample);
    }

    @Test
    public void testBytesPerSecondNotZero() {
        assertFalse(decoder.getHeader().getBytesPerSecond() == 0);
    }

    @Test
    public void testBytesPerSecond() {
        int bytesPerSecond = decoder.getSampleRate() *
                decoder.getHeader().getBitsPerSample() * decoder.getChannels() / 8;
        assertTrue(decoder.getHeader().getBytesPerSecond() == bytesPerSecond);
    }

    @Test
    public void testDataSizeNotZero() {
        assertNotEquals(0, decoder.getHeader().getDataSize());
    }

    @Test
    public void testGetOneSampleBlock() {
        short[] pcm = decoder.getNextSampleBlock();
        assertNotNull(pcm);
        assertTrue(pcm.length != 0);
        System.out.println("Sample block size: " + pcm.length);
    }

    @Test
    public void testReadFullStream() {
        int totalBytesRead = 0;
        int sampleBlocksRead = 0;
        short[] pcm = null;
        while ((pcm = decoder.getNextSampleBlock()) != null) {
            assertNotNull(pcm);
            sampleBlocksRead++;
            totalBytesRead += (pcm.length * 2);
        }
        System.out.println("Sample blocks read: " + sampleBlocksRead);
        System.out.println("Total bytes read: " + totalBytesRead);
    }

}
