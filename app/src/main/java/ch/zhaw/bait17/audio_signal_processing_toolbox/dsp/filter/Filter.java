package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * <p>
 *     An interface representing a digital filter.
 * </p>
 * @author georgrem, stockan1
 */

public abstract class Filter extends AudioEffect {

    /**
     * Returns the order of the filter.
     *
     * @return filter order
     */
    abstract int getOrder();

    /**
     * Returns the filter specification.
     *
     * @return
     */
    abstract FilterSpec getFilterSpec();

}
