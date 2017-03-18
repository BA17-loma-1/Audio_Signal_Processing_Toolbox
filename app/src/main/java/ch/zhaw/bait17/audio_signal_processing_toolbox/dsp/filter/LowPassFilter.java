package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;

/**
 * <p>Finite impulse response low pass filter</p>
 *
 * @author georgrem, stockan1
 */

public class LowPassFilter implements Filter {

    private Context context;
    private float[] fir_coeffs;

    public LowPassFilter(Context context) {
        this.context = context;
        importCoefficients();
    }

    /**
     * Process in-place.
     * Discrete convolution
     * @param samples
     */
    public short[] apply(@NonNull short[] samples) {
        int samplesLength = samples.length;
        short[] output = new short[samplesLength];
        if (fir_coeffs != null) {
            for (int n = 0; n < samplesLength; n++) {
                int accumulator = 0;
                for (int k = 0; k < fir_coeffs.length; k++) {
                    if (n-k > 0 && n-k < samplesLength) {
                        accumulator += fir_coeffs[k] * samples[n-k];
                    }
                }
                if (accumulator > Short.MAX_VALUE) {
                    output[n] = Short.MAX_VALUE;
                } else if (accumulator < Short.MIN_VALUE) {
                    output[n] = Short.MIN_VALUE;
                } else {
                    output[n] = (short) accumulator;
                }
            }
        }
        return output;
    }

    private void importCoefficients() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.b_fir_lowpass)));) {
            String[] b_coefficients = br.readLine().split(",");
            fir_coeffs = new float[b_coefficients.length];
            for (int i = 0; i < fir_coeffs.length; i++) {
                // Check for illegal values.
                float coeff = Float.parseFloat(b_coefficients[i]);
                fir_coeffs[i] = coeff;
            }
        } catch(IOException ex) {
            Toast.makeText(context, "Filter coefficients import failed.\n" + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
