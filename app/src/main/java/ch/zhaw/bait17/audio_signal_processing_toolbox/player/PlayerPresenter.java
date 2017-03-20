package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import ch.zhaw.bait17.audio_signal_processing_toolbox.model.SupportedAudioFormat;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Util;

/**
 * @author georgrem, stockan1
 */

public class PlayerPresenter {

    private static final String TAG = PlayerPresenter.class.getSimpleName();
    private final Context context;
    private final PlaybackListener listener;
    private AudioPlayer audioPlayer;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Bound service connected");
            audioPlayer = ((PlayerService.PlayerBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Problem: bound service disconnected");
            audioPlayer = null;
        }
    };

    public PlayerPresenter(Context context, PlaybackListener listener) {
        this.context = context;
        this.listener = listener;
        init();
    }

    private void init() {
        context.bindService(PlayerService.getIntent(context), serviceConnection, Activity.BIND_AUTO_CREATE);
    }

    public void destroy() {
        context.unbindService(serviceConnection);
    }

    public void resume() {
        context.stopService(PlayerService.getIntent(context));
    }

    public void pause() {
        context.startService(PlayerService.getIntent(context));
    }

    public void selectTrack(Track track) {
        String uri = track.getUri();
        play(uri);
//        if (audioPlayer == null) return;
//
//        String currentTrackUri = audioPlayer.getCurrentTrack();
//
//        if (currentTrackUri == null || !currentTrackUri.equals(uri)) {
//            if (audioPlayer.isPlaying()) {
//                audioPlayer.pause();
//                audioPlayer.stop();
//            }
//            audioPlayer.play(uri);
//        } else if (audioPlayer.isPlaying()) {
//            audioPlayer.pause();
//            audioPlayer.stop();
//        } else {
//            audioPlayer.play(uri);
//        }
    }

    private void play(String uri) {
        PCMSampleBlock sampleBlock;
        try (InputStream is = Util.getInputStreamFromURI(context, uri)) {
            AudioDecoder mp3Decoder = new MP3Decoder(context, is);
            audioPlayer.init(mp3Decoder.getSampleRate(), mp3Decoder.getSampleRate());
            boolean keepDecoding = true;
            do {
                sampleBlock = mp3Decoder.getNextSampleBlock();
                if (sampleBlock != null)
                    audioPlayer.enqueueSampleBlock(sampleBlock);
                else
                    keepDecoding = false;
            } while (!audioPlayer.isInputBufferFull() && keepDecoding);
            audioPlayer.start();

            while ((sampleBlock = mp3Decoder.getNextSampleBlock()) != null) {
                if (!audioPlayer.enqueueSampleBlock(sampleBlock))
                    Log.e(TAG, "One pcm sample block lost");
            }
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    public boolean isPlaying() {
        return audioPlayer.isPlaying();
    }

    public int getSampleRate() {
        return audioPlayer.getSampleRate();
    }

    public int getChannels() {
        return audioPlayer.getChannels();
    }

    public void seekToPosition(int msec) {
        //audioPlayer.seekToPosition(msec);
    }

//    public int getCurrentPosition() {
//        return audioPlayer.getCurrentPosition();
//    }

}
