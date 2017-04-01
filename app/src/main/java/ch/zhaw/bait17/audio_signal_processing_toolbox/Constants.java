package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * @author georgrem, stockan1
 */

public class Constants {

    // Decoder and audio player
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final int DEFAULT_BITS_PER_SAMPLE = 16;
    public static final int DEFAULT_CHANNELS = 2;
    public static final int DEFAULT_FFT_RESOLUTION = 2048;

    // Filter
    public static final String FREQUENCY_PASS_1 = "fpass1";
    public static final String FREQUENCY_PASS_2 = "fpass2";
    public static final String FREQUENCY_STOP_1 = "fstop1";
    public static final String FREQUENCY_STOP_2 = "fstop2";
    public static final String AMOUNT_RIPPLE_PASS_1 = "Apass1";
    public static final String AMOUNT_RIPPLE_PASS_2 = "Apass2";
    public static final String ATTENUATION_STOP_1 = "Astop1";
    public static final String ATTENUATION_STOP_2 = "Astop2";

}
