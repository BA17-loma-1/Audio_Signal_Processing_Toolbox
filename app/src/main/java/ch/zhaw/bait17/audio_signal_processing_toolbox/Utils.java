package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.net.Uri;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Utility class.
 * @author georgrem, stockan1
 */

public class Utils {

    /**
     * Returns an InputStream of the resource specified in the parameter.
     * @param context The application context.
     * @param uri The resource you want to read from.
     * @return An InputStream.
     * @throws FileNotFoundException Throws an exception if the file cannot be found.
     */
    public static InputStream getInputStreamFromURI(Context context, String uri)
            throws FileNotFoundException {
        return context.getContentResolver().openInputStream(Uri.parse(uri));
    }

}
