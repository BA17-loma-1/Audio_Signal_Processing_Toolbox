package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * Header of WAV audio files.
 * Created by georgrem, stockan1 on 21.02.2017.
 */

public class WaveHeaderInfo implements HeaderInfo {

    private int encodingFormat = 0;
    private int channels = 0;
    private int sampleRate = 0;
    private int bitsPerSample = 0;
    private int dataSize = 0;

    public WaveHeaderInfo(int encodingFormat, int channels, int sampleRate, int bitsPerSample,
                          int dataSize) {
        this.encodingFormat = encodingFormat;
        this.channels = channels;
        this.sampleRate = sampleRate;
        this.bitsPerSample = bitsPerSample;
        this.dataSize = dataSize;
    }

    /**
     *
     * @return
     */
    @Override
    public int getEncodingFormat() {
        return encodingFormat;
    }

    /**
     * Returns true if audio data is encoded with linear pulse-code modulation.
     * @return
     */
    public boolean isLinearPCM() {
        return encodingFormat == AudioCodingFormat.LINEAR_PCM.getValue();
    }

    /**
     * Returns the number of channels.
     * @return
     */
    @Override
    public int getChannels() {
        return  channels;
    }

    /**
     * Returns the sample rate.
     * @return
     */
    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Returns the number of bits per sample.
     * @return
     */
    @Override
    public int getBitsPerSample() {
        return bitsPerSample;
    }

    /**
     * Returns the file size.
     * @return
     */
    @Override
    public int getDataSize() {
        return dataSize;
    }

}
