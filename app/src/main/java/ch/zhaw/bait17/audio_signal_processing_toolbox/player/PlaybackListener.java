package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

/**
 * Created by georgrem, stockan1 on 23.02.2017.
 */

public interface PlaybackListener {

    void onProgress(int progress);

    void onCompletion();

    void onAudioDataReceived(short[] data);

}
