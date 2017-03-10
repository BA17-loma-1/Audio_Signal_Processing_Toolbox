package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ShortBuffer;

import ch.zhaw.bait17.audio_signal_processing_toolbox.DecoderException;
import ch.zhaw.bait17.audio_signal_processing_toolbox.PlaybackListener;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder;


public class AudioTrackPlayer implements Player {

    private static final String TAG = AudioTrackPlayer.class.getSimpleName();

    private Context context;
    private AudioTrack audioTrack;
    private WaveDecoder decoder;
    private String currentTrack;
    private ShortBuffer samples;
    private int sampleRate;
    private int channelOut;
    private PlaybackListener listener;
    private Thread thread;
    private short[] buffer;
    private int playbackStart;
    private int numberOfSamplesPerChannel;
    private boolean keepPlaying = false;


    @Override
    public void init(Context context, PlaybackListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void play(String uri) {
        Log.d(TAG, "Play");
        if (audioTrack != null) {
            audioTrack.release();
        }

        try {
            createAudioTrack(uri);
            currentTrack = uri;
        } catch (IOException e) {
            Log.e(TAG, "Could not play: " + uri, e);
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "Pause");
        if (audioTrack != null) {
            audioTrack.pause();
        }
    }

    @Override
    public void release() {
        Log.d(TAG, "Release");
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
        currentTrack = null;
    }

    @Override
    public void resume() {
        Log.d(TAG, "Resume");
        if (audioTrack != null) {
            // audioTrack.start();
        }
    }

    @Override
    public boolean isPlaying() {
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    @Override
    @Nullable
    public String getCurrentTrack() {
        return currentTrack;
    }

    private void createAudioTrack(String uri) throws IOException {
        Log.d(TAG, "Create new AudioTrack");

        InputStream is = context.getContentResolver().openInputStream(Uri.parse(uri));
        try {
            // TODO: check if wav or mp3
            decoder = new WaveDecoder(is);
        } catch (DecoderException e) {
            Log.e(TAG, "Could not decode: " + uri, e);
        }

        short[] samples = decoder.getShort();
        int channels = decoder.getHeader().getChannels();
        numberOfSamplesPerChannel = samples.length / channels;
        this.samples = ShortBuffer.wrap(samples);
        this.sampleRate = decoder.getHeader().getSampleRate();
        this.channelOut = channels;
        playbackStart = 0;
        int bufferSize = getMinBufferSize();
        buffer = new short[bufferSize];
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                channelOut == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                track.stop();
                track.flush();
                track.release();
                if (listener != null) {
                    listener.onCompletion();
                }
            }
            @Override
            public void onPeriodicNotification(AudioTrack track) {
                if (listener != null && track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    listener.onProgress((int) (track.getPlaybackHeadPosition() * 1000.0 / sampleRate));
                }
            }
        });

        audioTrack.setPositionNotificationPeriod(sampleRate / 1000);                    // E.g. at 48000 Hz --> 48 times per second
        audioTrack.setNotificationMarkerPosition(numberOfSamplesPerChannel - 1);
    }

    /**
     * Returns the minimum buffer size expressed in bytes.
     * @return
     */
    private int getMinBufferSize() {
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate,
                channelOut == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        // Ensure maximum buffer length 500 milliseconds.
        if (bufferSize <= 0 || bufferSize > sampleRate / 2) {
            bufferSize = sampleRate / 4;
        }
        return bufferSize;
    }
}
