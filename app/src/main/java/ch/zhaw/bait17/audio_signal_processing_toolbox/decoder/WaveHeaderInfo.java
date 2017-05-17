package ch.zhaw.bait17.audio_signal_processing_toolbox.decoder;

import android.annotation.SuppressLint;

/**
 * Header of a WAV audio file.
 *
 * @author georgrem, stockan1
 */

public class WaveHeaderInfo implements HeaderInfo {

    private int encodingFormat = 0;
    private int channels = 0;
    private int sampleRate = 0;
    private int bitsPerSample = 0;
    private int dataSize = 0;

    /**
     *
     * @param encodingFormat
     * @param channels
     * @param sampleRate
     * @param bitsPerSample
     * @param dataSize
     */
    public WaveHeaderInfo(int encodingFormat, int channels, int sampleRate, int bitsPerSample,
                          int dataSize) {
        this.encodingFormat = encodingFormat;
        this.channels = channels;
        this.sampleRate = sampleRate;
        this.bitsPerSample = bitsPerSample;
        this.dataSize = dataSize;
    }

    /**
     * Returns the encoding format.
     *
     * @return  encoding format
     */
    @Override
    public int getEncodingFormat() {
        return encodingFormat;
    }

    /**
     * Returns true if audio data is encoded with linear pulse-code modulation.
     *
     * @return  true if linear PCM encoding
     */
    public boolean isLinearPCM() {
        return encodingFormat == AudioCodingFormat.LINEAR_PCM.getValue();
    }

    /**
     * Returns the number of channels.
     *
     * @return  number of channels
     */
    @Override
    public int getChannels() {
        return  channels;
    }

    /**
     * Returns the sample rate.
     *
     * @return  sample rate
     */
    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Returns the number of bits per sample.
     *
     * @return  quantisation bits per sample
     */
    @Override
    public int getBitsPerSample() {
        return bitsPerSample;
    }

    /**
     * Returns the number of bytes (for all channels) to represent one sample of data.
     * This is sometimes called the block alignment.
     * Definition: Bits per sample * channels / 8
     *
     * @return  bytes per sample
     */
    public int getBytesPerSample() {
        return bitsPerSample * channels / 8;
    }

    /**
     * Returns the number of bytes per second which is the speed of the data stream.
     * Definition: sample rate * bits per sample * channels / 8
     *
     * @return  bytes per second
     */
    public int getBytesPerSecond() {
        return sampleRate * bitsPerSample * channels / 8;
    }

    /**
     * Returns the number of bytes of PCM data that is included in the data section.
     *
     * @return  the actual PCM audio data size in bytes
     */
    @Override
    public int getDataSize() {
        return dataSize;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("WAVE header:  encoding format=%s  channels=%d  sample rate=%d  bits per sample=%d",
                AudioCodingFormat.LINEAR_PCM.toString(), channels, sampleRate, bitsPerSample);
    }

}
