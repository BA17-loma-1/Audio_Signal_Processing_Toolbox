package ch.zhaw.bait17.audio_signal_processing_toolbox.decoder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.common.io.LittleEndianDataInputStream;

import java.io.IOException;
import java.io.InputStream;

import ch.zhaw.bait17.audio_signal_processing_toolbox.util.ApplicationContext;

/**
 * Implementation of a WAVE decoder. </br>
 * <p>
 * This class is designed to handle uncompressed PCM audio files, the most common type of RIFF files.
 * The header is the beginning of a WAV (RIFF - Resource Interchange File Format) file.
 * The header is used to provide specifications on the file type, sample rate, sample size and
 * bit size of the file, as well as its overall length.
 * </p>
 * <p>
 * The header of a WAV (RIFF) file is in general 44 bytes long and has the following format
 * for a 16-bit stereo source:
 * </p>
 * <p>
 *     <table>
 *         <tr>
 *            <th>Positions</th>
 *            <th>Sample Value</th>
 *            <th>Description</th>
 *         </tr>
 *         <tr>
 *              <td>0 - 3</td>
 *              <td>"RIFF"</td>
 *              <td>Marks the file as a riff file. Characters are each 1 byte long.</td>
 *         </tr>
 *         <tr>
 *             <td>4 - 7</td>
 *             <td>File size (integer)</td>
 *             <td>Size of the overall file - 8 bytes, in bytes (32-bit integer).</td>
 *         </tr>
 *         <tr>
 *             <td>8 -11</td>
 *             <td>"WAVE"</td>
 *             <td>File Type Header. For our purposes, it always equals "WAVE".</td>
 *         </tr>
 *         <tr>
 *             <td>12-15</td>
 *             <td>"fmt "</td>
 *             <td>Format chunk marker. Includes trailing null</td>
 *         </tr>
 *         <tr>
 *             <td>16-19</td>
 *             <td>16</td>
 *             <td>Length of format data as listed above</td>
 *         </tr>
 *         <tr>
 *             <td>20-21</td>
 *             <td>1</td>
 *             <td>Type of format (1 is PCM) - 2 byte integer</td>
 *         </tr>
 *         <tr>
 *             <td>22-23</td>
 *             <td>2</td>
 *             <td>Number of Channels - 2 byte integer</td>
 *         </tr>
 *         <tr>
 *             <td>24-27</td>
 *             <td>44100</td>
 *             <td>Sample Rate - 32 byte integer. Number of Samples per second [Hz]</td>
 *         </tr>
 *         <tr>
 *             <td>28-31</td>
 *             <td>176400</td>
 *             <td>(Sample Rate * BitsPerSample * Channels) / 8</td>
 *         </tr>
 *         <tr>
 *             <td>32-33</td>
 *             <td>4</td>
 *             <td>BitsPerSample * Channels) / 8.1 - 8 bit mono2 - 8 bit stereo/16 bit mono4 - 16 bit stereo</td>
 *         </tr>
 *         <tr>
 *             <td>34-35</td>
 *             <td>16</td>
 *             <td>Bits per sample</td>
 *         </tr>
 *         <tr>
 *             <td>36-39</td>
 *             <td>"data"</td>
 *             <td>"data" chunk header. Marks the beginning of the data section.</td>
 *         </tr>
 *         <tr>
 *             <td>40-43</td>
 *             <td>File size (data)</td>
 *             <td>Size of the data section.</td>
 *         </tr>
 *     </table>
 * </p>
 * <p>
 * Source: <a href="http://www.topherlee.com/software/pcm-tut-wavformat.html">www.topherlee.com</a></br>
 * See also <a href="https://github.com/BA17-loma-1/Audio_Signal_Processing_Toolbox/wiki/Technical-documentation#wave-file-header-format-specification">Project hosted by GitHub</a>
 * </p>
 * <p>
 * The raw PCM bytes are converted to floating point numbers in the range [-1,1]. </br>
 * 16 bit audio is usually signed, therefore the range of 16 bit integers is -32768 to 32767. </br>
 * Source: <a href="http://stackoverflow.com/questions/15087668/how-to-convert-pcm-samples-in-byte-array-as-floating-point-numbers-in-the-range#15094612">stackoverflow.com</a>
 * </p>
 *
 * @author georgrem, stockan1
 */
public class WaveDecoder implements AudioDecoder {

    private static final String TAG = WaveDecoder.class.getSimpleName();
    private static final int RIFF_HEADER = 0x46464952;          // "RIFF"   (little endian)
    private static final int WAVE_HEADER = 0x45564157;          // "WAVE"
    private static final int FORMAT_CHUNK_MARKER = 0x20746d66;  // "fmt "
    private static final int DATA_HEADER = 0x61746164;          // "data"
    private static final int WAVE_HEADER_SIZE = 44;
    private static final int LINEAR_PCM_ENCODING = AudioCodingFormat.LINEAR_PCM.getValue();
    private static final int MIN_SUPPORTED_SAMPLE_RATE = 8000;
    private static final int MAX_SUPPORTED_SAMPLE_RATE = 48000;
    private static final int ENCODING_16_BITS = 16;
    private static final int PCM_SAMPLE_BLOCK_SIZE = 2048;
    private static final WaveDecoder INSTANCE = new WaveDecoder();

    private LittleEndianDataInputStream waveStream;
    private WaveHeaderInfo header;
    private int dataOffset = 0;                 // The actual WAVE header size in bytes
    private int totalBytesRead = 0;             // PCM data bytes

