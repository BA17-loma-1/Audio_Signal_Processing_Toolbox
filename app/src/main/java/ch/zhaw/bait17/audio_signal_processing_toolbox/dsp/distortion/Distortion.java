package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

/**
 * <p>
 *     Base class for waveshaper, saturation and various other distortion effects.
 * </p>
 * <p>
 *     Most of the distortion effects used in this Android application are sourced from
 *     MusicDSP. According to its creator <b>Bram de Jong</b>, "Musicdsp.org is a
 *     collection of data and audio algorithms, gathered for the music dsp community".
 *     See <a href="http://www.musicdsp.com">www.musicdsp.com</a>
 * </p>
 */

public abstract class Distortion {

    /**
     * Sigmoid function.
     * By Bram de Jong
     * See <a href="https://en.wikipedia.org/wiki/Sigmoid_function">Sigmoid function on Wikipedia</a>
     *
     * @param   x input
     * @return  function output
     */
    protected static float sigmoid(float x) {
        if (Math.abs(x) < 1) {
            return x * (1.5f - (0.5f * x *x ));
        } else {
            return x > 0.f ? 1.f : -1.f;
        }
    }

    /**
     * Fast computation of arctangent function (inverse tangent) for input value x.
     * By antiprosynthesis@hotmail.com
     *
     * @param   x input
     * @return  function ouput
     */
    protected static float fastAtan(float x) {
        return x / ((1.0f + 0.28f) * (x * x));
    }

}
