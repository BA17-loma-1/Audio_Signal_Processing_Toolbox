package ch.zhaw.bait17.audio_signal_processing_toolbox;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * * Created by georgrem, stockan1 on 16.02.2017.
 *
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
 * Credits: http://mindtherobot.com/blog/580/android-audio-play-a-wav-file-on-an-audiotrack/
 */
public class WaveDecoder {

    private static final int RIFF_HEADER = 0x46464952;          // "RIFF"   (little endian)
    private static final int WAVE_HEADER = 0x45564157;          // "WAVE"
    private static final int DATA_HEADER = 0x61746164;          // "data"
    private static final int MAX_HEADER_SIZE = 4096;
    private static final int LINEAR_PCM_ENCODING = AudioCodingFormat.LINEAR_PCM.getValue();
    private static final int MIN_SUPPORTED_SAMPLE_RATE = 8000;
    private static final int MAX_SUPPORTED_SAMPLE_RATE = 96000;
    private static final int MAX_BITS_PER_SAMPLE = 16;
    private int dataOffset = 0;
    private WaveHeaderInfo header;
    private float[] pcmData = new float[0];

    /**
     * @param input The InputStream to read from.
     * @throws DecoderException
     */
    public WaveDecoder(InputStream input) throws DecoderException {
        if (input == null) {
            throw new DecoderException("InputStream is null.");
        }
        readStream(input);
    }

    /**
     * Allocates a non-direct ByteBuffer in little endian byte order.
     * Remember, little endian saves the least significant byte (LSB) at the lowest address.
     * Closes the underlying InputStream. Consecutive calls to this method will result in a DecoderException
     * @param input The InputStream of the wave file.
     * @throws DecoderException
     */
    private void readStream(InputStream input) throws DecoderException {
        // The wave file header should not exceed 4KB.
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
                    buffer.position(buffer.position() + (subChunkSize / 8));
                }
                int dataSize = buffer.getInt();
                // Important: set the dataOffset marker
                dataOffset = buffer.position();
                if (dataSize == 0) {
                    throw new DecoderException("Could not determine audio data size.");
                }

                header = new WaveHeaderInfo(encodingFormat, channels, sampleRate, bitsPerSample, dataSize);

                // Don't close the stream just yet...

                if (dataOffset > 0) {
                    // float size = byte size / 4 + 1
                    pcmData = new float[(header.getDataSize()/4) + 1];
                    // Skip over the header
                    waveStream.reset();

                    try {
                        if (waveStream.skip(dataOffset) == dataOffset) {
                            float data = 0;
                            int i = 0;
                            while (waveStream.available() > 0) {
                                // Read the next four bytes of data and convert it to float
                                pcmData[i++] = waveStream.readFloat();
                            }
                        }
                    } catch (EOFException e) { }
                }
                waveStream.close();
            }
        } catch (IOException ex) {
            throw new DecoderException("Cannot read from stream.");
        }
    }

    /**
     * Returns the raw audio data in PCM format.
     * In case of a decoder problem, this method will return an empty float array.
     * @return A float array containing the PCM audio data.
     * @throws IOException
     */
    public float[] getRawPCM() throws IOException {
        float[] pcm = new float[pcmData.length];
        System.arraycopy(pcmData, 0, pcm, 0, pcmData.length);
        return pcm;
    }

    /**
     * Returns the WAV file header information.
     * @return
     */
    public WaveHeaderInfo getHeader() {
        return header;
    }

}
