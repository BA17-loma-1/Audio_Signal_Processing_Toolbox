package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A General utility class.
 * @author georgrem, stockan1
 */

public class Util {

    /**
     * Returns an InputStream of the resource specified in the parameter.
     * @param context The application context.
     * @param uri The resource you want to read from.
     * @return An InputStream.
     * @throws FileNotFoundException Throws an exception if the file cannot be found.
     */
    public static InputStream getInputStreamFromURI(@NonNull Context context, @NonNull String uri)
            throws FileNotFoundException {
        return context.getContentResolver().openInputStream(Uri.parse(uri));
    }

    /**
     * Returns a ByteArrayInputStream containing the passed byte array as its internal buffer.
     * @param data A byte array.
     * @return InputStream
     */
    public static InputStream getInputStreamFromByteArray(@NonNull byte[] data) {
        return new ByteArrayInputStream(data);
    }

}
