package ch.zhaw.bait17.audio_signal_processing_toolbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;

/**
 * Decodes MPEG-1 Layer 3 audio files and loads the data in PCM format.
 *
 * Created by georgrem, stockan1 on 02.03.2017.
 */

public class MP3Decoder {

    private Bitstream bitstream;
    private Decoder mp3Decoder;

    public MP3Decoder() {

    }

    private byte[] decode(String path) {
        // Try with resource: auto-closes InputStream after try-catch block.
        try (InputStream inputStream = new FileInputStream(new File(path))) {
            bitstream = new Bitstream(inputStream);
            mp3Decoder = new Decoder();
            boolean done = false;
            while (!done) {
                Header header = bitstream.readFrame();
                if (header == null) {
                    done = true;
                } else {

                }
            }
        } catch (BitstreamException | IOException ex) {

        }
        return null;
    }

}
