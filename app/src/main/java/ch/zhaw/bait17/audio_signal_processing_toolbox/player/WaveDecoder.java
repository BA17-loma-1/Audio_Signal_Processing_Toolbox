package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.common.primitives.Floats;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ch.zhaw.bait17.audio_signal_processing_toolbox.AudioCodingFormat;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveHeaderInfo;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.PCMUtil;

/**
 * Decodes a WAV file.
 * This class is designed to handle uncompressed PCM audio files, the most common type of RIFF files.
 * The header is the beginning of a WAV (RIFF - Resource Interchange File Format) file.
 * The header is used to provide specifications on the file type, sample rate, sample size and
 * bit size of the file, as well as its overall length.
 *
 * The header of a WAV (RIFF) file is in general 44 bytes long and has the following format
 * for a 16 bit stereo source:
 *
 * Positions    Sample Value        Description
 * -------------------------------------------------------------------------------------------------
 * 0 - 3        "RIFF"              Marks the file as a riff file. Characters are each 1 byte long.
 * 4 - 7        File size (integer) Size of the overall file - 8 bytes, in bytes (32-bit integer).
 * 8 -11        "WAVE"              File Type Header. For our purposes, it always equals "WAVE".
 * 12-15	    "fmt "              Format chunk marker. Includes trailing null
 * 16-19        16                  Length of format data as listed above
 * 20-21        1                   Type of format (1 is PCM) - 2 byte integer
 * 22-23        2                   Number of Channels - 2 byte integer
 * 24-27        44100               Sample Rate - 32 byte integer. Number of Samples per second [Hz].
 * 28-31        176400              (Sample Rate * BitsPerSample * Channels) / 8.
 * 32-33        4                   (BitsPerSample * Channels) / 8.1 - 8 bit mono2 - 8 bit stereo/16 bit mono4 - 16 bit stereo
 * 34-35        16                  Bits per sample
 * 36-39    	"data"	            "data" chunk header. Marks the beginning of the data section.
 * 40-43    	File size (data)	Size of the data section.
 *
 * Source: http://www.topherlee.com/software/pcm-tut-wavformat.html
 * @See https://github.com/BA17-loma-1/Audio_Signal_Processing_Toolbox/wiki/Technical-documentation#wave-file-header-format-specification
 *
 * The raw PCM bytes are converted to floating point numbers in the range [-1,1].
 * 16 bit audio is usually signed, therefore the range of 16 bit integers is -32768 to 32767.
 *
 * Source: http://stackoverflow.com/questions/15087668/how-to-convert-pcm-samples-in-byte-array-as-floating-point-numbers-in-the-range#15094612
 *
 * Credits: http://mindtherobot.com/blog/580/android-audio-play-a-wav-file-on-an-audiotrack/
 *
 * @author georgrem, stockan1
 */
public class WaveDecoder implements AudioDecoder {

    private static final String TAG = WaveDecoder.class.getSimpleName();
    private static final int RIFF_HEADER = 0x46464952;          // "RIFF"   (little endian)
    private static final int WAVE_HEADER = 0x45564157;          // "WAVE"
    private static final int DATA_HEADER = 0x61746164;          // "data"
    private static final int MAX_HEADER_SIZE = 8192;            // The wave file header should not exceed 8KB.
    private static final int LINEAR_PCM_ENCODING = AudioCodingFormat.LINEAR_PCM.getValue();
    private static final int MIN_SUPPORTED_SAMPLE_RATE = 8000;
    private static final int MAX_SUPPORTED_SAMPLE_RATE = 48000;
    private static final int MAX_BITS_PER_SAMPLE = 16;
    private static final int ENCODING_16_BITS = 16;
    private static final int ENCODING_8_BITS = 8;
    private static final int PCM_SAMPLES_BUFFER_SIZE = 32;
    private static final WaveDecoder INSTANCE = new WaveDecoder();

    private static InputStream is;
    private static int dataOffset = 0;
    private static int totalBytesRead = 0;
    private static WaveHeaderInfo header;
    private static byte[] pcmData = new byte[0];

