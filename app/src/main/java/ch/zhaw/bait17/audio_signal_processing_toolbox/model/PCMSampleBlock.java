package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

import android.support.annotation.NonNull;

import java.util.Arrays;

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

    private short[] preFilterSamples;
    private short[] postFilterSamples;
    private final int SAMPLE_RATE;
    private final int CHANNELS;

    /**
     * Creates a new {@code PCMSampleBlock}.
     *
     * @param preFilterSamples      unfiltered samples
     * @param postFilterSamples     filtered samples
     * @param sampleRate            sample rate
     * @param channels              number of channels
     */
    public PCMSampleBlock(@NonNull short[] preFilterSamples, @NonNull short[] postFilterSamples,
                          final int sampleRate, final int channels) {
        this.preFilterSamples = Arrays.copyOf(preFilterSamples, preFilterSamples.length);
        this.postFilterSamples = Arrays.copyOf(postFilterSamples, postFilterSamples.length);
        this.SAMPLE_RATE = sampleRate;
        this.CHANNELS = channels;
    }

    /**
     * Returns the unfiltered samples.
     *
     * @return      an array of {@code short}
     */
    public short[] getPreFilterSamples() {
        return Arrays.copyOf(preFilterSamples, preFilterSamples.length);
    }

    /**
     * Retuns the filtered samples.
     *
     * @return      an array of {@code short}
     */
    public short[] getPostFilterSamples() {
        return Arrays.copyOf(postFilterSamples, postFilterSamples.length); }

    /**
     * Returns the sample rate.
     *
     * @return      sample rate in Hertz
     */
    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    /**
     * Returns the channels.
     *
     * @return      the number of channels
     */
    public int getChannels() {
        return CHANNELS;
    }

}
