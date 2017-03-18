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
    private short[] fir_coeffs;

    public LowPassFilter(Context context) {
        this.context = context;
        importCoefficients();
    }

    /**
     * Process in-place.
     * @param samples
     */
    public void apply(@NonNull short[] samples) {
        if (fir_coeffs != null) {

        }
    }

    private void importCoefficients() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.b_fir_lowpass)));) {
            String[] b_coefficients = br.readLine().split(",");
            fir_coeffs = new short[b_coefficients.length];
            for (int i = 0; i < fir_coeffs.length; i++) {
                // Check for illegal values.

            }
        } catch(IOException ex) {
            Toast.makeText(context, "Filter coefficients import failed.\n" + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
