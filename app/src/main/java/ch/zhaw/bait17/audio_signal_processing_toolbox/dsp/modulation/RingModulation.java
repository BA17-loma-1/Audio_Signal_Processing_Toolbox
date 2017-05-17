package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Constants;

/**
 * @author georgrem, stockan1
 */

public class RingModulation extends AudioEffect {

    private static final String LABEL = "Ring modulation";
    private static final String DESCRIPTION = "Amplitude modulation without the original signal, duplicates and shifts the spectrum, modifies pitch and timbre";

    private double carrierFrequency;
    private double samplingFrequency = Constants.DEFAULT_SAMPLE_RATE;
    private double frequencyModulation;
    private long index = 0;

    public RingModulation(double carrierFrequency) {
        setFrequencyModulation(carrierFrequency);
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
                output[i] *= Math.cos(frequencyModulation * index++);
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

    public void setFrequencyModulation(double carrierFrequency) {
        this.carrierFrequency = carrierFrequency;
        frequencyModulation = 2 * Math.PI;
        if (samplingFrequency > 0) {
            frequencyModulation *= (carrierFrequency / samplingFrequency);
        }
    }

    @Override
    public void setSamplingFrequency(int samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
        setFrequencyModulation(carrierFrequency);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.carrierFrequency);
        dest.writeDouble(this.samplingFrequency);
        dest.writeDouble(this.frequencyModulation);
        dest.writeLong(this.index);
    }

    protected RingModulation(Parcel in) {
        this.carrierFrequency = in.readDouble();
        this.samplingFrequency = in.readDouble();
        this.frequencyModulation = in.readDouble();
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
