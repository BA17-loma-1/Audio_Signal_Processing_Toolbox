package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * @author georgrem, stockan1
 */

public class RingModulation extends AudioEffect {

    private static final String LABEL = "Ring modulation";
    private static final String DESCRIPTION = "Amplitude modulation without the original signal, duplicates and shifts the spectrum, modifies pitch and timbre";

    private double fMod;
    private long index = 0;
    private int sampleRate;
    private double frequency;


    public RingModulation(double frequency) {
        setFrequency(frequency);
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
        this.fMod = 2. * Math.PI * frequency / (double) sampleRate;
    }

    /**
     * The (ring) modulation is a simple multiplication of the waveform with the carrier frequency.
     *
     * @param input  an array of {@code float} containing the input samples
     *               {@code float} values must be normalised in the range [-1,1]
     * @param output an array of {@code float} of same length as the input samples array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; ++i) {
                output[i] *= Math.cos(fMod * index++);
            }
        }
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
        setFrequency(frequency);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.fMod);
        dest.writeLong(this.index);
    }

    protected RingModulation(Parcel in) {
        this.fMod = in.readDouble();
        this.index = in.readLong();
    }

    public static final Creator<RingModulation> CREATOR = new Creator<RingModulation>() {
        @Override
        public RingModulation createFromParcel(Parcel source) {
            return new RingModulation(source);
        }

        @Override
        public RingModulation[] newArray(int size) {
            return new RingModulation[size];
        }
    };
}
