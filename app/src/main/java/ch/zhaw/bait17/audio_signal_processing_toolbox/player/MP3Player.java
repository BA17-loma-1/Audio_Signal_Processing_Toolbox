/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import javazoom.jl.decoder.*;
import javazoom.jl.decoder.DecoderException;

/**
 * Singleton with static factory.
 * @author georgrem, stockan1
 */

public class MP3Player implements AudioPlayer {

    private static final MP3Player INSTANCE = new MP3Player();
    private static final String TAG = MP3Player.class.getSimpleName();
    private final int NUMBER_OF_SAMPLES_TO_LOG = 10;
    private final int DEFAULT_SAMPLE_RATE = 44100;
    private final int DEFAULT_CHANNEL_COUNT = 2;

    private Context context;
    private byte[] input;
    private int sampleRate = DEFAULT_SAMPLE_RATE;
    private int channels = DEFAULT_CHANNEL_COUNT;
    private PlaybackListener listener;
    private Bitstream bitstream;
    private Decoder decoder;
    private Thread thread;
    private AudioTrack audioTrack;
    private String currentTrack;
    private int playbackStart;
    private int shortSamplesRead;
    private int shortSamplesWritten;
    private int numberOfSamplesPerChannel;
    private boolean keepPlaying = false;

    private MP3Player() {

    }

    /**
     * Returns the singleton instance of the mp3 audio player.
     * @return MP3Player instance
     */
    public static MP3Player getInstance() {
        return INSTANCE;
    }

    @Override
    public void init(Context context, PlaybackListener listener) {
        this.context = context;
        this.listener = listener;
        initialisePlayer();
    }

    @Override
    public void play(String uri) {
        // Already playing? Return!
        if (isPlaying()) {
            return;
        }

        currentTrack = uri;
        keepPlaying = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean done = false;
                try {
                    int frameCount = 0;
                    Header frameHeader = null;
                    while ((frameHeader = bitstream.readFrame()) != null) {
                        frameCount++;
                        playbackStart += frameHeader.ms_per_frame();

                        SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                        short[] pcm = samples.getBuffer();
                        shortSamplesRead += pcm.length;
                        shortSamplesWritten += audioTrack.write(pcm, 0, pcm.length);
                        listener.onAudioDataReceived(pcm);
                        bitstream.closeFrame();

                        short[] bufferToLog = new short[NUMBER_OF_SAMPLES_TO_LOG];
                        System.arraycopy(pcm, 0, bufferToLog, 0, NUMBER_OF_SAMPLES_TO_LOG);
                        Log.i(TAG, String.format("Frame %d len: %d, First %d samples: %s",
                                frameCount, samples.getBufferLength(), NUMBER_OF_SAMPLES_TO_LOG,
                                Arrays.toString(bufferToLog)));
                    }
                    bitstream.close();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage(), ex);
                }
            }
        });
        thread.start();

        audioTrack.flush();
        audioTrack.play();
    }

    @Override
    public void pause() {
        if (isPlaying()) {
            audioTrack.pause();
            seekToPosition(getCurrentPosition());
        }
    }

    @Override
    public void stop() {
        if (isPlaying() || isPaused()) {
            audioTrack.pause();     // Immediate stop
            audioTrack.stop();      // Unblock write to avoid deadlocks
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {

                }
                thread = null;
            }
            audioTrack.flush();
        }
    }

    @Override
    public boolean isPlaying() {
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    public boolean isPaused() {
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
    }

    @Nullable
    @Override
    public String getCurrentTrack() {
        return currentTrack;
    }

    @Override
    public void release() {
        stop();
        if (audioTrack != null) {
            audioTrack.release();;
            audioTrack = null;
        }
        currentTrack = null;
    }

    @Override
    public int getSampleRate() {
        if (audioTrack != null) {
            return audioTrack.getSampleRate();
        } else {
            return sampleRate;
        }
    }

    @Override
    public int getChannels() {
        return channels;
    }

    @Override
    public void seekToPosition(int msec) {
        boolean wasPlaying = isPlaying();
        stop();
        playbackStart = (int) (msec * sampleRate / 1000);
        if (playbackStart > numberOfSamplesPerChannel) {
            // No more samples to play
            playbackStart = numberOfSamplesPerChannel;
        }
        audioTrack.setNotificationMarkerPosition(numberOfSamplesPerChannel - 1 - playbackStart);
        if (wasPlaying) {
            play(currentTrack);
        }
    }

    @Override
    public int getCurrentPosition() {
        return playbackStart;
    }

    private void initialisePlayer() {
        bitstream = new Bitstream(getInputStreamFromByteArray(input));
        decoder = new Decoder();
        extractFrameHeaderInfo(bitstream);
        playbackStart = 0;
        shortSamplesRead = 0;
        shortSamplesWritten = 0;
        int bufferSize = getMinBufferSize();
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
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
        //audioTrack.setNotificationMarkerPosition(numberOfSamplesPerChannel - 1);            // when playback reaches end of samples --> notify
    }

    /**
     * Returns the minimum buffer size expressed in bytes.
     * @return
     */
    private int getMinBufferSize() {
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate,
                channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        // Ensure maximum buffer length 500 milliseconds.
        if (bufferSize <= 0 || bufferSize > sampleRate / 2) {
            bufferSize = sampleRate / 4;
        }
        return bufferSize;
    }

    private void extractFrameHeaderInfo(Bitstream bitstream) {
        try {
            Header frameHeader = bitstream.readFrame();
            SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
            bitstream.unreadFrame();
            sampleRate = samples.getSampleFrequency();
            channels = samples.getChannelCount();
        } catch(BitstreamException | DecoderException ex) {
            Log.e(TAG, "Failed to extract frame header data.", ex);
        }
    }

    private InputStream getInputStreamFromByteArray(byte[] data) {
        return new ByteArrayInputStream(data);
    }

}
