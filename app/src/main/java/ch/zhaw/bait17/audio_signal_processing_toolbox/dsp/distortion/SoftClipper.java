package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * Soft clipping function. By scoofy@inf.elte.hu, MusicDSP forum (www.musicdsp.com)
 *
 */

public class SoftClipper implements AudioEffect {

    private static final String LABEL = "Soft clipper";
    private static final String DESCRIPTION = "";
    private float clippingFactor;

    /**
     *
     * @param clippingFactor    value should be in the range [1,1000]
     */
    public SoftClipper(float clippingFactor) {
        this.clippingFactor = clippingFactor;
    }

    /**
     *
     * @param clippingFactor    value should be in the range [1,1000]
     */
    public void setClippingFactor(float clippingFactor) {
        this.clippingFactor = clippingFactor;
    }

    /**
     * @param input             an array of {@code float} containing the input samples
     *                          {@code float} values must be normalised in the range [-1,1]
     * @param output            an array of {@code float} of same length as the input samples array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            float invAtanShape = 1.0f / (float) Math.atan(clippingFactor);
            for (int i = 0; i < input.length; i++) {
                output[i] = invAtanShape * (float) Math.atan(input[i] * clippingFactor);
            }
        }
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() { return DESCRIPTION; }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.clippingFactor);
    }

    protected SoftClipper(Parcel in) {
        this.clippingFactor = in.readFloat();
    }

    public static final Creator<SoftClipper> CREATOR = new Creator<SoftClipper>() {
        @Override
        public SoftClipper createFromParcel(Parcel source) {
            return new SoftClipper(source);
        }

        @Override
        public SoftClipper[] newArray(int size) {
            return new SoftClipper[size];
        }
    };

}
