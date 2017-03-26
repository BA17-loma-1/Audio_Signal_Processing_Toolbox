package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.List;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.player.PlayerPresenter;

/**
 * @author georgrem, stockan1
 */

public class AudioPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static final String BUNDLE_ARGUMENT_FILTER = "filter_view";
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private final Handler seekHandler = new Handler();
    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };

    private View rootView;
    private Thread playerThread;
    private List<Track> tracks;
    private Track currentTrack;
    private int trackPosNr;
    private PlayerPresenter playerPresenter;
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
        rootView = inflater.inflate(R.layout.audio_player, container, false);
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
        Bundle args = getArguments();
        Filter filter = null;
        if (args != null) {
            filter = args.getParcelable(BUNDLE_ARGUMENT_FILTER);
        }
        playerPresenter = new PlayerPresenter(filter);
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
        playerPresenter.seekToPosition(seekBar.getProgress());
    }

    public void setFilter(Filter filter) {
        playerPresenter.setFilter(filter);
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void setTrackPosNr(int trackPosNr) {
        this.trackPosNr = trackPosNr;
        playTrack();
    }

    public void setTrack(Track track) {
        Track previousTrack = currentTrack;
        currentTrack = track;
        if (!track.equals(previousTrack)) {
            if (playerThread != null && playerPresenter.isPlaying()) {
                // Play newly selected track
                pauseTrack();
            }
            playTrack();
        }
    }

    private void pauseTrack() {
        if (playerThread != null) {
            playerPresenter.stop();
            try {
                playerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void playTrack() {
        if (playerThread != null && playerPresenter.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
            pauseTrack();
        } else {
            playPauseButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
            playerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    playerPresenter.selectTrack(currentTrack);
                }
            });
            playerThread.start();
            updateTrackPropertiesOnUI();
        }
    }

    public void playPreviousTrack() {
        trackPosNr--;
        if (trackPosNr < 0) trackPosNr = tracks.size() - 1;
        playTrack();
    }

    public void playNextTrack() {
        trackPosNr++;
        if (trackPosNr >= tracks.size()) trackPosNr = 0;
        playTrack();
    }

    private void updateTrackPropertiesOnUI() {
        int duration = Integer.parseInt(currentTrack.getDuration());
        seekBar.setMax(duration);
        endTime.setText(DateUtils.formatElapsedTime(duration / 1000));
        updateSeekBarProgress();
    }

    private void updateSeekBarProgress() {
        //seekBar.setProgress(playerPresenter.getCurrentPosition());
        seekHandler.postDelayed(updateProgressTask, PROGRESS_UPDATE_INTERNAL);
    }

}
