package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import java.util.Map;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.SupportedAudioFormat;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;

/**
 * @author georgrem, stockan1
 */

public class PlayerPresenter {

    private static final String TAG = PlayerPresenter.class.getSimpleName();
    private final Context context;
    private final PlaybackListener listener;
    private Map<String, AudioPlayer> audioPlayers;
    private AudioPlayer currentAudioPlayer;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Bound service connected");
            audioPlayers = ((PlayerService.PlayerBinder) service).getServices();
            // TODO: Select the right player
            currentAudioPlayer = audioPlayers.get(SupportedAudioFormat.MP3.toString());
            currentAudioPlayer.init(context, listener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Problem: bound service disconnected");
            currentAudioPlayer = null;
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

        if (currentAudioPlayer == null) return;

        String currentTrackUri = currentAudioPlayer.getCurrentTrack();

        if (currentTrackUri == null || !currentTrackUri.equals(uri)) {
            if (currentAudioPlayer.isPlaying()) {
                currentAudioPlayer.pause();
                currentAudioPlayer.stop();
            }
            currentAudioPlayer.play(uri);
        } else if (currentAudioPlayer.isPlaying()) {
            currentAudioPlayer.pause();
            currentAudioPlayer.stop();
        } else {
            currentAudioPlayer.play(uri);
        }
    }

    public boolean isPlaying() {
        return currentAudioPlayer.isPlaying();
    }

    public int getSampleRate() {
        return currentAudioPlayer.getSampleRate();
    }

    public int getChannels() {
        return currentAudioPlayer.getChannels();
    }

    public void seekToPosition(int msec) {
        currentAudioPlayer.seekToPosition(msec);
    }

    public int getCurrentPosition() {
        return currentAudioPlayer.getCurrentPosition();
    }

}
