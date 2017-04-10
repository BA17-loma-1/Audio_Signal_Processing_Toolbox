package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import java.io.FileNotFoundException;
import java.io.InputStream;
import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PostFilterSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PreFilterSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.PCMUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Util;

/**
 * <p>
 *    A versatile yet easy to use player facade.
 *    It hides the complexity of decoding the audio source, filtering and feeding the PCM samples
 *    to the audio sink and controlling the audio playback.
 * </p>
 *
 * @author georgrem, stockan1
 */
public final class AudioPlayer {

    private static final String TAG = AudioPlayer.class.getSimpleName();
    private static final AudioPlayer INSTANCE = new AudioPlayer();
    private static final int BUFFER_LENGTH_PER_CHANNEL_IN_SECONDS = 3;

    private static short[] decodedSamples;
    private static float[] filteredSamples;
    private static AudioDecoder mp3Decoder;
    private static AudioDecoder waveDecoder;
    private static AudioTrack audioTrack;
    private static Filter filter;
    private static EventBus eventBus;
    private PlaybackListener listener;
    private Track currentTrack;
    private volatile boolean keepPlaying = false;
    private volatile boolean paused = false;
    private int sampleRate;
    private int channels;
    private boolean sampleRateHasChanged = false;
    private boolean channelsHasChanged = false;

    private AudioPlayer() {
        sampleRate = Constants.DEFAULT_SAMPLE_RATE;
        channels = Constants.DEFAULT_CHANNELS;
        buildEventBus();
    }

    /**
     * Returns the singleton instance of the PlayerPresenter.
     *
     * @return
     */
    public static AudioPlayer getInstance() {
        mp3Decoder = MP3Decoder.getInstance();
        waveDecoder = WaveDecoder.getInstance();
        return INSTANCE;
    }

    /**
     * Sets the {@code PlaybackListener}.
     *
     * @param listener
     */
    public void setOnPlaybackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    /**
     * Selects the {@code Track} to be played.
     *
     * @param track
     */
    public void selectTrack(@NonNull Track track) {
        currentTrack = track;
    }

