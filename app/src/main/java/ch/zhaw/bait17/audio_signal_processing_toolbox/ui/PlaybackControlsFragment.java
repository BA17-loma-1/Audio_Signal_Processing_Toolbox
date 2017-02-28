package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;

public class PlaybackControlsFragment extends Fragment {

    private static final String TAG = PlaybackControlsFragment.class.toString();

    private ImageButton playPause;
    private TextView title;
    private TextView artist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);

        playPause = (ImageButton) rootView.findViewById(R.id.play_pause);
        playPause.setEnabled(true);
        playPause.setOnClickListener(mButtonListener);

        title = (TextView) rootView.findViewById(R.id.song_title);
        artist = (TextView) rootView.findViewById(R.id.song_artist);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "fragment.onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "fragment.onStop");
    }

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    private void playMedia() {
    }

    private void pauseMedia() {
    }
}
