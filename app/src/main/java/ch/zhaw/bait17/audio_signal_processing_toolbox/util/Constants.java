package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

import ch.zhaw.bait17.audio_signal_processing_toolbox.fft.WindowType;

/**
 * Holds all constants used throughout the application.
 *
 * @author georgrem, stockan1
 */

public interface Constants {

    // Decoder, FFT,  audio player
    int DEFAULT_SAMPLE_RATE = 44100;
    int DEFAULT_CHANNELS = 2;
    WindowType DEFAULT_WINDOW = WindowType.HAMMING;

    // FIR Filter
    String FREQUENCY_PASS_1 = "fpass1";
    String FREQUENCY_PASS_2 = "fpass2";
    String FREQUENCY_STOP_1 = "fstop1";
    String FREQUENCY_STOP_2 = "fstop2";
    String AMOUNT_RIPPLE_PASS_1 = "Apass1";
    String AMOUNT_RIPPLE_PASS_2 = "Apass2";
    String ATTENUATION_STOP_1 = "Astop1";
    String ATTENUATION_STOP_2 = "Astop2";

    // FIR comb filter
    float FIR_COMB_FILTER_MAX_DELAY = 0.1f;
    float FIR_COMB_FILTER_DEFAULT_DELAY = 0.005f;

    // Bitcrusher
    float BITCRUSHER_MIN_NORM_FREQ = 0;
    float BITCRUSHER_MAX_NORM_FREQ = 1;
    int BITCRUSHER_MIN_BIT_DEPTH = 1;
    int BITCRUSHER_MAX_BIT_DEPTH = 16;
    float BITCRUSHER_DEFAULT_NORM_FREQUENCY = 0.1f;
    int BITCRUSHER_DEFAULT_BITS = 8;

    //  Waveshaper
    float WAVESHAPER_DEFAULT_THRESHOLD = 5.0f;
    float WAVESHAPER_MAX_THRESHOLD = 25.0f;

    // Soft clipper
    float SOFT_CLIPPER_MAX_CLIPPING_FACTOR = 100.0f;
    float SOFT_CLIPPER_DEFAULT_CLIPPING_FACTOR = 20.0f;

    // Tube distortion
    float TUBE_DISTORTION_MAX_GAIN = 10;
    float TUBE_DISTORTION_DEFAULT_GAIN = 1.5f;
    float TUBE_DISTORTION_MAX_MIX = 1;
    float TUBE_DISTORTION_DEFAULT_MIX = 0.5f;

    // Ring modulator
    int RING_MODULATOR_MAX_MOD_FREQUENCY = 800;
    int RING_MODULATOR_DEFAULT_FREQUENCY = 50;

    // Tremolo
    float TREMOLO_MAX_MOD_FREQUENCY = 20.0f;
    float TREMOLO_DEFAULT_MOD_FREQUENCY = 5.0f;
    float TREMOLO_MAX_AMPLITUDE = 1.0f;
    float TREMOLO_DEFAULT_AMPLITUDE = 0.5f;

    // Flanger
    float FLANGER_MAX_RATE = 1.0f;
    float FLANGER_DEFAULT_RATE = 0.5f;
    float FLANGER_MAX_AMPLITUDE = 1;
    float FLANGER_DEFAULT_AMPLITUDE = 0.7f;
    float FLANGER_MAX_DELAY = 0.015f;
    float FLANGER_DEFAULT_DELAY = 0.003f;

    // Linear gain
    float GAIN_DEFAULT = 1.0f;
    float GAIN_MAX = 2.0f;
}
