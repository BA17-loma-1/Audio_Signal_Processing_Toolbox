package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PCMSampleBlock;

import static ch.zhaw.bait17.audio_signal_processing_toolbox.player.PlayerPresenter.QUEUE_SIZE;

/**
 * Audio player based on {@code AudioTrack}.
 *
 * @author georgrem, stockan1
 */

public class AudioPlayer {

    private static final String TAG = AudioPlayer.class.getSimpleName();
    private static final int BUFFER_LENGTH_PER_CHANNEL_IN_SECONDS = 3;

    private AudioTrack audioTrack;
    private volatile boolean keepPlaying = false;
    private boolean paused = false;
    private ArrayBlockingQueue<PCMSampleBlock> inputQueue;
    private int sampleRate;
    private int channels;

    /**
     * <p>
     * Create an audio player and initialise the {@code AudioTrack} for playback.
     * </p>
     */
    public AudioPlayer() {
        sampleRate = Constants.DEFAULT_SAMPLE_RATE;
        channels = Constants.DEFAULT_CHANNELS;
        inputQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    }

    /**
     * Return the size of the internal sample block buffer.
     *
     * @return
     */
    public static int getQueueSize() {
        return QUEUE_SIZE;
    }

    /**
     * Add a new {@code PCMSampleBlock} to the input queue for playback.
     *
     * @param sampleBlock A {@code PCMSampleBlock}
     * @return true if the sample block was added to the tail of the input queue
     */
    public boolean enqueueSampleBlock(@NonNull PCMSampleBlock sampleBlock) {
        return inputQueue.offer(sampleBlock);
    }

    /**
     * Clear the internal sample buffer.
     */
    public void clearSampleBuffer() {
        inputQueue.clear();
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
    public void play() {
        if (isPlaying()) {
            return;
        }
        createAudioTrack();
        audioTrack.play();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "AudioTrack Playback Thread (APT) started");
                keepPlaying = true;
                PCMSampleBlock sampleBlock = null;
                while (keepPlaying) {
                    if (paused) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Interrupted while paused.");
                        }
                    } else {
                        if (!inputQueue.isEmpty()) sampleBlock = inputQueue.poll();
                        if (sampleBlock != null) {
                            short[] pcm = sampleBlock.getSamples();
                            audioTrack.write(pcm, 0, pcm.length);
                            Log.d(TAG, "Input queue size: " + inputQueue.size());
                        }
                    }
                }
                Log.d(TAG, "AudioTrack Playback Thread (APT) stopped");
            }
        }).start();
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
        audioTrack.pause();
        audioTrack.stop();
        audioTrack.flush();
        audioTrack.release();
        Log.d(TAG, "AudioTrack pause/stop/flush/release.");
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
        int optimalBufferSize = sampleRate * channels * BUFFER_LENGTH_PER_CHANNEL_IN_SECONDS;
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate,
                channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        if (bufferSize < optimalBufferSize) {
            bufferSize = optimalBufferSize;
        }
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

    }

}
