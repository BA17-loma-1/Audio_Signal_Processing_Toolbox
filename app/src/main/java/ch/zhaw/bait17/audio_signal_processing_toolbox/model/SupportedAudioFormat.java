package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

/**
 * Supported audio formats by the application.
 * The field associated with each enum value is the MIME type of the supported audio format.
 *
 * @author georgrem, stockan1
 */

public enum SupportedAudioFormat {

    WAVE("audio/x-wav"), MP3("audio/mpeg"), UNKNOWN("unknown");

    private String audioFormat;
    private String fileExtension;

    SupportedAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
        setFileExtension(audioFormat);
    }

    @Override
    public String toString() {
        return getAudioFormat();
    }

    public String getAudioFormat() {
        return audioFormat;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public static SupportedAudioFormat getSupportedAudioFormat(String mimeType) {
        SupportedAudioFormat audioFormat = SupportedAudioFormat.UNKNOWN;
        for (SupportedAudioFormat item : SupportedAudioFormat.values()) {
            if (item.audioFormat.equalsIgnoreCase(mimeType)) {
                audioFormat = item;
            }
        }
        return audioFormat;
    }

    private void setFileExtension(String mimeType) {
        switch (mimeType) {
            case "audio/x-wav":
                fileExtension = "wav";
                break;
            case "audio/mpeg":
                fileExtension = "mp3";
                break;
            default:
                fileExtension = "";
        }
    }

}