    /**
     * This decoder only supports RIFF WAV files with {@link #ENCODING_16_BITS} bits encoding.
     */
    private WaveDecoder() {

    }

    /**
     * Returns the singleton instance of the WAVE decoder.
     *
     * @return  the {@code WaveDecoder} instance
     */
    public static WaveDecoder getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the audio source.
     *
     * @param inputStream   the {@code InputStream} to read from
     */
    @Override
    public void setSource(@NonNull InputStream inputStream) {
        if (waveStream != null) {
            try {
                // Close existing DataInputStream before reading from new InputStream.
                waveStream.close();
                Log.d(TAG, "WaveStream closed.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to close InputStream.");
            }
        }
        waveStream = new LittleEndianDataInputStream(inputStream);
        extractHeader();
    }

    /**
     * Returns a buffer of PCM samples of size {@link #PCM_SAMPLE_BLOCK_SIZE}.
     * or {@code null} if the end of the stream has been reached.
     *
     * @return  a {@code short} array containing PCM audio samples
     */
    @Override
    @Nullable
    public short[] getNextSampleBlock() {
        try {
            short[] pcm = new short[PCM_SAMPLE_BLOCK_SIZE];
            for (int i = 0; i < pcm.length; i++) {
                pcm[i] = waveStream.readShort();
                totalBytesRead += 2;
            }
            return pcm;
        } catch (IOException ex) {
            // End of file reached or I/O error.
            // We close the InputStream and return null to indicate EOF.
            try {
                waveStream.close();
                Log.d(TAG, "WaveStream closed.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to close WAWE InputStream.\");");
            }
        }
        return null;
    }

    @Override
    public int getSampleRate() {
        return header != null ? header.getSampleRate() : 0;
    }

    @Override
    public int getChannels() {
        return header != null ? header.getChannels() : 0;
    }

    @Override
    public boolean isInitialised() {
        return waveStream != null && header != null &&
                header.getSampleRate() != 0 && header.getChannels() != 0;
    }

    /**
     * Returns the WAV file header.
     *
     * @return  WAVE header
     */
    public WaveHeaderInfo getHeader() {
        return header;
    }

    /**
     * <p>
     *     Extracts the header from the WAVE file.
     * </p>
     */
    private void extractHeader() {
        try {
            // Check if file container format is RIFF (Resource Interchange File Format) and WAVE.
            int riffHeader = waveStream.readInt();
            if (riffHeader != RIFF_HEADER) {
                throw new DecoderException("Not a RIFF file.");
            }
            int fileSize = waveStream.readInt();

            // Check if file type is WAVE.
            int waveHeader = waveStream.readInt();
            if (waveHeader != WAVE_HEADER) {
                throw new DecoderException("Unknown file type - not WAVE.");
            }

            // Check the format chunk marker.
            int formatChunkMarker = waveStream.readInt();
            if (formatChunkMarker != FORMAT_CHUNK_MARKER) {
                throw new DecoderException("Illegal format chunk marker.");
            }

            // Size of the above data - should be 16 bytes.
            int fileFormatHeaderLength = waveStream.readInt();

            // Read two bytes at position 20 and check if the format is linear PCM encoding.
            short encodingFormat = waveStream.readShort();
            if (encodingFormat != LINEAR_PCM_ENCODING) {
                throw new DecoderException("Unsupported encoding");
            }

            // Read two bytes at position 22 and check for the number of channels.
            short channels = waveStream.readShort();
            if (channels < 1 || channels > 2) {
                throw new DecoderException("Unsupported number of channels: " + channels);
            }

            // Read four bytes at position 24 and check if the sample rate is supported.
            int sampleRate = waveStream.readInt();
            if (sampleRate < MIN_SUPPORTED_SAMPLE_RATE || sampleRate > MAX_SUPPORTED_SAMPLE_RATE) {
                throw new DecoderException("Unsupported sample rate: " + sampleRate);
            }

            /*
                Read four bytes at position 28.
                Bytes per second is the speed of the data stream:
                Sample Rate * BitsPerSample * Channels) / 8
             */
            int bytesPerSecond = waveStream.readInt();

            /*
                Read two bytes at position 32 to get the block alignment
                (the number of bytes for one sample including all channels.)
             */
            short blockAlignment = waveStream.readShort();

            // Read two bytes at position 34 and check if the quantisation bits per sample
            // is supported.
            short bitsPerSample = waveStream.readShort();
            if (bitsPerSample != ENCODING_16_BITS) {
                throw new DecoderException("Unsupported number of bits per sample: " + bitsPerSample);
            }

            /*
                In general the header size is 44 bytes. There may be additional subchunks in the
                Wave data stream. If so, each will have a 4 bytes (char) SubChunkID,
                4 bytes of SubChunkSize and SubChunkSize amount of data. The rest is audio data.
                "data" marker should follow at position 36 - skip over any padding/junk data.
             */
            int junkData = 0;
            int subChunkID = 0;
            while ((subChunkID = waveStream.readInt()) != DATA_HEADER) {
                int subChunkSize = waveStream.readInt();
                junkData += subChunkSize;
                waveStream.skipBytes(subChunkSize);
            }
            int dataSize = waveStream.readInt();
            dataOffset = WAVE_HEADER_SIZE + junkData;
            header = new WaveHeaderInfo(encodingFormat, channels, sampleRate, bitsPerSample, dataSize);
        } catch (IOException | DecoderException ex) {
            Log.e(TAG, "Exception while extracting WAV header. " + ex.getMessage());
            Toast.makeText(ApplicationContext.getAppContext(), "Cannot read from WAVE file.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
