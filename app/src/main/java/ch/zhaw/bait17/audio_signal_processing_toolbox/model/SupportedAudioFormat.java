package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

/**
 * Created by georgrem, stockan1 on 12.03.2017.
 */

public enum SupportedAudioFormat {

    WAVE("wav"), MP3("mp3");

    private String audioFormat;

    SupportedAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    @Override
    public String toString() {
        return audioFormat;
    }

}
