package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.content.Context;
import android.support.annotation.Nullable;

/**
 * Created by georgrem, stockan1 on 11.03.2017.
 */

public interface AudioPlayer {


    void init(Context context, PlaybackListener listener);

    void play(String uri);

    /**
     * Pauses the audio playback.
     */
    void pause();

    /**
     * Stops the audio playback.
     */
    void stop();

    boolean isPlaying();

    @Nullable
    String getCurrentTrack();

    void release();

    int getSampleRate();

    int getChannels();

    void seekToPosition(int msec);

    /**
     * Returns the current position as millisecond.
     */
    int getCurrentPosition();
}
