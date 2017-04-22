package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

/**
 * <p>
 *     This class is used to pass PCM samples between fragments. </br>
 *     It is basically a buffer that holds a block of PCM audio samples. </br>
 *     EventBus is used to simplify communication by implementing the publish/subscriber pattern.
 *     Source: http://greenrobot.org/eventbus
 * </p>
 *
 * @author georgrem, stockan1
 */

public final class PCMSampleBlock {

    private short[] samples;
    private final int SAMPLE_RATE;
    private final int CHANNELS;
    private final boolean PRE_FILTER_SAMPLES;

    /**
     * Creates a new {@code PCMSampleBlock}.
     *
     * @param samples
     * @param sampleRate
     * @param channels
     * @param preFilterSamples
     */
    public PCMSampleBlock(short[] samples, final int sampleRate, final int channels,
                          final boolean preFilterSamples) {
        this.samples = samples;
        this.SAMPLE_RATE = sampleRate;
        this.CHANNELS = channels;
        this.PRE_FILTER_SAMPLES = preFilterSamples;
    }

    /**
     * Returns the samples.
     *
     * @return  an array of {@code short}
     */
    public short[] getSamples() {
        return samples;
    }

    /**
     * Returns the sample rate.
     *
     * @return  sample rate in Hertz
     */
    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    /**
     * Returns the channels.
     *
     * @return  the number of channels
     */
    public int getChannels() {
        return CHANNELS;
    }

    /**
     * Returns true if the samples are pre-filter.
     *
     * @return
     */
    public boolean isPreFilterSamples() {
        return PRE_FILTER_SAMPLES;
    }

    /**
     * Returns the size of the sample block.
     *
     * @return  size of the sample block
     */
    public int getSize() {
        return samples.length;
    }

}
