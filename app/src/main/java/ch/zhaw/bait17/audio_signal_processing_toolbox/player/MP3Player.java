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
 *
 * Created by georgrem, stockan1 on 11.03.2017.
 */

public class MP3Player implements AudioPlayer {

    private final String TAG = "MP3 player";
    private final int NUMBER_OF_SAMPLES_TO_LOG = 20;
    private final int DEFAULT_SAMPLE_RATE = 44100;
    private final int DEFAULT_CHANNEL_COUNT = 2;

    private Context context;
    private byte[] input;
    private int sampleRate = DEFAULT_SAMPLE_RATE;
    private int channelCount = DEFAULT_CHANNEL_COUNT;
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

    @Override
    public void init(Context context, PlaybackListener listener) {
        this.listener = listener;
        bitstream = new Bitstream(getInputStreamFromByteArray(input));
        decoder = new Decoder();
        extractFrameHeaderInfo(bitstream);
        initialisePlayer();
    }

    @Nullable
    @Override
    public String getCurrentTrack() {
        return null;
    }

    @Override
    public void release() {

    }

    private void initialisePlayer() {
        playbackStart = 0;
        shortSamplesRead = 0;
        shortSamplesWritten = 0;
        int bufferSize = getMinBufferSize();
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                channelCount == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
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
     * Returns true if the AudioTrack play state is PlAYSTATE_PLAYING.
     * @return
     */
    public boolean isPlaying() {
        return audioTrack == null ? false: audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    /**
     * Returns true if the AudioTrack play state is PLAYSTATE_PAUSED.
     * @return
     */
    public boolean isPaused() {
        return audioTrack == null ? false: audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
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

    public void pause() {
        if (isPlaying()) {
            audioTrack.pause();
            seekToPosition(getCurrentPosition());
        }
    }

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

    /**
     * Returns the minimum buffer size expressed in bytes.
     * @return
     */
    private int getMinBufferSize() {
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate,
                channelCount == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        // Ensure maximum buffer length 500 milliseconds.
        if (bufferSize <= 0 || bufferSize > sampleRate / 2) {
            bufferSize = sampleRate / 4;
        }
        return bufferSize;
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
    public int getSampleRate() {
        if (audioTrack != null) {
            return audioTrack.getSampleRate();
        } else {
            return sampleRate;
        }
    }

    @Override
    public int getChannels() {
        return channelCount;
    }

    @Override
    public int getCurrentPosition() {
        return playbackStart;
    }

    private void extractFrameHeaderInfo(Bitstream bitstream) {
        try {
            Header frameHeader = bitstream.readFrame();
            SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
            bitstream.unreadFrame();
            sampleRate = samples.getSampleFrequency();
            channelCount = samples.getChannelCount();
        } catch(BitstreamException | DecoderException ex) {
            Log.e(TAG, "Failed to extract frame header data.", ex);
        }
    }

    private InputStream getInputStreamFromByteArray(byte[] data) {
        return new ByteArrayInputStream(data);
    }

}
