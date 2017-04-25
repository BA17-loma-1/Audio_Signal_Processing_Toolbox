package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * @author georgrem, stockan1
 */

public class Constants {

    // Decoder and audio player
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final int DEFAULT_BITS_PER_SAMPLE = 16;
    public static final int DEFAULT_CHANNELS = 2;
    public static final int DEFAULT_FFT_RESOLUTION = 8192;

    // Filter
    public static final String FREQUENCY_PASS_1 = "fpass1";
    public static final String FREQUENCY_PASS_2 = "fpass2";
    public static final String FREQUENCY_STOP_1 = "fstop1";
    public static final String FREQUENCY_STOP_2 = "fstop2";
    public static final String AMOUNT_RIPPLE_PASS_1 = "Apass1";
    public static final String AMOUNT_RIPPLE_PASS_2 = "Apass2";
    public static final String ATTENUATION_STOP_1 = "Astop1";
    public static final String ATTENUATION_STOP_2 = "Astop2";

    // Bitcrusher
    public static final float BITCRUSHER_MIN_NORM_FREQ = 0;
    public static final float BITCRUSHER_MAX_NORM_FREQ = 1;
    public static final int BITCRUSHER_MIN_BIT_DEPTH = 1;
    public static final int BITCRUSHER_MAX_BIT_DEPTH = 16;
    public static final float BITCRUSHER_DEFAULT_NORM_FREQUENCY = 1.0f;
    public static final int BITCRUSHER_DEFAULT_BITS = 16;

    // Soft clipper
    public static final int SOFT_CLIPPER_MIN_CLIPPING_FACTOR = 1;
    public static final int SOFT_CLIPPER_MAX_CLIPPING_FACTOR = 1000;
    public static final float SOFT_CLIPPER_DEFAULT_CLIPPING_FACTOR = 120f;

    // Waveshaper
    public static final float WAVESHAPER_MIN_CLIPPING_FACTOR = 0f;
    public static final float WAVESHAPER_MAX_CLIPPING_FACTOR = 1.0f;
    public static final float WAVESHAPER_DEFAULT_THRESHOLD = 1.0f;

    // Ring modulator
    public static final int RING_MODULATOR_MIN_MOD_FREQ = 1;
    public static final int RING_MODULATOR_MAX_MOD_FREQ = 5000;
    public static final float RING_MODULATOR_DEFAULT_FREQUENCY = 0;

}
