package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * Created by georgrem, stockan1 on 23.02.2017.
 */

public interface PlaybackListener {

    public void onProgress(int progress);

    public void onCompletion();

    public void onAudioDataReceived(short[] data);

}
