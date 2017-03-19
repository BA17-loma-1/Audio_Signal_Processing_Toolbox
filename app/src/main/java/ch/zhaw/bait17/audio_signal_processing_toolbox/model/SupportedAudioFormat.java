package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

/**
 * @auhor georgrem, stockan1
 */

public enum SupportedAudioFormat {

    WAVE("audio/x-wav"), MP3("audio/mpeg"), unknown("unknown");

    private String audioFormat;

    SupportedAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    @Override
    public String toString() {
        return audioFormat;
    }

    public static SupportedAudioFormat getSupportedAudioFormat(String mimeType) {
        SupportedAudioFormat audioFormat = SupportedAudioFormat.unknown;
        for (SupportedAudioFormat item : SupportedAudioFormat.values()) {
            if (item.audioFormat.equalsIgnoreCase(mimeType)) {
                audioFormat = item;
            }
        }
        return audioFormat;
    }

}
