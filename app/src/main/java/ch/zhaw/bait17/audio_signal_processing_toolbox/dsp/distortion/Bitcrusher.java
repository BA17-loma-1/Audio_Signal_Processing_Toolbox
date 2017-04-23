package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * Quantizer / decimator with smooth control. By David Lowenfels, MusicDSP forum (www.musicdsp.com)
 *
 */

public class Bitcrusher implements AudioEffect {

    private static final String LABEL = "Bitcrusher";
    private static final String DESCRIPTION = "";

    private float normFrequency;
    private int bits;

    /**
     *
     * @param normFrequency     frequency / sampleRate, a value in the range [0,1]
     * @param bits              the number of bits in the range [1,16]
     */
    public Bitcrusher(float normFrequency, int bits) {
        this.normFrequency = normFrequency;
        this.bits = bits;
    }

    /**
     * Sets the normalised frequency.
     *
     * @param normFrequency     frequency / sampleRate, a value in the range [0,1]
     */
    public void setNormFrequency(float normFrequency) {
        if (normFrequency < Constants.BITCRUSHER_MIN_NORM_FREQ) {
            this.normFrequency = Constants.BITCRUSHER_MIN_NORM_FREQ;
        } else if (normFrequency > Constants.BITCRUSHER_MAX_NORM_FREQ) {
            this.normFrequency = Constants.BITCRUSHER_MAX_NORM_FREQ;
        } else {
            this.normFrequency = normFrequency;
        }
    }

    /**
     * Returns the normalised frequency.
     *
     * @return      normalised frequency
     */
    public float getNormalisedFrequency() {
        return normFrequency;
    }


    /**
     * Sets the bit depth.
     *
     * @param bits  the number of bits in the range [1,16]
     */
    public void setBits(int bits) {
        if (bits < Constants.BITCRUSHER_MIN_BIT_DEPTH) {
            this.bits = Constants.BITCRUSHER_MIN_BIT_DEPTH;
        } else if (bits > Constants.BITCRUSHER_MAX_BIT_DEPTH) {
            this.bits = Constants.BITCRUSHER_MAX_BIT_DEPTH;
        } else {
            this.bits = bits;
        }
    }

    /**
     * Returns the bit depth.
     *
     * @return      bit depth
     */
    public int getBits() {
        return bits;
    }

    /**
     * @param input     an array of {@code float} containing the input samples
     * @param output    an array of {@code float} of same length as the input samples array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            double step = Math.pow(0.5, bits);
            double phasor = 0;
            double last = 0;
            for (int i = 0; i < input.length; i++) {
                phasor += normFrequency;
                if (phasor >= 1) {
                    phasor -= 1;
                    // Quantize
                    last = step * Math.floor((input[i] / step) + 0.5);
                }
                // Sample and hold
                output[i] = (float) last;
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
        dest.writeFloat(this.normFrequency);
        dest.writeInt(this.bits);
    }

    protected Bitcrusher(Parcel in) {
        this.normFrequency = in.readFloat();
        this.bits = in.readInt();
    }

    public static final Creator<Bitcrusher> CREATOR = new Creator<Bitcrusher>() {
        @Override
        public Bitcrusher createFromParcel(Parcel source) {
            return new Bitcrusher(source);
        }

        @Override
        public Bitcrusher[] newArray(int size) {
            return new Bitcrusher[size];
        }
    };

}
