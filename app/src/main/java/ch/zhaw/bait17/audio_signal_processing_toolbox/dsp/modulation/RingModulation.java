package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * Ring modulation is a multiplication of the input signal with a carrier signal.
 *
 * @author georgrem, stockan1
 */

public class RingModulation extends AudioEffect {

    private static final String LABEL = "Ring modulation";
    private static final String DESCRIPTION = "Amplitude modulation without the original signal, duplicates and shifts the spectrum, modifies pitch and timbre";

    private double modulationFrequency;
    private long index = 0;

    /**
     * * Creates an instance of {@code RingModulation}.
     *
     * @param carrierFrequency          the carrier frequency (modulation frequency) must be >= 0
     * @throws IllegalArgumentException if carrier frequency < 0
     */
    public RingModulation(double carrierFrequency) throws IllegalArgumentException {
        if (carrierFrequency < 0) {
            throw new IllegalArgumentException("Carrier frequency must be >= 0.");
        }
        setModulationFrequency(carrierFrequency);
    }

    protected RingModulation(Parcel in) {
        this.modulationFrequency = in.readDouble();
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
                output[i] = (float) (input[i] * Math.cos(2 * Math.PI * modulationFrequency *
                        (index++ / (float) getSamplingFrequency())));
                if (index == getSamplingFrequency()) {
                    index = 0;
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

    public void setModulationFrequency(double modFreq) {
        modulationFrequency = modFreq;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.modulationFrequency);
        dest.writeLong(this.index);
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
