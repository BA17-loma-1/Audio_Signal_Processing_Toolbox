/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by georgrem, stockan1 on 23.02.2017.
 */

package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.nio.ShortBuffer;

public class AudioPlayerService extends Service {

    private ShortBuffer samples;
    private int sampleRate;
    private int channelOut;
    private PlaybackListener listener;
    private Thread thread;
    private short[] buffer;
    private AudioTrack audioTrack;
    private int playbackStart;
    private int numberOfSamplesPerChannel;
    private boolean keepPlaying = false;


    private final IBinder audioPlayerBinder = new AudioPlayerBinder();
    private String TAG = "bound";



    @Override
    public void onCreate() {
        Log.i(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.i(TAG, "Service binded");
        return audioPlayerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.i(TAG, "Service un-binded");
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with Re-bindService(),
        // after onUnbind() has already been called
        Log.i(TAG, "Service re-binded");
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.i(TAG, "Service destroyed");
        audioTrack.release();
        Thread dummy = thread;
        thread = null;
        dummy.interrupt();
    }

    public class AudioPlayerBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }





    public void setSamples(short[] samples) {
        this.samples = ShortBuffer.wrap(samples);
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setChannelOut(int channelOut) {
        this.channelOut = channelOut;
    }

    public void setListener(PlaybackListener listener) {
        this.listener = listener;
    }

    public PlaybackListener getListener() {
        return listener;
    }

    public int getChannelOut() {
        return channelOut;
    }


    public short[] getBuffer() {
        return buffer;
    }

    public void initialize() {
        numberOfSamplesPerChannel = samples.array().length / channelOut;
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
        audioTrack.setNotificationMarkerPosition(numberOfSamplesPerChannel - 1);            // when playback reaches end of samples --> notify
    }

    /**
     * Returns true if the AudioTrack play state is PlAYSTATE_PLAYING.
     *
     * @return
     */
    public boolean isPlaying() {
        return audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    /**
     * Returns true if the AudioTrack play state is PLAYSTATE_PAUSED.
     *
     * @return
     */
    public boolean isPaused() {
        return audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
    }

    /**
     * Starts the audio playback.
     */
    public void play() {
        // Already playing? Return!
        if (isPlaying()) {
            return;
        }

        if (audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            initialize();
        }

        keepPlaying = true;
        audioTrack.flush();
        audioTrack.play();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int position = playbackStart * channelOut;
                samples.position(position);
                final int limit = numberOfSamplesPerChannel * channelOut;
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
                    if (listener != null) listener.onAudioDataReceived(buffer);
                }
            }
        });
        thread.start();
    }

    /**
     * Pauses the audio playback.
     */
    public void pause() {
        if (isPlaying()) {
            audioTrack.pause();
            seekToPosition(getCurrentPosition());
        }
    }

    /**
     * Stops the audio playback.
     */
    public void stop() {
        if (isPlaying() || isPaused()) {
            keepPlaying = false;
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

    /**
     * Returns the minimum buffer size expressed in bytes.
     *
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

    public int getSampleRate() {
        if (audioTrack != null) {
            return audioTrack.getSampleRate();
        } else {
            return sampleRate;
        }
    }

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
            play();
        }
    }

    /**
     * Returns the current position as millisecond.
     *
     * @return
     */
    public int getCurrentPosition() {
        return (int) ((playbackStart + audioTrack.getPlaybackHeadPosition()) * (1000.0 / sampleRate));
    }

}
