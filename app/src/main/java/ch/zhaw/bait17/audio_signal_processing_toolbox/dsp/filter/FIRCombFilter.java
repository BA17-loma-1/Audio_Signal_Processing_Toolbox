package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * @author georgrem, stockan1
 */

public class FIRCombFilter extends AudioEffect {

    private static final String LABEL = "FIR comb filter";
    private static final String DESCRIPTION = "Simulates a single delay. The response of this filter is made up of the direct and the delayed signal.";
    private static final float DELAY_GAIN = 0.5f;

    private float[] delayLine;
    private int delayInSamples;

    /**
     * * Creates an instance of {@code FIRCombFilter}.
     *
     * @param delay                     the amount of delay time in seconds must be >= 0
     * @throws IllegalArgumentException if delay in ms is < 0
     */
    public FIRCombFilter(float delay) throws IllegalArgumentException {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay in seconds must be >= 0.");
        }
        setDelay(delay);
    }

    protected FIRCombFilter(Parcel in) {
        this.delayLine = in.createFloatArray();
        this.delayInSamples = in.readInt();

    }

    /**
     * <p>
     * Applies the {@code AudioEffect} to a block of PCM samples.
     * Input and output sample arrays must have the same length.
     * </p>
     *
     * @param input  array of {@code float} input samples
     * @param output arary of {@code float} output samples must be of same length as input array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; i++) {
                output[i] = input[i];
                if (delayLine != null && delayLine.length > 1) {
                    output[i] += DELAY_GAIN * delayLine[delayLine.length - 1];
                    // shift right one element in delay line
                    System.arraycopy(delayLine, 0, delayLine, 1, delayLine.length - 1);
                    delayLine[0] = input[i];
                }
            }
        }
    }

    /**
     * Sets the amount of delay time.
     *
     * @param delay  delay in seconds
     */
    public void setDelay(float delay) {
        delayInSamples = Math.round(delay * getSamplingFrequency());
        delayLine = new float[delayInSamples];
    }

    /**
     * <p>
     * Returns the label of the {@code AudioEffect}. </br>
     * This label will be displayed in the {@code AudioEffect} drop down list.
     * </p>
     *
     * @return label
     */
    @Override
    public String getLabel() {
        return LABEL;
    }

    /**
     * <p>
     * Returns the description of the {@code AudioEffect}. </br>
     * This description will be displayed in the {@code AudioEffect} drop down list.
     * </p>
     *
     * @return description
     */
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(this.delayLine);
        dest.writeInt(this.delayInSamples);
    }

    public static final Creator<FIRCombFilter> CREATOR = new Creator<FIRCombFilter>() {
        @Override
        public FIRCombFilter createFromParcel(Parcel source) {
            return new FIRCombFilter(source);
        }

        @Override
        public FIRCombFilter[] newArray(int size) {
            return new FIRCombFilter[size];
        }
    };
}
