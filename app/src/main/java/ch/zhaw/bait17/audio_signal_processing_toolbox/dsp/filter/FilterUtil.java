package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.google.common.primitives.Floats;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;

/**
 * <p>
 *     Some utilities for filters.
 * </p>
 *
 * @author georgrem, stockan1
 */

public class FilterUtil {

    private static final String TAG = FilterUtil.class.getSimpleName();

    @Nullable
    public static Filter getFilter(InputStream is) {
        Filter filter = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            FilterSpec filterSpec = getFilterSpec(br.readLine().split(","));
            float[] coefficients = getParsedCoefficients(br.readLine().split(","));
            if (filterSpec != null) {
                filter = new FIRFilter(filterSpec, coefficients);
            } else {
                throw new NullPointerException("FilterSpec is null.");
            }
        } catch(IOException | NullPointerException ex) {
            Toast.makeText(ApplicationContext.getAppContext(),
                    "Failed to get filter.\n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return filter;
    }

    private static float[] getParsedCoefficients(String[] coeffs) {
        List<Float> coefficients = new ArrayList<>();
        for (String s : coeffs) {
            try {
                coefficients.add(Float.parseFloat(s));
            } catch(NumberFormatException | NullPointerException e) {
                Log.e(TAG, "Parsing of coefficients failed.\n" + e.getMessage());
            }
        }
        return Floats.toArray(coefficients);
    }

    @Nullable
    private static FilterSpec getFilterSpec(@NonNull String[] specTokens) {
        final String orderToken = "order";
        FilterSpec filterSpec = null;
        FilterType filterType = null;
        int order = 0;
        Map<String, Float> specMap = new HashMap<>();
        for (String s : specTokens) {
            try {
                String[] item = s.split("  *");
                if (item.length == 1) {
                    for (FilterType type : FilterType.values()) {
                        if (type.getType().equals(item[0].trim())) {
                            filterType = type;
                            break;
                        }
                    }
                } else if (item[0].equals(orderToken)) {
                    order = Integer.parseInt(item[1].trim());
                } else if (item.length == 2) {
                    specMap.put(item[0].trim(), Float.parseFloat(item[1].trim()));
                }
            } catch (Exception e) {
                Log.e(TAG, "Parsing of filter specifications failed.\n" + e.getMessage());
            }
        }

        if (filterType == null) {
            Log.e(TAG, "Unknown filter type.");
        } else {
            FilterSpec.Builder builder = new FilterSpec.Builder(filterType, order);
            for (Map.Entry<String, Float> entry : specMap.entrySet()) {
                switch (entry.getKey()) {
                    case Constants.FREQUENCY_PASS_1:
                        builder.frequencyPassBand1(entry.getValue());
                        break;
                    case Constants.FREQUENCY_PASS_2:
                        builder.frequencyPassBand2(entry.getValue());
                        break;
                    case Constants.FREQUENCY_STOP_1:
                        builder.frequencyStopBand1(entry.getValue());
                        break;
                    case Constants.FREQUENCY_STOP_2:
                        builder.frequencyStopBand2(entry.getValue());
                        break;
                    case Constants.AMOUNT_RIPPLE_PASS_1:
                        builder.amountRipplePassBand1(entry.getValue());
                        break;
                    case Constants.AMOUNT_RIPPLE_PASS_2:
                        builder.amountRipplePassBand2(entry.getValue());
                        break;
                    case Constants.ATTENUATION_STOP_1:
                        builder.attenuationStopBand1(entry.getValue());
                        break;
                    case Constants.ATTENUATION_STOP_2:
                        builder.attenuationStopBand2(entry.getValue());
                        break;
                    default:
                        break;
                }
            }
            filterSpec = builder.build();
        }
        return filterSpec;
    }

}
