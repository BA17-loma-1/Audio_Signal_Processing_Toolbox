package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * An interface representing a header of an audio file.
 *
 * @author georgrem, stockan
 */

public interface HeaderInfo {

    int getEncodingFormat();

    int getChannels();

    int getSampleRate();

    int getBitsPerSample();

    int getDataSize();

}
