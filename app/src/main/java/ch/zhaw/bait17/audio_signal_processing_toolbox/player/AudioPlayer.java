package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;
import android.util.Log;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <p>Singleton with static factory.</p>
 * <p>Usage:
 * 1. Init the audio player with sample rate and number of audio channels
 * 2. Enqueue sample blocks until the internal buffer is full
 * 3. Start the playback thread</p>
 *
 * Based on: https://github.com/demantz/RFAnalyzer/blob/master/app/src/main/java/com/mantz_it/rfanalyzer/AudioSink.java
 */

public class AudioPlayer extends Thread {

    private static final String TAG = AudioPlayer.class.getSimpleName();
    private static final AudioPlayer INSTANCE = new AudioPlayer();
    private static final int QUEUE_SIZE = 8;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNELS = 1;
    private static final long QUEUE_POLL_WAIT_TIME = 100;

    private AudioTrack audioTrack;
    private boolean keepPlaying = false;
    private boolean paused = false;
    private ArrayBlockingQueue<PCMSampleBlock> inputQueue;
    private ArrayBlockingQueue<PCMSampleBlock> outputQueue;
    private int sampleRate;
    private int channels;


    private AudioPlayer() {
        inputQueue = new ArrayBlockingQueue<PCMSampleBlock>(QUEUE_SIZE);
        outputQueue = new ArrayBlockingQueue<PCMSampleBlock>(QUEUE_SIZE);
        sampleRate = DEFAULT_SAMPLE_RATE;
        channels = DEFAULT_CHANNELS;
    }

    @Override
    public void run() {
        super.run();
        Log.d(TAG, "Run thread.");
        keepPlaying = true;
        play();
        Log.d(TAG, "Thread '" + this.getName() + "' stopped.");
    }

    @Override
    public synchronized void start() {
        this.setName("AudioTrack Playback Thread (APT).");
        Log.d(TAG, "Thread '" + this.getName() + "' started.");
        super.start();
        run();
    }

    /**
     * <p>Returns the singleton instance of the stand alone audio player.</p>
     * @return {@code StandAloneAudioPlayer} instance
     */
    public static AudioPlayer getInstance() {
        return INSTANCE;
    }

    /**
     * Initialise the {@code AudioTrack} for playback.
     * @param sampleRate The sample rate
     * @param channels   The channel count
     */
    public void init(int sampleRate, int channels) {
        this.sampleRate = sampleRate;
        this.channels = channels;
        createAudioTrack();
    }

    /**
     * Add a new {@code PCMSampleBlock} to the input queue for playback.
     * @param sampleBlock A {@code PCMSampleBlock}
     * @return true if the sample block was added to the tail of the input queue
     */
    public boolean enqueueSampleBlock(@NonNull PCMSampleBlock sampleBlock) {
        try {
            return inputQueue.offer(sampleBlock, QUEUE_POLL_WAIT_TIME, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    /**
     * Clear the internal sample buffer.
     */
    public void clearSampleBuffer() {
        inputQueue.clear();
    }

    /**
     *
     * @param timeout Queue polling wait time in milliseconds.
     * @return the head sample block of the output queue, or null if the specified waiting time
     * elapses before an element is available
     */
    public PCMSampleBlock getSampleBlock(int timeout) {
        try {
            return outputQueue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "getSampleBlock failed. Returned null.");
            return null;
        }
    }

    public boolean isInputBufferFull() {
        return inputQueue.size() == QUEUE_SIZE;
    }

    public int getInputBufferSize() {
        return inputQueue.size();
    }

    /**
     * Start audio playback.
     * All associated {@code AudioTrack} resources are automatically released when this method is left.
     */
    private void play() {
        Log.d(TAG, "Play");
        audioTrack.play();
        PCMSampleBlock sampleBlock = null;
        while (keepPlaying) {
            if (paused) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while paused.");
                }
            } else {
                try {
                    if ((sampleBlock = inputQueue.poll(QUEUE_POLL_WAIT_TIME, TimeUnit.MILLISECONDS)) != null) {
                        short[] pcm = sampleBlock.getSamples();
                        if (audioTrack.write(pcm, 0, pcm.length) != pcm.length) {
                            Log.d(TAG, "AudioTrack.write encountered a problem. Stop playback.");
                            keepPlaying = false;
                        }
                        // Write the sample block back to the output queue.
                        outputQueue.offer(new PCMSampleBlock(pcm, audioTrack.getSampleRate()));
                    } else {
                        Log.d(TAG, "Empty PCM sample block.");
                    }
                } catch (InterruptedException e) {
                    keepPlaying = false;
                    Log.e(TAG, "Interrupted while polling the queue. Stop playback.");
                }
            }
        }
        keepPlaying = false;
        paused = false;
        audioTrack.stop();
        audioTrack.flush();
        audioTrack.release();
        Log.d(TAG, "AudioTrack stopped, flushed and released.");
    }

    /**
     * Pause audio playback.
     */
    public void pausePlayback() {
        Log.d(TAG, "Pause playback");
        keepPlaying = true;
        paused = true;
        audioTrack.pause();
    }

    /**
     * Resume audio playback.
     */
    public void resumePlayback() {
        keepPlaying = true;
        paused = false;
        audioTrack.play();
    }

    /**
     * Stop audio playback.
     */
    public void stopPlayback() {
        Log.d(TAG, "Stop playback.");
        keepPlaying = false;
    }

    /**
     * Returns true if the AudioTrack play state is PlAYSTATE_PLAYING.
     */
    public boolean isPlaying() {
        if (audioTrack == null) {
            Log.d(TAG, String.format("isPlaying ? --> AudioTrack is null"));
        } else {
            Log.d(TAG, String.format("isPlaying ? --> %s",
                    audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING));
        }
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    /**
     * Returns true if the AudioTrack play state is PLAYSTATE_PAUSED.
     */
    public boolean isPaused() {
        if (audioTrack == null) {
            Log.d(TAG, String.format("isPaused ? --> AudioTrack is null"));
        } else {
            Log.d(TAG, String.format("isPaused ? --> %s",
                    audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED));
        }
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
    }

    /**
     * Returns the sample rate.
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Returns the number of audio channels.
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Create an instance of {@code AudioTrack}.
     */
    private void createAudioTrack() {
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate,
                channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

    }

}
