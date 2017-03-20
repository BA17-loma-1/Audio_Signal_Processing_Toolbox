package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.InputStream;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public class MP3Decoder {

    private static final String TAG = MP3Decoder.class.getSimpleName();

    private Context context;
    private InputStream is;
    private Bitstream bitstream;
    private Decoder decoder;
    private int frameIndex;
    private int position;
    private int shortSamplesRead;
    private int sampleRate;
    private int channels;

    public MP3Decoder(Context context, InputStream is) {
        this.context = context;
        this.is = is;
        init();
    }

    public PCMSampleBlock getNextSampleBlock() {
        try {
            Header currentFrameHeader = bitstream.readFrame();
            if (currentFrameHeader != null) {
                frameIndex++;
                position += currentFrameHeader.ms_per_frame();
                Log.d(TAG, String.format("Position %d:", position));
                SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(currentFrameHeader, bitstream);
                PCMSampleBlock sampleBlock = new PCMSampleBlock(
                        samples.getBuffer(), samples.getSampleFrequency());
                shortSamplesRead += sampleBlock.getSamples().length;
                return sampleBlock;
            }
            bitstream.closeFrame();
        } catch (BitstreamException | DecoderException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return null;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    private void init() {
        bitstream = new Bitstream(is);
        decoder = new Decoder();
        extractFrameHeaderInfo(bitstream);
        shortSamplesRead = 0;
        position = 0;
        frameIndex = 0;
    }

    private void extractFrameHeaderInfo(Bitstream bitstream) {
        try {
            Header frameHeader = bitstream.readFrame();
            SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
            sampleRate = samples.getSampleFrequency();
            channels = samples.getChannelCount();
            bitstream.closeFrame();
            bitstream.unreadFrame();
        } catch(BitstreamException | DecoderException ex) {
            Toast.makeText(context, "Failed to extract frame header data.\n " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
