package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.graphics.Canvas;

/**
 * @author georgrem, stockan1
 */

public interface SpectrumRenderer {

    /**
     *
     * @param canvas The canvas to draw on.
     * @param samples The PCM samples to transform.
     * @param sampleRate The sample rate of the audio file.
     */
    void render(Canvas canvas, short[] samples, int sampleRate);

}
