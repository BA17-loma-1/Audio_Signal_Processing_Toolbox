package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Constants;

/**
 * <p>
 * Tube distortion simulation effect. From DAFX Digital Audio Effects, second edition, page 122.
 * </br>
 * Authors: Bendiksen, Dutilleux, Zölzer
 * </p>
 */

public class TubeDistortion extends AudioEffect {

    private static final String LABEL = "Tube distortion";
    private static final String DESCRIPTION = "Asymmetrical function with static characteristic: performs clipping of large negative values and is linear for positive values";

    /*
        Work point, controls the linearity of the transfer function for low input levels,
        more negative = more linear.
     */
    private float q = -0.1f;
    /*
        Controls the distortion's character, a higher number gives a harder distortion,
        dist > 0.
     */
    private int dist = 8;

    /*
        Mix of original and distorted sound, 1 = only distorted
     */
    private float mix = Constants.TUBE_DISTORTION_DEFAULT_MIX;

    /*
        The amount of distortion, > 0
     */
    private float gain = Constants.TUBE_DISTORTION_DEFAULT_GAIN;

    public TubeDistortion() {

    }

    protected TubeDistortion(Parcel in) {
        this.q = in.readFloat();
        this.dist = in.readInt();
        this.mix = in.readFloat();
        this.gain = in.readFloat();
    }

    /**
     * Sets the gain.
     *
     * @param gain    the amount of distortion, > 0
     */
    public void setGain(float gain) {
        this.gain = gain;
    }

    /**
     * Mix of original and distorted sound, value in the range [0,1].
     * 1 = only distorted
     *
     * @param mix       mix ration original/distorted
     */
    public void setMix(float mix) {
        if (mix < 0) {
            mix = 0;
        }
        if (mix > 1) {
            mix = 1;
        }
        this.mix = mix;
    }

    /**
     * @param input             an array of {@code float} containing the input samples
     *                          {@code float} values must be normalised in the range [-1,1]
     * @param output            an array of {@code float} of same length as the input samples array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            float max = 0.01f;
            for (int i = 0; i < input.length; i++) {
                if (Math.abs(input[i]) > max) {
                    max = Math.abs(input[i]);
                }
            }
            float[] z = new float[input.length];
            float maxZ = 0.01f;
            for (int i = 0; i < input.length; i++) {
                float normalisation = input[i] * gain / max;

                if (q == 0.0f) {
                    z[i] = (float) (normalisation / (1 - Math.exp(-dist * normalisation)));
                    if (Math.abs(normalisation - q) < 0.001) {
                        z[i] = 1.0f / dist;
                    }
                } else {
                    z[i] = (float) ((normalisation - q) / (1 - Math.exp(-dist * (normalisation - q))) + q / (1 - Math.exp(dist * q)));
                    if (Math.abs(normalisation - q) < 0.001) {
                        z[i] = (float) ((1.0f / dist) + (q / (1 - Math.exp(dist * q))));
                    }
                }

                if (Math.abs(z[i]) > maxZ) {
                    maxZ = Math.abs(z[i]);
                }
            }

            float maxOut = 0.01f;
            for (int i = 0; i < output.length; i++) {
                output[i] = mix * z[i] * max / maxZ + (1 - mix) * input[i];
                if (Math.abs(output[i]) > maxOut) {
                    maxOut = Math.abs(output[i]);
                }
            }

            for (int i = 0; i < output.length; i++) {
                output[i] = output[i] * max / maxOut;
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
        dest.writeFloat(this.q);
        dest.writeInt(this.dist);
        dest.writeFloat(this.mix);
        dest.writeFloat(this.gain);
    }

    public static final Creator<TubeDistortion> CREATOR = new Creator<TubeDistortion>() {
        @Override
        public TubeDistortion createFromParcel(Parcel source) {
            return new TubeDistortion(source);
        }

        @Override
        public TubeDistortion[] newArray(int size) {
            return new TubeDistortion[size];
        }
    };
}
