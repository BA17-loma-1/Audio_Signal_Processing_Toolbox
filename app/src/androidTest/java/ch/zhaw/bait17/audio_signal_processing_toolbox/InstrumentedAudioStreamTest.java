package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Created by georgrem, stockan1 on 25.02.2017.
 *
 * Instrumented tests of the AudioStream class.
 * @see ch.zhaw.bait17.audio_signal_processing_toolbox.AudioStream
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedAudioStreamTest {

    // Context of the app under test.
    private static Context context = InstrumentationRegistry.getTargetContext();
    private static final int AUDIO_RESOURCE_ID = R.raw.sawtooth;
    private WaveDecoder decoder;
    private AudioStream as;

    public InstrumentedAudioStreamTest() throws DecoderException {
        decoder = new WaveDecoder(context.getResources().openRawResource(AUDIO_RESOURCE_ID));
        as = new AudioStream(decoder.getHeader(), decoder.getFloat(), new PlaybackListener() {
            @Override
            public void onProgress(int progress) {

            }
            @Override
            public void onCompletion() {

            }

            @Override
            public void onAudioDataReceived(float[] data) {

            }
        });
    }

    /**
     * Sets up test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp() throws DecoderException {

    }

    @Test
    public void testNotPlaying() {
        assertFalse(as.isPlaying());
    }

    @Test
    public void testPlayFull() throws InterruptedException {
        as.start();
        while (as.isPlaying()) {
            Thread.sleep(100);
        }
    }

}
