package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.Arrays;

/**
 * Created by georgrem, stockan1 on 23.02.2017.
 */

public class AudioStream {

    private WaveHeaderInfo header;
    private float[] samples;
    private PlaybackListener listener;
    private Thread thread;
    private int channelOut;
    private int encoding;
    private boolean continuePlayback = false;
    private AudioTrack audioTrack;

    public AudioStream(WaveHeaderInfo header, float[] samples, PlaybackListener listener)
            throws DecoderException {
        if (header == null) {
            throw new DecoderException("Invalid header.");
        }
        this.header = header;
        this.samples = Arrays.copyOf(samples, samples.length);
        /*
        if (header.getBitsPerSample() == 8) {
            this.samples = PCMUtil.float8Bit2ByteArray(samples);
        } else{
            this.samples = PCMUtil.float16Bit2ByteArray(samples);
        }
        */
        this.listener = listener;

        init();
    }

    private void init() throws DecoderException {
        if (header.getChannels() == 1) {
            channelOut = AudioFormat.CHANNEL_OUT_MONO;
        } else {
            channelOut = AudioFormat.CHANNEL_OUT_STEREO;
        }

        encoding = AudioFormat.ENCODING_PCM_FLOAT;
        /*
        if (header.isLinearPCM() && header.getBitsPerSample() == 8) {
            encoding = AudioFormat.ENCODING_PCM_8BIT;
        } else if (header.isLinearPCM() && header.getBitsPerSample() == 16) {
            encoding = AudioFormat.ENCODING_PCM_16BIT;
        } else {
            throw new DecoderException("Unsupported file format.");
        }
        */
    }

    /**
     *
     * @return
     */
    public boolean isPlaying() {
        return thread != null;
    }

    /**
     * Starts the audio playback thread.
     * Calls of this method while a playback thread is online will be ignored.
     */
    public void start() {
        // already playing...?
        if (thread == null) {
            continuePlayback = true;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    play();
                }
            });
            thread.start();
        }
    }

    /**
     * Stops the audio playback thread.
     */
    public void stop() {
        if (thread != null) {
            continuePlayback = false;
            thread = null;
        }
    }

    private void play() {
        int bufferSize = getMinBufferSize();
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, header.getSampleRate(),
                channelOut, encoding, bufferSize, AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                track.release();
                if (listener != null) {
                    listener.onCompletion();
                }
            }
            @Override
            public void onPeriodicNotification(AudioTrack track) {
                if (listener != null && track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    listener.onProgress(track.getPlaybackHeadPosition() * 1000 / header.getSampleRate());
                }
            }
        });

        audioTrack.setPositionNotificationPeriod(header.getSampleRate() / 1000);        // at 48000 Hz --> 48 times per second
        audioTrack.setNotificationMarkerPosition(samples.length);                       // when playback reaches end of samples --> notify
        audioTrack.play();

        int totalSamplesWritten = 0;
        while (totalSamplesWritten < samples.length) {
            float[] buffer;
            // Fill the AudioTrack buffer
            if (samples.length - totalSamplesWritten > bufferSize) {
                buffer = new float[bufferSize];
            } else {
                buffer = new float[samples.length-totalSamplesWritten];
            }
            System.arraycopy(samples, totalSamplesWritten, buffer, 0, buffer.length);
            int samplesWritten = audioTrack.write(buffer, 0, buffer.length, AudioTrack.WRITE_NON_BLOCKING);
            if (samplesWritten <= 0) {
                // Some error happened with AudioTrack.
                stop();
            }
            totalSamplesWritten += samplesWritten;
        }

        // Release the audio device
        if (!continuePlayback) {
            audioTrack.release();
        }
    }

    /**
     * Returns the minimum buffer size in bytes
     * @return
     */
    private int getMinBufferSize() {
        int bufferSize = AudioTrack.getMinBufferSize(header.getSampleRate(), channelOut, encoding);
        if (bufferSize <= 0) {
            // Some error happened with AudioTrack.
            bufferSize = header.getSampleRate() * 2;
        } else if (bufferSize > samples.length) {
            bufferSize = samples.length;
        }
        return bufferSize;
    }

    public int getSampleRate() {
        if (audioTrack == null) return header.getSampleRate();
        else return audioTrack.getSampleRate();
    }

}