    /**
     * This decoder only supports RIFF WAV files with encoding of either {@link #ENCODING_8_BITS}
     * or {@link #ENCODING_16_BITS} bits.
     */
    private WaveDecoder() {

    }

    /**
     * Returns the singleton instance of the WAVE decoder.
     */
    public static WaveDecoder getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the audio source.
     * @param inputStream The InputStream to read from.
     * @throws DecoderException Throws DecoderException if reading from stream fails.
     */
    public void setSource(@NonNull InputStream inputStream) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {

            }
        }
        is = inputStream;
        readHeader();
    }

    @Override
    public short[] getNextSampleBlock() {
        return null;
    }

    @Override
    public int getSampleRate() {
        return 0;
    }

    @Override
    public int getChannels() {
        return 0;
    }

    @Override
    public boolean isInitialised() {
        return false;
    }

    private static void readHeader() {

    }

    /**
     * Allocates a non-direct ByteBuffer in little endian byte order.
     * Remember, little endian saves the least significant byte (LSB) at the lowest address.
     * Closes the underlying InputStream. Consecutive calls to this method will result in a DecoderException
     * @param input The InputStream of the wave file.
     * @throws DecoderException Throws DecoderException if reading from stream fails.
     */
    private static void readStream(@NonNull InputStream input) throws DecoderException {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_HEADER_SIZE);
        buffer.limit(buffer.capacity());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        DataInputStream waveStream = new DataInputStream(input);
        try {
            if (input.read(buffer.array(), 0, buffer.capacity()) > 0) {
                buffer.rewind();
                // Check if file container format is RIFF (Resource Interchange File Format) and WAVE
                if (buffer.getInt(0) != RIFF_HEADER || buffer.getInt(8) != WAVE_HEADER) {
                    throw new DecoderException("Unknown file format.");
                }

                // Read two bytes at position 20 and check if the format is linear PCM encoding.
                short encodingFormat = buffer.getShort(20);
                if (encodingFormat != LINEAR_PCM_ENCODING) {
                    throw new DecoderException("Unsupported encoding");
                }

                // Read two bytes at position 22 and check for the number of channels.
                short channels = buffer.getShort(22);
                if (channels < 1 || channels > 2) {
                    throw new DecoderException("Unsupported number of channels.");
                }

                // Read four bytes and check if the sample rate is supported.
                int sampleRate = buffer.getInt(24);
                if (sampleRate < MIN_SUPPORTED_SAMPLE_RATE || sampleRate > MAX_SUPPORTED_SAMPLE_RATE) {
                    throw new DecoderException("Unsupported sample rate.");
                }

                // Read two at position 32 to get the block alignment (number of bytes per sample for all channels)
                int bytesPerSample = buffer.getInt(32);

                // Read two bytes and check if bits per sample is supported.
                int bitsPerSample = buffer.getShort(34);
                if (bitsPerSample <= 0 || bitsPerSample > MAX_BITS_PER_SAMPLE) {
                    throw new DecoderException("Unsupported number of bits per sample.");
                }

                /* In case there are additional subchunks of meta data, we need to skip over it.
                   In general, the "data" marker is at position 36. */
                buffer.position(36);
                int subChunkSize = 0;
                while (buffer.getInt() != DATA_HEADER && buffer.position() < buffer.limit()) {
                    // subChunkID corresponds NOT to "data" marker -> skip over
                    // Read four bytes of data to determine the subchunk size
                    subChunkSize = buffer.getInt();
                    buffer.position(buffer.position() + subChunkSize);
                }
                int dataSize = buffer.getInt();
                // Important: set the dataOffset marker
                dataOffset = buffer.position();
                if (dataSize == 0) {
                    throw new DecoderException("Could not determine audio data size.");
                }

                header = new WaveHeaderInfo(encodingFormat, channels, sampleRate, bitsPerSample,
                        bytesPerSample, dataSize);

                if (dataOffset > 0) {
                    pcmData = new byte[header.getDataSize()];
                    waveStream.reset();
                    // Skip over the header
                    if (waveStream.skip(dataOffset) == dataOffset) {
                        totalBytesRead = waveStream.read(pcmData, 0, pcmData.length);
                    }
                }
                waveStream.close();
            }
        } catch (IOException ex) {
            throw new DecoderException("Cannot read from stream.");
        }
    }

    /**
     * Returns the the complete raw audio data in PCM format.
     * Byte order is little endian.
     * @return A byte array containing the PCM audio samples.
     */
    public byte[] getRawPCM() {
        byte[] pcm = new byte[pcmData.length];
        System.arraycopy(pcmData, 0, pcm, 0, pcmData.length);
        return pcm;
    }

    /**
     * Returns a buffer of PCM samples of size {@link #PCM_SAMPLES_BUFFER_SIZE} KB.
     * Byte order is little endian.
     * This method returns null if the underlying InputStream does not support mark and reset.
     * @return A byte array containing the PCM audio samples.
     * @throws IOException Throws IOException if samples cannot be read from InputStream
     */
    public byte[] getNextRawPCM() throws IOException {
        byte[] pcm = new byte[PCM_SAMPLES_BUFFER_SIZE];
        int bytesRead = 0;
        if ((bytesRead = is.read(pcm, totalBytesRead, pcm.length)) >= 0) {
            totalBytesRead += bytesRead;
        } else {
            // Reached the end of the InputStream.
            is.close();
        }
        return pcm;
    }

    /**
     * Returns the the complete raw audio data in PCM format.
     * @return A short array containing the PCM audio samples.
     */
    public short[] getShort() {
        short[] buffer = new short[pcmData.length/2];
        ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(buffer);
        return buffer;
    }

    /**
     * Returns a buffer of PCM samples of {@link #PCM_SAMPLES_BUFFER_SIZE} KB.
     * Byte order is little endian.
     * This method returns null if the underlying InputStream does not support mark and reset.
     * @return A short array containing the PCM audio samples.
     * @throws IOException Throws IOException if samples cannot be read from InputStream
     */
    public short[] getNextShort() throws IOException {
        byte[] pcmBytes = new byte[PCM_SAMPLES_BUFFER_SIZE * 2];
        int bytesRead = 0;
        if ((bytesRead = is.read(pcmBytes, totalBytesRead, pcmBytes.length)) >= 0) {
            totalBytesRead += bytesRead;
        } else {
            // Reached the end of the InputStream.
            is.close();
        }
        ShortBuffer pcm = ByteBuffer.wrap(pcmBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        return pcm.array();
    }

    /**
     * Returns the the complete raw audio data in PCM format.
     * The float values lie in the range [-1,1].
     * @return A float array containing the PCM audio samples.
     */
    public float[] getFloat() {
        /*
          8-bit samples are stored as unsigned bytes, ranging from 0 to 255.
          16-bit samples are stored as 2's-complement signed integers, ranging from -32768 to 32767.
          If for some reason we are using >16 bit integers, we need additional bounding to make sure
          that the float values lie in the range [-1,1].

          !! Caution !!
          For some reason, WAV files don't support signed 8-bit format, so when reading and writing
          WAV files, be aware that 8-bits means unsigned.
          Source: http://blog.bjornroche.com/2013/05/the-abcs-of-pcm-uncompressed-digital.html

          Java primitive data types and their min/max values
          See: Oracle Java documentation: http://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
         */
        ByteBuffer buffer = ByteBuffer.allocate(pcmData.length).put(pcmData);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        List<Float> samples = new ArrayList<>();
        try {
            if (header.getBitsPerSample() == ENCODING_16_BITS) {
                // Read two bytes (short) of PCM data at a time
                while (true) {
                    samples.add(PCMUtil.shortByte2Float(buffer.getShort()));
                }
            } else if (header.getBitsPerSample() == ENCODING_8_BITS) {
                // Read one byte of PCM data at a time
                while (true) {
                    samples.add(PCMUtil.byte2Float(buffer.get()));
                }
            } else {
                throw new DecoderException("Unsupported encoding.");
            }
        } catch (DecoderException | BufferUnderflowException e) {
            Log.e(TAG, e.getMessage());
        }
        return Floats.toArray(Arrays.asList(samples.toArray(new Float[samples.size()])));
    }

    /**
     * Returns the WAV file header information.
     * @return
     */
    public WaveHeaderInfo getHeader() {
        return header;
    }

}
