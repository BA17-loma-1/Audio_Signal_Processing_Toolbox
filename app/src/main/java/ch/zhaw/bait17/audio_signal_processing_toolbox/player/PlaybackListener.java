package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

/**
 * @author georgrem, stockan1
 */

public interface PlaybackListener {

    void onProgress(int progress);

    void onCompletion();

    void onAudioDataReceived(short[] data);

}
