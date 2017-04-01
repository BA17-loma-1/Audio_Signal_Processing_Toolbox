package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PCMSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PostFilterSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PreFilterSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.PCMUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Util;


/**
 * <p>
 * A versatile yet easy to use player facade.
 * It hides the complexity of decoding the audio source, feeding PCM samples to the audio player
 * and controlling the audio playback.
 * </p>
 *
 * @author georgrem, stockan1
 */

public class PlayerPresenter {

    private static final String TAG = PlayerPresenter.class.getSimpleName();
    private static final int QUEUE_SIZE = 70;

    private AudioDecoder mp3Decoder;
    private AudioPlayer audioPlayer;
    private Filter filter;
    private EventBus eventBus;
    private ArrayBlockingQueue<PreFilterSampleBlock> preFilterSampleBuffer;
    private ArrayBlockingQueue<PostFilterSampleBlock> postFilterSampleBuffer;
    private String currentTrack = "";
    private Thread decoderThread;
    private Thread feederThread;
    private volatile boolean keepDecoding = true;
    private volatile boolean keepFeedingSamples = true;

    public PlayerPresenter(Filter filter) {
        this.filter = filter;
        audioPlayer = new AudioPlayer();
        preFilterSampleBuffer = new ArrayBlockingQueue<>(QUEUE_SIZE);
        postFilterSampleBuffer = new ArrayBlockingQueue<>(QUEUE_SIZE);
        buildEventBus();
    }

    /**
     * Starts the audio playback.
     *
     * @param track
     */
    public void selectTrack(Track track) {
        String uri = track.getUri();
        if (currentTrack.equals(uri)) {
            if (audioPlayer.isPlaying()) {
                keepDecoding = false;
                audioPlayer.pausePlayback();
            } else {
                decode();
                audioPlayer.resumePlayback();
            }
        } else {
            currentTrack = uri;
            initialiseDecoder(currentTrack);
            decode();
        }
    }

    /**
     * Stops the audio playback.
     */
    public void stop() {
        keepFeedingSamples = false;
        keepDecoding = false;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public boolean isPlaying() {
        return audioPlayer.isPlaying();
    }

    public int getSampleRate() {
        return audioPlayer.getSampleRate();
    }

    public int getChannels() {
        return audioPlayer.getChannels();
    }

    public void seekToPosition(int msec) {

    }

    public int getCurrentPosition() {
        return 0;
    }

    private void buildEventBus() {
        eventBus = EventBus.getDefault();
    }

    private void initialiseDecoder(final String uri) {
        try {
            InputStream is = Util.getInputStreamFromURI(uri);
            mp3Decoder = new MP3Decoder(is);
        } catch (FileNotFoundException e) {
            Toast.makeText(ApplicationContext.getAppContext(), "", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAudioPlayerFeed() {
        feederThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Log.d(TAG, "Sample feed thread '" + Thread.currentThread().getName() + "' start");
                Log.d(TAG, "Sample feed start");
                while (keepFeedingSamples) {
                    if (audioPlayer.getInputBufferSize() < QUEUE_SIZE) {

                        PreFilterSampleBlock preFilterSampleBlock = preFilterSampleBuffer.poll();
                        if (preFilterSampleBlock != null) {
                            // Broadcast pre filter_view sample block using event bus
                            eventBus.post(new PreFilterSampleBlock(
                                    preFilterSampleBlock.getSamples(),
                                    preFilterSampleBlock.getSampleRate()));
                        }

                        PostFilterSampleBlock postFilterSampleBlock = postFilterSampleBuffer.poll();
                        if (postFilterSampleBlock != null) {
                            // Send the samples to the audio player input queue.
                            if (!audioPlayer.enqueueSampleBlock(postFilterSampleBlock)) {
                                Log.e(TAG, "One pcm sample block lost");
                            }
                            // Broadcast post filter_view sample block using event bus
                            eventBus.post(new PostFilterSampleBlock(
                                    postFilterSampleBlock.getSamples(),
                                    postFilterSampleBlock.getSampleRate()));
                        }
                    }
                }
                Log.d(TAG, "Sample feed stop");
                Log.d(TAG, "Sample feed thread '" + Thread.currentThread().getName() + "' stop");
            }
        };
        feederThread.start();
    }

    private void decode() {
        keepDecoding = true;
        keepFeedingSamples = true;
        decoderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Decoding thread '" + Thread.currentThread().getName() + "' start");
                Log.d(TAG, "Decoding start");

                while (keepDecoding) {
                    if (postFilterSampleBuffer.size() < QUEUE_SIZE) {
                        PCMSampleBlock pcmSampleBlock = mp3Decoder.getNextSampleBlock();
                        if (pcmSampleBlock != null) {
                            short[] samples = pcmSampleBlock.getSamples();
                            short[] frame = Arrays.copyOf(samples, samples.length);

                            preFilterSampleBuffer.offer(new PreFilterSampleBlock(
                                    frame, Constants.DEFAULT_SAMPLE_RATE));

                            PCMSampleBlock output = applyFilter(new PCMSampleBlock(frame, Constants.DEFAULT_SAMPLE_RATE));

                            postFilterSampleBuffer.offer(new PostFilterSampleBlock(
                                    output.getSamples(), Constants.DEFAULT_SAMPLE_RATE));
                        } else {
                            // No more frames to decode, we reached of the InputStream. --> quit
                            keepDecoding = false;
                            keepFeedingSamples = false;
                        }
                    }
                }
                Log.d(TAG, "Finished decoding");
                audioPlayer.stopPlayback();
                if (feederThread != null) {
                    try {
                        feederThread.join();
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                Log.d(TAG, "Decoding stop");
                Log.d(TAG, "Decoding thread '" + Thread.currentThread().getName() + "' stop");
            }
        });
        decoderThread.start();
        startAudioPlayerFeed();
        while (audioPlayer.getInputBufferSize() < AudioPlayer.getQueueSize()) ;
        audioPlayer.play();
    }

    private PostFilterSampleBlock applyFilter(PCMSampleBlock input) {
        if (filter == null) {
            return new PostFilterSampleBlock(input.getSamples(), input.getSampleRate());
        }
        float[] samples = PCMUtil.short2FloatArray(input.getSamples());
        short[] filtered = PCMUtil.float2ShortArray(filter.apply(samples));
        return new PostFilterSampleBlock(filtered, input.getSampleRate());
    }

}
