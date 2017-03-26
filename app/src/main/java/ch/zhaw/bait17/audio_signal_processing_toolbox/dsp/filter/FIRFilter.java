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

    private final int ORDER;
    private float[] fir_coeffs;         // The impulse response of the filter_view

    /**
     *
     * @param coefficients
     */
    public FIRFilter(@NonNull float[] coefficients) {
        fir_coeffs = new float[coefficients.length];
        System.arraycopy(coefficients, 0, fir_coeffs, 0, coefficients.length);
        ORDER = fir_coeffs.length - 1;
    }


    /**
     * <p>
     *     Process in-place with discrete convolution.
     *     Calculate only the samples in the output signal where the impulse response is fully
     *     immersed in the input signal.
     * </p>
     * <p>
     *     See The Scientist and Engineer's Guide to Digital Signal Processing for detailed
     *     information about convolution. {@Link http://www.dspguide.com/ch6/4.htm}
     * </p>
     *
     * @param input a {@code short} array of input samples
     * @return a {@code short} array of filtered samples
     */
    @Override
    public short[] apply(@NonNull short[] input) {
        final int samplesLength = input.length;
        short[] output = new short[samplesLength];
        System.arraycopy(input, 0, output, 0, output.length);
        if (fir_coeffs != null) {
            /*
             This program handles undefined samples in the input signal by ignoring them,
             therefore we need to set the start index for the outer loop.
             */
            int convolutionLength = samplesLength + fir_coeffs.length - 1;
            for (int n = 0; n < convolutionLength; n++) {
                // accumulator holds the convolution sum.
                int accumulator = 0;
                for (int k = 0; k < fir_coeffs.length; k++) {
                    if (n-k >= 0 && n-k < input.length) {
                        accumulator += fir_coeffs[k] * input[n - k];
                    }
                }
                if (n < output.length) {
                    if (accumulator > Short.MAX_VALUE) {
                        output[n] = Short.MAX_VALUE;
                    } else if (accumulator < Short.MIN_VALUE) {
                        output[n] = Short.MIN_VALUE;
                    } else {
                        output[n] = (short) accumulator;
                    }
                }
            }
        }
        return output;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ORDER);
        dest.writeFloatArray(this.fir_coeffs);
    }

    protected FIRFilter(Parcel in) {
        this.ORDER = in.readInt();
        this.fir_coeffs = in.createFloatArray();
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
