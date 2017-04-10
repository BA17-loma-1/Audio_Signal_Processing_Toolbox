package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.player.AudioPlayer;
import ch.zhaw.bait17.audio_signal_processing_toolbox.player.PlaybackListener;

/**
 * @author georgrem, stockan1
 */

public class AudioPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = AudioPlayerFragment.class.getSimpleName();
    private static final String BUNDLE_ARGUMENT_FILTER = "filter_view";
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private final Handler seekHandler = new Handler();
    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };

    private List<Track> tracks;
    private Track currentTrack;
    private Track nextTrack;
    private int trackPosNr;
    private static AudioPlayer audioPlayer;
    private TextView currentTime;
    private TextView endTime;
    private SeekBar seekBar;
    private ImageButton playPauseButton;

    /*
        In certain cases, your fragment may want to accept certain arguments.
        A common pattern is to create a static newInstance method for creating a Fragment with arguments.
        This is because a Fragment must have only a constructor with no arguments.
        From: {@link https://guides.codepath.com/android/Creating-and-Using-Fragments#communicating-with-fragments}
     */
    public static AudioPlayerFragment newInstance(Filter filter) {
        AudioPlayerFragment fragment = new AudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_ARGUMENT_FILTER, filter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.audio_player, container, false);
        currentTime = (TextView) rootView.findViewById(R.id.currentTime);
        endTime = (TextView) rootView.findViewById(R.id.endTime);
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        playPauseButton = (ImageButton) rootView.findViewById(R.id.play_pause);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        audioPlayer = AudioPlayer.getInstance();
        audioPlayer.setOnPlaybackListener(new PlaybackListener() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onCompletion() {
                setPlayButtonOnUI();
            }
        });
        Bundle args = getArguments();
        if (args != null) {
            Filter filter = args.getParcelable(BUNDLE_ARGUMENT_FILTER);
            audioPlayer.setFilter(filter);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentTime.setText(DateUtils.formatElapsedTime(progress / 1000));
        //playerPresenter.seekToPosition(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        audioPlayer.seekToPosition(seekBar.getProgress());
    }

    public void setFilter(Filter filter) {
        audioPlayer.setFilter(filter);
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void setTrackPosNr(int trackPosNr) {
        this.trackPosNr = trackPosNr;
    }

    public void setTrack(@NonNull Track track) {
        if (currentTrack == null) {
            // First time a track is selected.
            currentTrack = track;
            nextTrack = currentTrack;
        } else {
            nextTrack = track;
        }
        audioPlayer.selectTrack(track);
        playPauseTrack();
    }

    public void playPauseTrack() {
        if (currentTrack != null && nextTrack != null) {
            if (currentTrack == nextTrack) {
                // No change in track selection.
                if (audioPlayer.isPlaying()) {
                    setPlayButtonOnUI();
                    audioPlayer.pausePlayback();
                } else if (audioPlayer.isPaused()) {
                    setPauseButtonOnUI();
                    audioPlayer.resumePlayback();
                } else {
                    setPauseButtonOnUI();
                    audioPlayer.play();
                }
            } else {
                // A new track has been selected.
                setPlayButtonOnUI();
                currentTrack = nextTrack;
                updateTrackPropertiesOnUI();
                audioPlayer.stopPlayback();
                while (!audioPlayer.isStopped()) {
                    try {
                        // Sleep until the playback thread has stopped.
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Interrupted while waiting for playback thread to stop.");
                    }
                }
                setPauseButtonOnUI();
                audioPlayer.play();
            }
        } else {
            Toast.makeText(ApplicationContext.getAppContext(), "No track selected.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void playPreviousTrack() {
        trackPosNr--;
        if (trackPosNr < 0) trackPosNr = tracks.size() - 1;
        playPauseTrack();
    }

    public void playNextTrack() {
        trackPosNr++;
        if (trackPosNr >= tracks.size()) trackPosNr = 0;
        playPauseTrack();
    }

    private void updateTrackPropertiesOnUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int duration = Integer.parseInt(currentTrack.getDuration());
                seekBar.setMax(duration);
                endTime.setText(DateUtils.formatElapsedTime(duration / 1000));
                updateSeekBarProgress();
            }
        });
    }

    private void updateSeekBarProgress() {
        //seekBar.setProgress(playerPresenter.getCurrentPosition());
        seekHandler.postDelayed(updateProgressTask, PROGRESS_UPDATE_INTERNAL);
    }

    private void setPauseButtonOnUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playPauseButton.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
            }
        });
    }

    private void setPlayButtonOnUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playPauseButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
            }
        });
    }

}
