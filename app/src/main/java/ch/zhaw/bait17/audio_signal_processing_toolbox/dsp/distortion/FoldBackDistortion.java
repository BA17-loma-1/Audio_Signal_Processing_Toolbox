package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * Simple Fold-back distortion filter. By hellfire@upb.de, MusicDSP forum (www.musicdsp.com)
 *
 */
public class FoldBackDistortion implements AudioEffect {

    private static final String LABEL = "Fold-back distortion";
    private static final String DESCRIPTION = "";
    private float threshold;

    /**
     * Creates a new FoldBackDistortion instance.
     *
     * @param threshold         value > 0
     */
    public FoldBackDistortion(float threshold) {
        this.threshold = threshold;
    }

    /**
     * Sets the threshold value.
     *
     * @param threshold
     */
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    /**
     * <p>
     *     Applies the {@code AudioEffect} to a block of PCM samples.
     *     Input and output sample arrays must have the same length.
     * </p>
     *
     * @param input     array of {@code float} input samples
     * @param output    array of {@code float} output samples must be of same length as input array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            for (int i = 0; i < input.length; i++) {
                if (input[i] > threshold || input[i] < -threshold) {
                    output[i] = Math.abs((input[i] - threshold % 4 * threshold) -
                            (2 * threshold)) - threshold;
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


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.threshold);
    }

    protected FoldBackDistortion(Parcel in) {
        this.threshold = in.readFloat();
    }

    public static final Creator<FoldBackDistortion> CREATOR = new Creator<FoldBackDistortion>() {
        @Override
        public FoldBackDistortion createFromParcel(Parcel source) {
            return new FoldBackDistortion(source);
        }

        @Override
        public FoldBackDistortion[] newArray(int size) {
            return new FoldBackDistortion[size];
        }
    };

}
