package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * <p>
 * Based on Professor A D Marshall's (University of Cardiff, UK) MATLAB implementation of a tremolo effect.
 * </p>
 * <p>
 * Source: <a href="http://users.cs.cf.ac.uk/Dave.Marshall/CM0268/PDF/10_CM0268_Audio_FX.pdf">link</a>
 * </p>
 */

public class Tremolo extends AudioEffect {

    private static final String LABEL = "Tremolo";
    private static final String DESCRIPTION = "Amplitude modulation";

    private double carrierFrequency;
    private double samplingFrequency = Constants.DEFAULT_SAMPLE_RATE;
    private double frequencyModulation;
    private float amplitude = 0.5f;
    private long index = 0;


    public Tremolo(double carrierFrequency, float amplitude) {
        this.amplitude = amplitude;
        setFrequencyModulation(carrierFrequency);
    }

    /**
     * @param input  an array of {@code float} containing the input samples
     *               {@code float} values must be normalised in the range [-1,1]
     * @param output an array of {@code float} of same length as the input samples array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; ++i) {
                output[i] *= 1 + amplitude * Math.cos(frequencyModulation * index++);
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

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public void setSamplingFrequency(int sampleRate) {
        samplingFrequency = sampleRate;
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
        dest.writeFloat(this.amplitude);
        dest.writeLong(this.index);
    }

    protected Tremolo(Parcel in) {
        this.carrierFrequency = in.readDouble();
        this.samplingFrequency = in.readDouble();
        this.frequencyModulation = in.readDouble();
        this.amplitude = in.readFloat();
        this.index = in.readLong();
    }

    public static final Creator<Tremolo> CREATOR = new Creator<Tremolo>() {
        @Override
        public Tremolo createFromParcel(Parcel source) {
            return new Tremolo(source);
        }

        @Override
        public Tremolo[] newArray(int size) {
            return new Tremolo[size];
        }
    };
}
