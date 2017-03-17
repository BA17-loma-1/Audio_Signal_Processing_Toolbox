package ch.zhaw.bait17.audio_signal_processing_toolbox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utility class.
 * @author georgrem, stockan1
 */

public class PCMUtil {

    public static final int BIAS_8_BIT = 128;
    public static final int BIAS_16_BIT = 32768;

    /**
     * <p><Converts a short (2 bytes) of sample data to a 16-bit float value.
     * The float value is guaranteed to lie in the range [-1,1].</p>
     *
     * <p>16-bit PCM samples are stored as 2's-complement signed integers, ranging from -32768 to 32767.</p>
     * @param sample
     * @return The 16-bit float value of the input sample
     */
    public static float shortByte2Float(short sample) {
        float f = ((float) sample) / (float) BIAS_16_BIT;
        if (f > 1) f = 1;
        if (f < -1) f = -1;
        return f;
    }

    /**
     *
     * @param samples
     * @return
     */
    public static float[] short2FloatArray(short[] samples) {
        float[] output = new float[samples.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = shortByte2Float(samples[i]);
        }
        return output;
    }

     /**
     * <p>Converts a byte of sample data to a 16-bit float value.
     * The float value is guaranteed to lie in the range [-1,1].</p>
     *
     * <p>!!! Caution !!! <br>
     * For some reason, WAV files don't support signed 8-bit format, so when reading and writing
     * WAV files, be aware that 8-bits means unsigned, ranging from 0 to 255.</p>
     * @param sample
     * @return The 16-bit float value of the input sample
     */
    public static float byte2Float(byte sample) {
        float f = ((float) (sample - BIAS_8_BIT)) / (float) BIAS_8_BIT;
        if (f > 1) f = 1;
        if (f < -1) f = -1;
        return f;
    }

    /**
     *
     * @param samples
     * @return
     */
    public static float[] byte2FloatArray(byte[] samples) {
        float[] output = new float[samples.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = byte2Float(samples[i]);
        }
        return output;
    }

    /**
     * <p>Converts a float array into a 8-bit PCM byte array.</p>
     * @param samples An array of floats
     * @return An array of bytes
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
     * <p>Converts a float array into a 16-bit PCM byte array.
     * The byte order is little endian.</p>
     * @param samples An array of floats
     * @return An array of bytes
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
