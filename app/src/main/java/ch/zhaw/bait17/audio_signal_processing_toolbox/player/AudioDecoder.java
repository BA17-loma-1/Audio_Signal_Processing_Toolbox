package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.InputStream;

/**
 * An interface representing an audio decoder.
 *
 * @author georgrem, stockan1
 */

public interface AudioDecoder {

    /**
     * Returns the next PCM sample block or null if end of stream is reached.
     *
     * @return
     */
    @Nullable
    short[] getNextSampleBlock();

    /**
     * Returns the sample rate.
     *
     * @return
     */
    int getSampleRate();

    /**
     * Returns the number of channels.
     *
     * @return
     */
    int getChannels();

    /**
     * Sets the audio source to decode.
     *
     * @param is
     */
    void setSource(@NonNull InputStream is);

}
