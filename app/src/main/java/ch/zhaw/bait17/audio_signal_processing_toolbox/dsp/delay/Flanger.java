package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.delay;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * <p>
 * Based on Professor A D Marshall's (University of Cardiff, UK) MATLAB implementation of a flanger.
 * </p>
 * <p>
 * Source: <a href="http://users.cs.cf.ac.uk/Dave.Marshall/CM0268/PDF/10_CM0268_Audio_FX.pdf">link</a>
 * </p>
 */

public class Flanger extends AudioEffect {

    private static final String LABEL = "Flanger";
    private static final String DESCRIPTION = "A single FIR delay with an oscillating delay time";

    private double carrierFrequency = 1;
    private double samplingFrequency = Constants.DEFAULT_SAMPLE_RATE;
    private double frequencyModulation;
    private int maxDelayInSamples;
    private float amplitude = 0.7f;
    private double maxDelayInMs = 0.003;
    private long index = 0;


    public Flanger(double carrierFrequency, float amplitude, double maxDelayInMs) {
        this.amplitude = amplitude;
        setFrequencyModulation(carrierFrequency);
        setMaxDelayInSamples(maxDelayInMs);
    }

    /**
     * @param input  an array of {@code float} containing the input samples
     *               {@code float} values must be normalised in the range [-1,1]
     * @param output an array of {@code float} of same length as the input samples array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = maxDelayInSamples; i < input.length; ++i) {
                double currentCosine = Math.abs(Math.cos(frequencyModulation * index++));
                int currentDelay = (int) Math.ceil(currentCosine * maxDelayInSamples);
                output[i] = amplitude * input[i] + amplitude * input[i - currentDelay];
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

    public void setMaxDelayInSamples(double maxDelayInMs) {
        this.maxDelayInMs = maxDelayInMs;
        maxDelayInSamples = (int) Math.round(maxDelayInMs * samplingFrequency);
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public void setSamplingFrequency(int sampleRate) {
        samplingFrequency = sampleRate;
        setFrequencyModulation(carrierFrequency);
        setMaxDelayInSamples(maxDelayInMs);
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
        dest.writeInt(this.maxDelayInSamples);
        dest.writeFloat(this.amplitude);
        dest.writeDouble(this.maxDelayInMs);
        dest.writeLong(this.index);
    }

    protected Flanger(Parcel in) {
        this.carrierFrequency = in.readDouble();
        this.samplingFrequency = in.readDouble();
        this.frequencyModulation = in.readDouble();
        this.maxDelayInSamples = in.readInt();
        this.amplitude = in.readFloat();
        this.maxDelayInMs = in.readDouble();
        this.index = in.readLong();
    }

    public static final Creator<Flanger> CREATOR = new Creator<Flanger>() {
        @Override
        public Flanger createFromParcel(Parcel source) {
            return new Flanger(source);
        }

        @Override
        public Flanger[] newArray(int size) {
            return new Flanger[size];
        }
    };
}
