package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

/**
 * A buffer that holds a block of PCM audio samples.
 * @author georgrem, stockan1
 */

public class PCMSampleBlock {

    private short[] samples;
    private final int SAMPLE_RATE;

    /**
     * Creates a new {@code PCMSampleBuffer}
     * @param samples
     * @param sampleRate
     */
    public PCMSampleBlock(short[] samples, final int sampleRate) {
        this.samples = samples;
        this.SAMPLE_RATE = sampleRate;
    }

    /**
     *
     * @return
     */
    public short[] getSamples() {
        return samples;
    }

    /**
     *
     * @return
     */
    public int getSAMPLE_RATE() {
        return SAMPLE_RATE;
    }

    /**
     * Returns the size of this {@code PCMSampleBuffer}
     * @return
     */
    public int getSize() {
        return samples.length;
    }

}
