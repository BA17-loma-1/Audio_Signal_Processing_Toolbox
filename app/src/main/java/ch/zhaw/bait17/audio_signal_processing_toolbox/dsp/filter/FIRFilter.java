package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * <p>
 *     A class representing a Nth-order discrete-time FIR filter.
 * </p>
 *
 * @author georgrem, stockan1
 */

public class FIRFilter extends Filter {

    private static final String TAG = FIRFilter.class.getSimpleName();
    private FilterSpec filterSpec;
    private final int ORDER;
    private final float[] COEFFICIENTS;         // The impulse response of the filter
    private float[] overlap;

    /**
     * Creates a new instance of {@code FIRFilter}.
     *
     * @param filterSpec        a filter specification
     * @param coefficients      filter coefficients
     */
    public FIRFilter(@NonNull FilterSpec filterSpec, @NonNull float[] coefficients) {
        this.filterSpec = filterSpec;
        COEFFICIENTS = new float[coefficients.length];
        System.arraycopy(coefficients, 0, COEFFICIENTS, 0, coefficients.length);
        ORDER = COEFFICIENTS.length - 1;
        overlap = new float[coefficients.length];
    }

    /**
     * Returns the filter specification.
     *
     * @return      the filter specification
     */
    @Override
    public FilterSpec getFilterSpec() {
        return filterSpec;
    }

    /**
     * Returns the filter order.
     *
     * @return      the filter order
     */
    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * Returns the label that identifies this filter in e.g. a view.
     *
     * @return      label
     */
    @Override
    public String getLabel() {
        return filterSpec.getFilterType().getLabel();
    }

    /**
     * Returns a description of the filter.
     *
     * @return      filter description
     */
    @Override
    public String getDescription() {
        return filterSpec.getDescription();
    }

    /**
     * <p>
     *     Process a block of PCM samples with discrete convolution.
     *     Calculates the full convolution and takes advantage of symmetry of FIR filters
     *     to reduce multiplications. </br>
     *     Input and output samples arrays must have the same length.
     * </p>
     * <p>
     *     See The Scientist and Engineer's Guide to Digital Signal Processing for detailed
     *     information about convolution. </br>
     *     <a href="http://www.dspguide.com/ch6/4.htm">www.dspguide.com</a>
     * </p>
     *
     * @param input     {@code float} array of filter input samples
     * @param output    {@code float} array of filter output samples
     */
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length && input.length != 0 && getOrder() > 0) {
            float[] fullConvolution = new float[input.length + getOrder()];
            convolveInputSide(input, fullConvolution, input.length);
            System.arraycopy(fullConvolution, 0, output, 0, output.length);
        }
    }

    /**
     * <p>
     *     Discrete convolution using the input side algorithm.
     *     FIR filters have symmetrical impulse response. Full convolution is performed but
     *     not needed calculations due to symmetry in impulse response are eliminated. </br>
     *     Input and output samples arrays must have the same length.
     * </p>
     * <p>
     *     Source: <a href="https://christianfloisand.wordpress.com/2013/02/18/the-different-sides-of-convolution/">christianfloisand.wordpress.com</a>
     * </p>
     *
     * @param input             array of input samples
     * @param output            array that will hold the output samples
     * @param inputLength       the length of the input samples array
     */
    private void convolveInputSide(@NonNull float[] input, @NonNull float[] output, int inputLength) {
        int i,j;
        float temp = 0;
        int halfOrder = getOrder() / 2;
        for (i = 0; i < inputLength; ++i) {
            for (j = 0; j < halfOrder; ++j) {
                temp = COEFFICIENTS[j] * input[i];
                output[i + j] += temp;
                output[i + COEFFICIENTS.length - j - 1] += temp;   // Symmetry
            }
            output[i + j] += COEFFICIENTS[j] * input[i];           // Midpoint value
        }

        // Kind of "Overlap-add" in time-domain convolution
        for (int k = 0; k < COEFFICIENTS.length; ++k) {
            output[k] += overlap[k];
            overlap[k] = output[inputLength + k - 1];
        }
    }

    /**
     * <p>
     *     Discrete convolution using the output side algorithm.
     *     FIR filters have symmetrical impulse response. Full convolution is performed but
     *     not needed calculations due to symmetry in impulse response are eliminated. </br>
     *     Input and output samples arrays must have the same length.
     * </p>
     * <p>
     *     Source: <a href="https://christianfloisand.wordpress.com/2013/02/18/the-different-sides-of-convolution/">christianfloisand.wordpress.com</a>
     * </p>
     *
     * @param input             array of input samples
     * @param output            array that will hold the output samples
     * @param inputLength       the length of the input samples array
     */
    private void convolveOutputSide(@NonNull float[] input, @NonNull float[] output, int inputLength) {

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
        dest.writeFloatArray(this.overlap);
    }

    protected FIRFilter(Parcel in) {
        this.filterSpec = in.readParcelable(FilterSpec.class.getClassLoader());
        this.ORDER = in.readInt();
        this.COEFFICIENTS = in.createFloatArray();
        this.overlap = in.createFloatArray();
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
