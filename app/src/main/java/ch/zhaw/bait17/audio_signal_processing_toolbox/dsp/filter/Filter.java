package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

/**
 * Interface representing a filter for online processing of PCM samples provided
 * as an array of {@code short}s.
 * @author georgrem, stockan1
 */

public interface Filter {

    /**
     * Apply the filter to {@code samples}.
     * @param samples
     */
    short[] apply(short[] samples);

}
