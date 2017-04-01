package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * <p>
 *     A class representing a Nth-order discrete-time FIR filter.
 * </p>
 * @author georgrem, stockan1
 */

public class FIRFilter implements Filter {

    private static final String TAG = FIRFilter.class.getSimpleName();
    private FilterSpec filterSpec;
    private final int ORDER;
    private final float[] COEFFICIENTS;         // The impulse response of the filter
    private int[] overlap;

    /**
     *
     * @param filterSpec
     * @param coefficients
     */
    public FIRFilter(@NonNull FilterSpec filterSpec, @NonNull float[] coefficients) {
        this.filterSpec = filterSpec;
        COEFFICIENTS = new float[coefficients.length];
        System.arraycopy(coefficients, 0, COEFFICIENTS, 0, coefficients.length);
        ORDER = COEFFICIENTS.length - 1;
        overlap = new int[coefficients.length];
    }

    /**
     * Returns the filter specifications.
     * @return
     */
    @Override
    public FilterSpec getFilterSpec() {
        return filterSpec;
    }

    /**
     * Returns the filter order.
     * @return
     */
    public int getOrder() {
        return ORDER;
    }

    /**
     * <p>
     *     Process a block of PCM samples with discrete convolution.
     *     Calculates the full convolution and takes advantage of symmetry of FIR filters
     *     to reduce multiplications.
     * </p>
     * <p>
     *     See The Scientist and Engineer's Guide to Digital Signal Processing for detailed
     *     information about convolution. {@Link http://www.dspguide.com/ch6/4.htm}
     * </p>
     *
     * @param input a {@code short} array of input samples
     * @return a {@code short} array of filtered samples
     */
    public short[] apply(@NonNull short[] input) {
        if (input.length == 0 || getOrder() <= 0) {
            return  input;
        }
        short[] fullConvolution = new short[input.length + getOrder()];
        convolveInputSide(input, fullConvolution, input.length);
        short[] output = new short[input.length];
        System.arraycopy(fullConvolution, 0, output, 0, output.length);
        return output;
    }

    /**
     * <p>
     *     Discrete convolution using the input side algorithm.
     *     FIR filters have symmetrical impulse response. Full convolution is performed but
     *     not needed calculations due to symmetry in impulse response are eliminated.
     * </p>
     * <p>
     *     Source: {@Link https://christianfloisand.wordpress.com/2013/02/18/the-different-sides-of-convolution/}
     * </p>
     * @param input
     * @return
     */
    private void convolveInputSide(short[] input, short[] output, int inputLength) {
        int i,j;
        float temp;
        int halfOrder = getOrder() / 2;
        for (i = 0; i < inputLength; ++i) {
            for (j = 0; j < halfOrder; ++j) {
                temp = COEFFICIENTS[j] * input[i];
                output[i + j] += temp;
                output[i + COEFFICIENTS.length - j - 1] += temp;   // Symmetry
            }
            output[i + j] += COEFFICIENTS[j] * input[i];           // Midpoint value
        }

        // Overlap-add
        for (int k = 0; k < COEFFICIENTS.length; ++k) {
            output[k] += overlap[k];
            overlap[k] = output[inputLength + k - 1];
        }
    }

    /**
     * <p>
     *     Discrete convolution using the output side algorithm.
     *     FIR filters have symmetrical impulse response. Full convolution is performed but
     *     not needed calculations due to symmetry in impulse response are eliminated.
     * </p>
     * <p>
     *     Source: {@Link https://christianfloisand.wordpress.com/2013/02/18/the-different-sides-of-convolution/}
     * </p>
     * @param input
     * @return
     */
    private void convolveOutputSide(short[] input, short[] output) {

    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.filterSpec, flags);
        dest.writeInt(this.ORDER);
        dest.writeFloatArray(this.COEFFICIENTS);
        dest.writeIntArray(this.overlap);
    }

    protected FIRFilter(Parcel in) {
        this.filterSpec = in.readParcelable(FilterSpec.class.getClassLoader());
        this.ORDER = in.readInt();
        this.COEFFICIENTS = in.createFloatArray();
        this.overlap = in.createIntArray();
    }

    public static final Creator<FIRFilter> CREATOR = new Creator<FIRFilter>() {
        @Override
        public FIRFilter createFromParcel(Parcel source) {
            return new FIRFilter(source);
        }

        @Override
        public FIRFilter[] newArray(int size) {
            return new FIRFilter[size];
        }
    };

}
