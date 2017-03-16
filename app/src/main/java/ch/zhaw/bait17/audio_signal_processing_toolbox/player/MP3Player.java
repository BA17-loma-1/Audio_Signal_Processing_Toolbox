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

import java.io.IOException;
import java.io.InputStream;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Util;
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
    private int sampleRate = DEFAULT_SAMPLE_RATE;
    private int channels = DEFAULT_CHANNEL_COUNT;
    private PlaybackListener listener;
    private InputStream is;
    private Bitstream bitstream;
    private Decoder decoder;
    private Thread thread;
    private AudioTrack audioTrack;
    private String currentTrack;
    private int shortSamplesRead;
    private int shortSamplesWritten;
    private boolean keepPlaying = false;
    private int position;
    private int frameIndex;

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
        Log.d(TAG, "Init mp3 player");
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void play(@NonNull String uri) {
        Log.d(TAG, "Play");
        this.play(uri, position);
    }

    private void play(@NonNull String uri, final int pos) {
        // Already playing? Return!
        if (isPlaying()) {
            return;
        }

        Log.d(TAG, String.format("Play from position %d", pos));

        try {
            if (audioTrack == null || !currentTrack.equals(uri)) {
                createAudioTrack(uri);
            } else if (audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
                createAudioTrack(uri);
            }
            if (currentTrack != null) {
                if (!currentTrack.equals(uri)) {
                    stop();
                    createAudioTrack(uri);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Could not play audio track.", ex);
            return;
        }

        currentTrack = uri;
        keepPlaying = true;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Header currentFrameHeader = null;
                    while ((currentFrameHeader = bitstream.readFrame()) != null && keepPlaying) {
                        frameIndex++;
                        position += currentFrameHeader.ms_per_frame();
                        Log.d(TAG, String.format("Position %d:", position));
                        if (position >= pos) {
                            SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(currentFrameHeader, bitstream);
                            short[] pcm = samples.getBuffer();
                            shortSamplesRead += pcm.length;
                            shortSamplesWritten += audioTrack.write(pcm, 0, pcm.length);
                            listener.onAudioDataReceived(pcm);
                            /*
                            short[] bufferToLog = new short[NUMBER_OF_SAMPLES_TO_LOG];
                            System.arraycopy(pcm, 0, bufferToLog, 0, NUMBER_OF_SAMPLES_TO_LOG);
                            Log.i(TAG, String.format("Frame %d len: %d, First %d samples: %s",
                                    frameIndex, samples.getBufferLength(), NUMBER_OF_SAMPLES_TO_LOG,
                                    Arrays.toString(bufferToLog)));
                                    */
                        }
                        bitstream.closeFrame();
                    }
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
            keepPlaying = false;
            audioTrack.pause();
            Log.d(TAG, "Paused");
            seekToPosition(getCurrentPosition());
        }
    }

    @Override
    public void stop() {
        if (isPlaying() || isPaused()) {
            /*try {
                bitstream.close();
                is.close();
            } catch (BitstreamException e) {
                Toast.makeText(context, "Failed to close Bitstream.\n " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(context, "Failed to close the InputStreamn\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }*/
            keepPlaying = false;
            audioTrack.pause();     // Immediate stop
            audioTrack.stop();      // Unblock write to avoid deadlocks
            Log.d(TAG, "Stopped");
            position = 0;
            frameIndex = 0;
            shortSamplesRead = 0;
            shortSamplesWritten = 0;
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
        position = msec * sampleRate / 1000;
        /*
        if (playbackStart > numberOfSamplesPerChannel) {
            // No more samples to play
            playbackStart = numberOfSamplesPerChannel;
        }
        audioTrack.setNotificationMarkerPosition(numberOfSamplesPerChannel - 1 - playbackStart);
        */
        if (wasPlaying) {
            Log.d(TAG, String.format("Was playing... now seeking to position %d", position));
            stop();
            this.play(currentTrack, position);
        }
    }

    @Override
    public int getCurrentPosition() {
        return position;
    }

    private void createAudioTrack(@NonNull String uri) throws IOException {
        Log.d(TAG, "Create new AudioTrack");

        if (audioTrack != null) {
            release();
        }

        try {
            is = Util.getInputStreamFromURI(context, uri);
            bitstream = new Bitstream(is);
            decoder = new Decoder();
            extractFrameHeaderInfo(bitstream);

            shortSamplesRead = 0;
            shortSamplesWritten = 0;

            position = 0;
            frameIndex = 0;
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
            //audioTrack.setNotificationMarkerPosition(numberOfSamplesPerChannel - 1);        // when playback reaches end of samples --> notify
        } catch (IOException e) {
            throw new IOException("Some error occurred with the InputStream:\n" + e.getMessage());
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
        // Ensure minimum and maximum buffer length.

        if (bufferSize <= 0 || bufferSize > sampleRate / 2) {
            bufferSize = sampleRate / 4;
        }
        return bufferSize;
    }

    private void extractFrameHeaderInfo(Bitstream bitstream) {
        try {
            Header frameHeader = bitstream.readFrame();
            SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
            sampleRate = samples.getSampleFrequency();
            channels = samples.getChannelCount();
            bitstream.closeFrame();
            bitstream.unreadFrame();
        } catch(BitstreamException | DecoderException ex) {
            Toast.makeText(context, "Failed to extract frame header data.\n " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
