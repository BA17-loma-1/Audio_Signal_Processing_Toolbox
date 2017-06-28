package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.Field;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.fft.WindowType;

/**
 * <p>
 *     Convenient helper class that provides application context. <br>
 *     Additionally, it provides easy access to user preferences to other parts of the application.
 * </p>
 *
 * @author georgrem, stockan1
 */

public final class ApplicationContext extends Application {

    private static final String TAG = ApplicationContext.class.getSimpleName();

    private static Context context;
    private static SharedPreferences prefs;

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext.context = getApplicationContext();
        ApplicationContext.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Returns the application context.
     *
     * @return context
     */
    public static Context getAppContext() {
        return ApplicationContext.context;
    }

    /**
     *
     * @return  the preferred fft resolution saved in the application preferences
     */
    public static int getPreferredFFTResolution() {
        final String key = "pref_fft_resolution";
        return Integer.parseInt(prefs.getString(key, String.valueOf(
                context.getString(R.string.pref_fft_resolution_default))));
    }

    /**
     *
     * @return  the preferred window type saved in the application preferences
     */
    public static WindowType getPreferredWindow() {
        final String key = "pref_window";
        String window = prefs.getString(key, context.getString(R.string.pref_window_type_default));
        WindowType windowType = WindowType.fromString(window);
        if (windowType == null) {
            windowType = Constants.DEFAULT_WINDOW;
        }
        return windowType;
    }

    /**
     *
     * @return  the preferred colormap saved in the application preferences
     */
    public static Colour[] getPreferredColormap() {
        final String key = "pref_colormap";
        String colormapName = prefs.getString(key, context.getString(R.string.pref_colormap_type_default));
        Colour[] colormap = HeatMap.RAINBOW;
        Field[] fields = HeatMap.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(colormapName)) {
                try {
                    colormap = (Colour[]) field.get(null);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return colormap;
    }

    /**
     *
     * @return  the preferred magnitude floor saved in the application preferences
     */
    public static int getPreferredDBFloor() {
        final String key = "pref_db_floor";
        String value = prefs.getString(key, context.getString(R.string.pref_magnitude_db_floor_default));
        return Integer.parseInt(value.split(" ")[0]);
    }
}
