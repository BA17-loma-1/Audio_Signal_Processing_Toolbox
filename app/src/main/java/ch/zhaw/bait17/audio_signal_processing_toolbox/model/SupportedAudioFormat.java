package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

/**
 * @auhor georgrem, stockan1
 */

public enum SupportedAudioFormat {

    WAVE("audio/x-wav"), MP3("audio/mpeg");

    private String audioFormat;

    SupportedAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    @Override
    public String toString() {
        return audioFormat;
    }

}
