package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by georgrem, stockan1 on 11.03.2017.
 */

import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;

public class PlayerPresenter {

    private static final String TAG = PlayerPresenter.class.getSimpleName();
    private final Context context;
    private final PlaybackListener listener;

    private AudioPlayer player;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Bound service connected");
            player = ((PlayerService.PlayerBinder) service).getService();
            player.init(context, listener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Problem: bound service disconnected");
            player = null;
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

        if (player == null) return;

        String currentTrackUri = player.getCurrentTrack();

        if (currentTrackUri == null || !currentTrackUri.equals(uri)) {
            if (player.isPlaying()) {
                player.pause();
                player.stop();
            }
            player.play(uri);
        } else if (player.isPlaying()) {
            player.pause();
            player.stop();
        } else {
            player.play(uri);
        }
    }

    public int getSampleRate() {
        return player.getSampleRate();
    }

    public int getChannels() {
        return player.getChannels();
    }

    public void seekToPosition(int msec) {
        player.seekToPosition(msec);
    }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

}
