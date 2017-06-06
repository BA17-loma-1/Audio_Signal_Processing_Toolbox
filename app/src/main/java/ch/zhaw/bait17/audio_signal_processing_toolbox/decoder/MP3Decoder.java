package ch.zhaw.bait17.audio_signal_processing_toolbox.decoder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;

import ch.zhaw.bait17.audio_signal_processing_toolbox.util.ApplicationContext;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * <p>
 *     Implementation of a MP3 decoder based on Java Zoom JLayer.
 *     See <a href="http://www.javazoom.net/javalayer/javalayer.html">JLayer website</a> </br>
 *     PCM sample blocks can be read one by one with {@link #getNextSampleBlock()}.
 * </p>
 *
 * @author georgrem, stockan1
 */

public class MP3Decoder implements AudioDecoder {

    private static final String TAG = MP3Decoder.class.getSimpleName();
    private static final MP3Decoder INSTANCE = new MP3Decoder();
    private static Decoder decoder;

    private static Bitstream bitstream;
    private InputStream is;
    private static int sampleRate;
    private static int channels;
    private int shortSamplesRead;
    private int position;

    private MP3Decoder() {

    }

    /**
     * Returns the singleton instance of the {@code MP3Decoder}.
     *
     * @return the {@code MP3Decoder} instance
     */
    public static MP3Decoder getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the audio source.
     *
     * @param inputStream   the {@code InputStream} to read from
     */
    @Override
    public void setSource(@NonNull InputStream inputStream) {
        if (bitstream != null) {
            try {
                // Close existing InputStream before reading from new InputStream.
                bitstream.close();
                Log.d(TAG, "MP3 BitStream closed.");
            } catch (BitstreamException e) {
                Log.e(TAG, "Failed to close BitStream.");
            }
        }
        is = inputStream;
        bitstream = new Bitstream(is);
        decoder = new Decoder();
        init();
    }

    @Override
    @Nullable
    public short[] getNextSampleBlock() {
        short[] sampleBlock = null;
        try {
            Header currentFrameHeader = bitstream.readFrame();
            if (currentFrameHeader != null) {
                position += currentFrameHeader.ms_per_frame();
                SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(currentFrameHeader, bitstream);
                sampleBlock = samples.getBuffer();
                shortSamplesRead += sampleBlock.length;
            } else {
                // EOF reached - close the BitStream
                bitstream.close();
                Log.d(TAG, "MP3 BitStream closed.");
                return null;
            }
            bitstream.closeFrame();
        } catch (Exception ex) {
            /*
                ArrayIndexOutOfBoundsException: weird error happens sometimes with MP3Decoder
             */
            Log.e(TAG, ex.getMessage(), ex);
            sampleBlock = null;
        }
        return sampleBlock;
    }

    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    @Override
    public int getChannels() {
        return channels;
    }

    @Override
    public boolean isInitialised() {
        return is != null && decoder != null && bitstream != null
                && sampleRate != 0 && channels != 0;
    }

    private void init() {
        extractFrameHeaderInfo(bitstream);
        shortSamplesRead = 0;
        position = 0;
    }

    private void extractFrameHeaderInfo(@NonNull Bitstream bitstream) {
        try {
            Header frameHeader = bitstream.readFrame();
            SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
            if (samples != null) {
                sampleRate = samples.getSampleFrequency();
                channels = samples.getChannelCount();
            }
            bitstream.closeFrame();
            bitstream.unreadFrame();
        } catch(BitstreamException | DecoderException ex) {
            Toast.makeText(ApplicationContext.getAppContext(),
                    "Failed to extract frame header data.\n " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
