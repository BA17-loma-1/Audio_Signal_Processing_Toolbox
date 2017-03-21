package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.app.Application;
import android.content.Context;

/**
 * <p>
 *     Convenient helper class that provides application context.
 * </p>
 * @author georgrem, stockan1
 */

public class ApplicationContext extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext.context = getApplicationContext();
    }

    /**
     * Return the application context.
     * @return
     */
    public static Context getAppContext() {
        return ApplicationContext.context;
    }

}
