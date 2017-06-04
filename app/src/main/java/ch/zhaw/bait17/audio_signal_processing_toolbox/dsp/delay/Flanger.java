package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.delay;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Constants;

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

    private float rate = Constants.FLANGER_DEFAULT_RATE;
    private int samplingFrequency = Constants.DEFAULT_SAMPLE_RATE;
    private float amplitude = Constants.FLANGER_DEFAULT_AMPLITUDE;
    private float maxDelayInMs = Constants.FLANGER_DEFAULT_DELAY;
    private int maxDelayInSamples;
    private long index = 0;

    public Flanger(float rate, float amplitude, float maxDelayInMs) {
        this.amplitude = amplitude;
        setRate(rate);
        setMaxDelayInMs(maxDelayInMs);
    }

    protected Flanger(Parcel in) {
        this.rate = in.readFloat();
        this.samplingFrequency = in.readInt();
        this.amplitude = in.readFloat();
        this.maxDelayInMs = in.readFloat();
        this.maxDelayInSamples = in.readInt();
        this.index = in.readLong();
    }

    /**
     * @param input  an array of {@code float} containing the input samples
     *               {@code float} values must be normalised in the range [-1,1]
     * @param output an array of {@code float} of same length as the input samples array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; i++) {
                double phase = Math.abs(Math.cos(2 * Math.PI * index++ *
                        (rate / (float) samplingFrequency)));
                if (index == samplingFrequency) {
                    index = 0;
                }
                int currentDelay = (int) Math.ceil(phase * maxDelayInSamples);
                output[i] = amplitude * input[i];
                if (i - currentDelay >= 0) {
                    output[i] += amplitude * input[i - currentDelay];
                }
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

    /**
     * Sets the flanging rate in Hz.
     *
     * @param rate  flanging rate in interval [0,1] [Hz]
     */
    public void setRate(float rate) {
        this.rate = rate;
    }

    /**
     * Sets the delay.
     *
     * @param maxDelayInMs  delay in milliseconds
     */
    public void setMaxDelayInMs(float maxDelayInMs) {
        this.maxDelayInMs = maxDelayInMs;
        maxDelayInSamples = Math.round(maxDelayInMs * samplingFrequency);
    }

    /**
     * Sets the modulation amplitude.
     *
     * @param amplitude the modulation amplitude in interval [0,1]
     */
    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public void setSamplingFrequency(int sampleRate) {
        if (sampleRate > 0) {
            samplingFrequency = sampleRate;
            setMaxDelayInMs(maxDelayInMs);
        }
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.rate);
        dest.writeInt(this.samplingFrequency);
        dest.writeInt(this.maxDelayInSamples);
        dest.writeFloat(this.amplitude);
        dest.writeFloat(this.maxDelayInMs);
        dest.writeLong(this.index);
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
