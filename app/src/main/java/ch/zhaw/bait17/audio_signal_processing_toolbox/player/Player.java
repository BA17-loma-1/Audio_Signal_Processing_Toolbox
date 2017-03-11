package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.content.Context;
import android.support.annotation.Nullable;

import ch.zhaw.bait17.audio_signal_processing_toolbox.PlaybackListener;

public interface Player {

    void init(Context context, PlaybackListener listener);

    void play(String uri);

    void pause();

    boolean isPlaying();

    @Nullable
    String getCurrentTrack();

    void release();

    int getSampleRate();

    int getChannelOut();
}
