package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * <p>
 *     A general utility class.
 * </p>
 *
 * @author georgrem, stockan1
 */

public class Util {

    /**
     * Returns an InputStream of the resource specified in the parameter.
     *
     * @param uri                       the resource you want to read from
     * @return                          an InputStream
     * @throws FileNotFoundException    Throws an exception if the file cannot be found
     */
    @Nullable
    public static InputStream getInputStreamFromURI(@NonNull String uri)
            throws FileNotFoundException {
        return ApplicationContext.getAppContext().getContentResolver().openInputStream(Uri.parse(uri));
    }

    /**
     * Returns a ByteArrayInputStream containing the passed byte array as its internal buffer.
     *
     * @param data  a byte array
     * @return      InputStream
     */
    public static InputStream getInputStreamFromByteArray(@NonNull byte[] data) {
        return new ByteArrayInputStream(data);
    }

    /**
     * Computes and returns the greatest common divisor (gcd) of two 32-bit integers.
     *
     * @param n     first integer
     * @param m     second integer
     * @return      gcd of n and m
     */
    public static int gcd(int n, int m) {
        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        } else if (n < m) {
            return gcd(m % n, n);
        } else {
            return gcd(n % m, m);
        }
    }

    /**
     * Computes the font size based on the text appearance.
     *
     * @param textAppearance    a text appearance value
     * @return                  font size expressed as a {@code float} value
     */
    public static float getFontSize(int textAppearance) {
        TypedValue typedValue = new TypedValue();
        ApplicationContext.getAppContext().getTheme().resolveAttribute(
                textAppearance, typedValue, true);
        int[] textSizeAttr = new int[]{android.R.attr.textSize};
        TypedArray arr = ApplicationContext.getAppContext().obtainStyledAttributes(
                typedValue.data, textSizeAttr);
        float fontSize = arr.getDimensionPixelSize(0, -1);
        arr.recycle();
        return fontSize;
    }
}
