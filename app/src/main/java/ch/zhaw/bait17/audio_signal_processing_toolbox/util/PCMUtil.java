package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <h3>PCM Utility class</h3>
 * <p>
 *     <b>Important:</b> </br>
 *     These conversions will introduce distortion. The errors when converting from one data type
 *     to another are correlated with the input signal sample. For absolute pristine conversion
 *     dithering is required.
 *     See <a href="http://stackoverflow.com/questions/15087668/how-to-convert-pcm-samples-in-byte-array-as-floating-point-numbers-in-the-range#15094612">stackoverflow.com</a>
 * </p>
 *
 * @author georgrem, stockan1
 */
public class PCMUtil {

    private static final String TAG = PCMUtil.class.getSimpleName();
    public static final int BIAS_8_BIT = 128;
    public static final float BIAS_16_BIT = 32768.0f;

    /**
     * <p>
     *     Converts a short (2 bytes) of sample data to a 32-bit float value.
     *     The float value is guaranteed to lie in the range [-1,1].
     * </p>
     * <p>
     *     16-bit PCM samples are stored as 2's-complement signed integers, ranging from -32768 to 32767.
     * </p>
     *
     * @param sample    a 16-bit {@code short}
     * @return          a 32-bit {@code float}
     */
    public static float shortByte2Float(short sample) {
        //float f = ((float) sample) / (float) BIAS_16_BIT;
        float f = ((float) sample) * (1.0f / BIAS_16_BIT);
        if (f > 1) {
            Log.d(TAG, "Hard clipping float > 1");
        } else if (f < -1) {
            Log.d(TAG, "Hard clipping float < -1");
        }
        return f;
    }

    /**
     * <p>
     *     Converts an array of {@code short} (signed, 16 bit) into an array
     *     of signed 32-bit {@code float}.
     *     The float values are normalised and guaranteed to lie in the range [-1,1].
     *     16-bit PCM samples are stored as 2's-complement signed integers,
     *     ranging from -32768 to 32767.
     * </p>
     *
     * @param samples   an array of {@code short}
     * @return          an array of {@code float}
     */
    public static float[] short2FloatArray(short[] samples) {
        float[] output = new float[samples.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = ((float) samples[i]) * (1.0f / BIAS_16_BIT);
        }
        return output;
    }

    /**
     * <p>
     *     Converts a {@code short} (signed, 16 bit) into a signed 32-bit {@code float}.
     *     The float value is normalised and guaranteed to lie in the range [-1,1].
     *     16-bit PCM samples are stored as 2's-complement signed integers,
     *     ranging from -32768 to 32767.
     * </p>
     *
     * @param sample   a {@code short}
     * @return         a {@code float}
     */
    public static float short2Float(short sample) {
        return (float) sample * (1.0f / BIAS_16_BIT);
    }

    /**
     * <p>
     *     Converts a float value into a 16-bit short value.
     *     The float value must lie in the range [-1,1].
     * </p>
     *
     * @param sample    a 32-bit {@code float}
     * @return          a 16-bit {@code short}
     */
    public static short float2Short(float sample) {
        float f = sample * BIAS_16_BIT;
        if (f > BIAS_16_BIT - 1) {
            f = BIAS_16_BIT - 1;
            //Log.d(TAG, "Hard clipping short > " + (BIAS_16_BIT - 1));
        } else if (f < -BIAS_16_BIT) {
            f = -BIAS_16_BIT;
            //Log.d(TAG, "Hard clipping short < -" + BIAS_16_BIT);
        }
        return (short) f;
    }

    /**
     * <p>
     *     Converts an array of {@code float} into an array of signed 16-bit {@code short}.
     * </p>
     *
     * @param samples   an array of {@code float}
     * @return          an array of {@code short}
     */
    public static short[] float2ShortArray(float[] samples) {
        short[] output = new short[samples.length];
        for (int i = 0; i < output.length; i++) {
            double out =  samples[i] * BIAS_16_BIT;
            if (out < Short.MIN_VALUE) {
                out = Short.MIN_VALUE;
            } else if (out > Short.MAX_VALUE) {
                out = Short.MAX_VALUE;
            }
            output[i] = (short) out;
        }
        return output;
    }

     /**
     * <p>
     *      Converts a byte of sample data to a 32-bit float value.
     *      The float value is guaranteed to lie in the range [-1,1].
     * </p>
     * <p>
     *     !!! Caution !!! <br>
     *     For some reason, WAV files encode 8-bit values as a unsigned bytes.
      *    Therefore, when reading and writing WAV files, be aware that 8-bit ranges from 0 to 255.
     * </p>
      *
     * @param sample    a 8-bit {@code byte}
     * @return          a 32-bit {@code float}
     */
    public static float byte2Float(byte sample) {
        float f = ((float) (sample - BIAS_8_BIT)) / (float) BIAS_8_BIT;
        if (f > 1) {
            f = 1;
            //Log.d(TAG, "Hard clipping float > 1");
        } else if (f < -1) {
            f = -1;
            //Log.d(TAG, "Hard clipping float < -1");
        }
        return f;
    }

    /**
     * <p>
     *     Converts an array of bytes into an array of floats.
     *     The float values are guaranteed to lie in the range [-1,1].
     * </p>
     *
     * @param samples   an array of {@code byte}
     * @return          an array of {@code float}
     */
    public static float[] byte2FloatArray(byte[] samples) {
        float[] output = new float[samples.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = byte2Float(samples[i]);
        }
        return output;
    }

    /**
     * <p>
     *     Converts a array of floats into an array of PCM bytes.
     *     The byte order is little endian.
     * </p>
     *
     * @param samples   an array of {@code float}
     * @return          an array of {@code byte}
     */
    public static byte[] float8Bit2ByteArray(float[] samples) {
        ByteBuffer buffer = ByteBuffer.allocate(samples.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        float f = 0;
        for (int i = 0; i < samples.length; i++) {
            f = samples[i] * BIAS_8_BIT;
            f += BIAS_8_BIT;
            if (f < 0) f = 0;
            if (f > (2 * BIAS_8_BIT) - 1) f = (2 * BIAS_8_BIT - 1);
            buffer.put(i, (byte) f).array();
        }
        return buffer.array();
    }

    /**
     * <p>
     *     Converts an array of floats into an array of PCM bytes.
     *     The byte order is little endian.
     * </p>
     *
     * @param samples   an array of {@code float}
     * @return          an array of {@code byte}
     */
    public static byte[] float16Bit2ByteArray(float[] samples) {
        ByteBuffer buffer = ByteBuffer.allocate(samples.length * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        float f = 0;
        for (int i = 0; i < samples.length; i++) {
            f = samples[i] * BIAS_16_BIT;
            if (f < -BIAS_16_BIT) f = -BIAS_16_BIT;
            if (f > BIAS_16_BIT - 1) f = (BIAS_16_BIT - 1);
            buffer.putShort(i, (short) f).array();
        }
        return buffer.array();
    }

}
