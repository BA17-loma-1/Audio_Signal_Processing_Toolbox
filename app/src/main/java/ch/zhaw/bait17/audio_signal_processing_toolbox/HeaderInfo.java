package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * Created by georgrem, stockan1 on 20.02.2017.
 */

public interface HeaderInfo {

    public int getEncodingFormat();

    public int getChannels();

    public int getSampleRate();

    public int getBitsPerSample();

    public int getDataSize();

}
