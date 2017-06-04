package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Constants;

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

    private int samplingFrequency = Constants.DEFAULT_SAMPLE_RATE;
    private float modulationFrequency;
    private float amplitude = Constants.TREMOLO_DEFAULT_AMPLITUDE;
    private long index = 0;

    public Tremolo(float modulationFrequency, float amplitude) {
        this.amplitude = amplitude;
        setModulationFrequency(modulationFrequency);
    }

    protected Tremolo(Parcel in) {
        this.samplingFrequency = in.readInt();
        this.modulationFrequency = in.readFloat();
        this.amplitude = in.readFloat();
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
                output[i] = input[i] * (float) (1 + amplitude * Math.sin(2 * Math.PI * index++ *
                        (modulationFrequency / (float) samplingFrequency)));
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

    public void setModulationFrequency(float modulationFrequency) {
        this.modulationFrequency = modulationFrequency;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public void setSamplingFrequency(int sampleRate) {
        if (sampleRate > 0) {
            samplingFrequency = sampleRate;
        }
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.samplingFrequency);
        dest.writeFloat(this.modulationFrequency);
        dest.writeFloat(this.amplitude);
        dest.writeLong(this.index);
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
