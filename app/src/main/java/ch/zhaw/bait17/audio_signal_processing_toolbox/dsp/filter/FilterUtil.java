package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.support.annotation.Nullable;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;

/**
 * <p>
 *     Some utilities for filters.
 * </p>
 *
 * @author georgrem, stockan1
 */

public class FilterUtil {

    @Nullable
    public static float[] getCoefficients(InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String[] line = br.readLine().split(",");
            float[] coefficients = new float[line.length];
            for (int i = 0; i < coefficients.length; i++) {
                // Check for illegal values ? --> stable filter_view
                float c = Float.parseFloat(line[i]);
                coefficients[i] = c;
            }
            return coefficients;
        } catch(IOException ex) {
            Toast.makeText(ApplicationContext.getAppContext(),
                    "Filter coefficients import failed.\n" + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}
