package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * <p>
 *     An interface representing a digital filter.
 * </p>
 * @author georgrem, stockan1
 */

public interface Filter extends AudioEffect {

    /**
     * Returns the order of the filter.
     *
     * @return filter order
     */
    int getOrder();

    /**
     * Returns the filter specification.
     *
     * @return
     */
    FilterSpec getFilterSpec();

}