    /**
     * Plays back the currently selected {@code Track}.
     * Works only if a {@code Track} has been selected {@link #selectTrack(Track)}.
     */
    public void play() {
        if (!isPaused() && !isPlaying()) {
            if (currentTrack != null) {
                initialiseDecoder(currentTrack.getUri());
                if (!isAudioTrackInitialised() || sampleRateHasChanged || channelsHasChanged) {
                    // Change AudioTrack buffer size requires API Level 24.
                    // audioTrack.setBufferSizeInFrames(getOptimalBufferSize());
                    audioTrack = null;
                    createAudioTrack();
                }
                startPlayback();
            } else {
                Toast.makeText(ApplicationContext.getAppContext(), "No track selected.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Pauses the audio playback.
     */
    public void pausePlayback() {
        if (isAudioTrackInitialised()) {
            if (isPlaying()) {
                Log.d(TAG, "Pause playback");
                audioTrack.pause();
                paused = true;
                keepPlaying = true;
            }
        }
    }

    /**
     * Resumes the audio playback.
     */
    public void resumePlayback() {
        if (isAudioTrackInitialised()) {
            if (isPaused()) {
                Log.d(TAG, "Resume playback");
                audioTrack.play();
                paused = false;
                keepPlaying = true;
            }
        }
    }

    /**
     * Stops the audio playback.
     */
    public void stopPlayback() {
        if (isAudioTrackInitialised()) {
            if (isPlaying() || isPaused()) {
                Log.d(TAG, "Stop playback.");
                keepPlaying = false;
            }
        }
    }

    /**
     * Positions the playback head to the new position.
     *
     * @param msec
     */
    public void seekToPosition(int msec) {

    }

    /**
     * Returns true if the AudioTrack play state is PlAYSTATE_PLAYING.
     *
     * @return
     */
    public boolean isPlaying() {
        if (!isAudioTrackInitialised()) {
            Log.d(TAG, String.format("isPlaying ? --> AudioTrack is null"));
        } else {
            Log.d(TAG, String.format("isPlaying ? --> %s",
                    audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING));
        }
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    /**
     * Returns true if the AudioTrack play state is PlAYSTATE_PLAYING.
     *
     * @return
     */
    public boolean isStopped() {
        if (!isAudioTrackInitialised()) {
            Log.d(TAG, String.format("isStopped ? --> AudioTrack is null"));
        } else {
            Log.d(TAG, String.format("isStopped ? --> %s",
                    audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED));
        }
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED;
    }

    /**
     * Returns true if the AudioTrack play state is PLAYSTATE_PAUSED.
     *
     * @return
     */
    public boolean isPaused() {
        if (!isAudioTrackInitialised()) {
            Log.d(TAG, String.format("isPaused ? --> AudioTrack is null"));
        } else {
            Log.d(TAG, String.format("isPaused ? --> %s",
                    audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED));
        }
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
    }

    /**
     * Returns the sample rate of the currently playing track.
     *
     * @return
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Returns the number of channels of the currently playing track.
     *
     * @return
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Returns the current head position of the playback.
     *
     * @return
     */
    public int getCurrentPosition() {
        return 0;
    }

    /**
     * Sets the filter.
     *
     * @param filter
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * Initialises the decoder.
     *
     * @param uri
     */
    private void initialiseDecoder(@NonNull final String uri) {
        try {
            InputStream is = Util.getInputStreamFromURI(uri);
            if (uri.endsWith(".mp3")) {
                mp3Decoder.setSource(is);
                int newSampleRate = mp3Decoder.getSampleRate();
                if (newSampleRate != sampleRate) {
                    sampleRateHasChanged = true;
                    sampleRate = newSampleRate;
                } else {
                    sampleRateHasChanged = false;
                }
                int newChannels = mp3Decoder.getChannels();
                if (newChannels != channels) {
                    channelsHasChanged = true;
                    channels = newChannels;
                } else {
                    channelsHasChanged = false;
                }
            } else if (uri.endsWith(".wav")) {
                waveDecoder.setSource(is);
            } else {
                throw new DecoderException("Unsupported audio format.");
            }
        } catch (FileNotFoundException | DecoderException e) {
            Toast.makeText(ApplicationContext.getAppContext(), "", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Starts the audio playback.
     */
    private void startPlayback() {
        // Sometimes AudioTrack initialisation fails - we need to check if AudioTrack is ready.
        if (isAudioTrackInitialised()) {
            keepPlaying = true;
            paused = false;
            audioTrack.play();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Playback thread '" + Thread.currentThread().getName() + "' start");
                    Log.d(TAG, "Playback start");
                    long currentFrameTime = 0;
                    while (keepPlaying) {
                        if (paused) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "Interrupted while paused.");
                            }
                        } else {
                            decodedSamples = mp3Decoder.getNextSampleBlock();
                            if (decodedSamples != null) {
                                currentFrameTime += decodedSamples.length;
                                filteredSamples = filter(PCMUtil.short2FloatArray(decodedSamples));
                                if (audioTrack.write(PCMUtil.float2ShortArray(filteredSamples),
                                        0, filteredSamples.length) < filteredSamples.length) {
                                    Log.d(TAG, "Dropped samples.");
                                }
                                // Broadcast pre filter sample block using event bus
                                eventBus.post(new PreFilterSampleBlock(decodedSamples, sampleRate));
                                // Broadcast post filter sample block using event bus
                                eventBus.post(new PostFilterSampleBlock(
                                        PCMUtil.float2ShortArray(filteredSamples), sampleRate));
                            } else {
                                // No more frames to decode, we reached the end of the InputStream.
                                // --> quit
                                keepPlaying = false;
                            }
                        }
                    }
                    Log.d(TAG, "Finished decoding");
                    // Wait until all frames are written
                    while (currentFrameTime < audioTrack.getPlaybackHeadPosition()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Interrupted while waiting that playback finishes.");
                        }
                    }
                    audioTrack.pause();
                    audioTrack.stop();
                    audioTrack.flush();
                    audioTrack.release();
                    Log.d(TAG, "AudioTrack pause/stop/flush/release.");
                    Log.d(TAG, "Playback stop");
                    Log.d(TAG, "Playback thread '" + Thread.currentThread().getName() + "' stop");
                    listener.onCompletion();
                }
            }).start();
        }
    }

    /**
     * Apply the filter to the supplied audio samples block.
     *
     * @param input
     * @return
     */
    private float[] filter(float[] input) {
        if (filter == null) {
            return input;
        }
        return filter.apply(input);
    }

    /**
     * Creates an instance of {@code AudioTrack}.
     */
    private void createAudioTrack() {
        int optimalBufferSize = getOptimalBufferSize();
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

    /**
     * Computes and returns the optimal buffers size for the {@code AudioTrack} object.
     *
     * @return
     */
    private int getOptimalBufferSize() {
        return sampleRate * channels * BUFFER_LENGTH_PER_CHANNEL_IN_SECONDS;
    }

    /**
     * Returns true if the AudioTrack object is initialised.
     *
     * @return
     */
    private boolean isAudioTrackInitialised() {
        return audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED;
    }

    /**
     * Initialises the EventBus.
     */
    private void buildEventBus() {
        eventBus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .installDefaultEventBus();
    }

}
