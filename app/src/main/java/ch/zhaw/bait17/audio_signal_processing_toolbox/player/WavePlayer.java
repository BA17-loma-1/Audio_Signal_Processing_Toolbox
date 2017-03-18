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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ShortBuffer;
import ch.zhaw.bait17.audio_signal_processing_toolbox.DecoderException;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Util;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder;

/**
 * Singleton with static factory.
 * @author georgrem, stockan1
 */

public class WavePlayer implements AudioPlayer {

    private static final WavePlayer INSTANCE = new WavePlayer();
    private static final String TAG = WavePlayer.class.getSimpleName();

    private Context context;
    private AudioTrack audioTrack;
    private String currentTrack;
    private ShortBuffer samples;
    private int sampleRate;
    private int channels;
    private PlaybackListener listener;
    private Thread thread;
    private short[] buffer;
    private int playbackStart;
    private int numberOfSamplesPerChannel;
    private boolean keepPlaying = false;

    private WavePlayer() {

    }

    /**
     * Returns the singleton instance of the wave audio player.
     * @return WavePlayer instance
     */
    public static WavePlayer getInstance() {
        return INSTANCE;
    }

    @Override
    public void init(Context context, PlaybackListener listener) {
        Log.d(TAG, "Init wave player");
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void play(@NonNull String uri) {
        // Already playing? Return!
        if (isPlaying()) {
            return;
        }

        Log.d(TAG, "Play");
        try {
            if (audioTrack == null || !currentTrack.equals(uri)) {
                createAudioTrack(uri);
            } else {
                if (audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
                    createAudioTrack(uri);
                }
            }
            if (currentTrack != null) {
                if (!currentTrack.equals(uri)) {
                    stop();
                    createAudioTrack(uri);
                }
            }
        } catch(Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Could not play audio track.", ex);
            return;
        }
        currentTrack = uri;
        keepPlaying = true;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int position = playbackStart * channels;
                samples.position(position);
                final int limit = numberOfSamplesPerChannel * channels;
                while (samples.position() < limit && keepPlaying) {
                    int samplesLeft = limit - samples.position();
                    if (samplesLeft >= buffer.length) {
                        samples.get(buffer);
                    } else {
                        for (int i = samplesLeft; i < buffer.length; i++) {
                            buffer[i] = 0;
                        }
                        samples.get(buffer, 0, samplesLeft);
                    }

                    audioTrack.write(buffer, 0, buffer.length);
                    listener.onAudioDataReceived(buffer);
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
            Log.d(TAG, "Paused");
            seekToPosition(getCurrentPosition());
        }
    }

    @Override
    public void stop() {
        if (isPlaying() || isPaused()) {
            keepPlaying = false;
            audioTrack.pause();     // Immediate stop
            audioTrack.stop();      // Unblock write to avoid deadlocks
            Log.d(TAG, "Stopped");
            if (thread != null) {
                try {
                    thread.join();
                    Log.d(TAG, "Thread joined");
                } catch (InterruptedException ex) {

                }
                thread = null;
                Log.d(TAG, "Thread killed.");
            }
            audioTrack.flush();
        }
    }

    @Override
    public void release() {
        stop();
        if (audioTrack != null) {
            audioTrack.release();
            Log.d(TAG, "Released");
            audioTrack = null;
        }
        currentTrack = null;
    }

    @Override
    public boolean isPlaying() {
        if (audioTrack == null) {
            Log.d(TAG, String.format("isPlaying ? --> AudioTrack is null"));
        } else {
            Log.d(TAG, String.format("isPlaying ? --> %s", audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING));
        }
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        if (audioTrack == null) {
            Log.d(TAG, String.format("isPaused ? --> AudioTrack is null"));
        } else {
            Log.d(TAG, String.format("isPaused ? --> %s", audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED));
        }
        return audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
    }

    @Override
    @Nullable
    public String getCurrentTrack() {
        Log.d(TAG, String.format("Current track: %s", currentTrack));
        return currentTrack;
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
        Log.d(TAG, "Seek to position");
        boolean wasPlaying = isPlaying();
        playbackStart = (int) (msec * sampleRate / 1000);
        if (playbackStart > numberOfSamplesPerChannel) {
            // No more samples to play
            playbackStart = numberOfSamplesPerChannel;
        }
        audioTrack.setNotificationMarkerPosition(numberOfSamplesPerChannel - 1 - playbackStart);
        if (wasPlaying) {
            Log.d(TAG, String.format("Was playing... now seeking to position %d", playbackStart));
            play(currentTrack);
        }
    }

    @Override
    public int getCurrentPosition() {
        return (int) ((playbackStart + audioTrack.getPlaybackHeadPosition()) * (1000.0 / sampleRate));
    }

    private void createAudioTrack(@NonNull String uri) throws FileNotFoundException, DecoderException {
        Log.d(TAG, "Create new AudioTrack");

        if (audioTrack != null) {
            release();
        }

        WaveDecoder decoder;
        try {
            InputStream is = Util.getInputStreamFromURI(context, uri);
            decoder = new WaveDecoder(is);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found: " + uri);
        } catch (DecoderException e) {
            throw new DecoderException("Could not decode track: " + uri);
        }

        short[] samples = decoder.getShort();
        if (samples != null) {
            int channels = decoder.getHeader().getChannels();
            numberOfSamplesPerChannel = samples.length / channels;
            this.samples = ShortBuffer.wrap(samples);
            this.sampleRate = decoder.getHeader().getSampleRate();
            this.channels = channels;
            playbackStart = 0;
            int bufferSize = getMinBufferSize();
            buffer = new short[bufferSize];
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
            audioTrack.setNotificationMarkerPosition(numberOfSamplesPerChannel - 1);
        }
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

}
