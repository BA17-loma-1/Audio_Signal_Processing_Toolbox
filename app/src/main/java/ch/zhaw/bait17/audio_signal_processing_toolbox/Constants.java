package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * @author georgrem, stockan1
 */

public class Constants {

    // Decoder and audio player
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final int DEFAULT_BITS_PER_SAMPLE = 16;
    public static final int DEFAULT_CHANNELS = 2;
    public static final int DEFAULT_FFT_RESOLUTION = 4096;

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
    public static final float MIN_NORM_FREQ = 0;
    public static final float MAX_NORM_FREQ = 1;
    public static final int MIN_BIT_DEPTH = 1;
    public static final int MAX_BIT_DEPTH = 16;
    public static final float BITCRUSHER_DEFAULT_NORM_FREQUENCY = 0.1f;
    public static final int BITCRUSHER_DEFAULT_BITS = 8;

    // Soft clipper
    public static final float SOFT_CLIPPER_DEFAULT_CLIPPING_FACTOR = 120f;

    // Ring modulator
    public static final float RING_MODULATOR_DEFAULT_FREQUENCY = 5;

}
