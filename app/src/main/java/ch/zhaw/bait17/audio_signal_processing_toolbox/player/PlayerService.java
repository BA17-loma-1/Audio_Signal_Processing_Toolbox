package ch.zhaw.bait17.audio_signal_processing_toolbox.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Map;

/**
 * @author georgrem, stockan1
 */

public class PlayerService extends Service {

    private static final String TAG = PlayerService.class.getSimpleName();

    private final IBinder binder = new PlayerBinder();
    private AudioPlayer audioPlayer = AudioPlayer.getInstance();


    public static Intent getIntent(Context context) {
        return new Intent(context, PlayerService.class);
    }

    public class PlayerBinder extends Binder {
        /**
         * Returns the singleton instance of the audio player that corresponds to the file type.
         *
         * @return
         */
        public AudioPlayer getService() {
            return audioPlayer;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.d(TAG, "Service bound");
        return binder;
    }

}
