package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PCMSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PreFilterSampleBlock;

/**
 * An interface representing an audio decoder.
 * @author georgrem, stockan1
 */

public interface AudioDecoder {

    /**
     * Returns the next PCM sample block or null if end of stream is reached.
     * @return
     */
    PCMSampleBlock getNextSampleBlock();

    /**
     * Returns the sample rate.
     * @return
     */
    int getSampleRate();

    /**
     * Returns the number of channels.
     * @return
     */
    int getChannels();

}
