package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.zhaw.bait17.audio_signal_processing_toolbox.player.PlaybackListener;

import static org.junit.Assert.*;

/**
 * Created by georgrem, stockan1 on 25.02.2017.
 *
 * Instrumented tests of the AudioStream class.
 * @see AudioPlayer
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedAudioStreamTest {

    // Context of the app under test.
    private static Context context = InstrumentationRegistry.getTargetContext();
    private static final int AUDIO_RESOURCE_ID = R.raw.sawtooth;
    private WaveDecoder decoder;
    private AudioPlayer audioPlayer;

    public InstrumentedAudioStreamTest() throws DecoderException {
        decoder = new WaveDecoder(context.getResources().openRawResource(AUDIO_RESOURCE_ID));
        audioPlayer = new AudioPlayer(decoder.getShort(),
                decoder.getHeader().getSampleRate(),
                decoder.getHeader().getChannels(), new PlaybackListener() {
            @Override
            public void onProgress(int progress) {

            }
            @Override
            public void onCompletion() {

            }

            @Override
            public void onAudioDataReceived(short[] data) {

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
        assertFalse(audioPlayer.isPlaying());
    }

    @Test
    public void testPlayFull() throws InterruptedException {
        audioPlayer.play();
        Thread.sleep(3000);
        audioPlayer.stop();
    }

}
