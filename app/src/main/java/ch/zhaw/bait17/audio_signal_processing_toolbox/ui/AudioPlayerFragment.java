package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.player.AudioPlayer;
import ch.zhaw.bait17.audio_signal_processing_toolbox.player.PlaybackListener;

/**
 * @author georgrem, stockan1
 */
public class AudioPlayerFragment extends Fragment {

    private static final String TAG = AudioPlayerFragment.class.getSimpleName();
    private static final String BUNDLE_ARGUMENT_AUDIOEFFECTS = "audio_effect_view";
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final Handler seekBarHandler = new Handler();
    private final ScheduledExecutorService executorService =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduleFuture;
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
    private AudioPlayer audioPlayer;
    private TextView currentTime;
    private TextView endTime;
    private SeekBar seekBar;
    private ImageButton playPauseButton;
    private View currentMediaListItemView;
    private View previousMediaListItemView;
    private RecyclerView recyclerView;

    /*
        In certain cases, your fragment may want to accept certain arguments.
        A common pattern is to create a static newInstance method for creating
        a Fragment with arguments.
        This is because a Fragment must have only a constructor with no arguments.
        From: <a href="https://guides.codepath.com/android/Creating-and-Using-Fragments#communicating-with-fragments">codepath.com</a>
     */
    public static AudioPlayerFragment newInstance(Filter filter) {
        AudioPlayerFragment fragment = new AudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_ARGUMENT_AUDIOEFFECTS, filter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
        The onCreateView method is called when Fragment should create its View object hierarchy,
        either dynamically or via XML layout inflation.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.audio_player, container, false);
        currentTime = (TextView) rootView.findViewById(R.id.currentTime);
        endTime = (TextView) rootView.findViewById(R.id.endTime);
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    currentTime.setText(DateUtils.formatElapsedTime(progress / 1000));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    stopSeekbarUpdate();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    audioPlayer.seekToPosition(seekBar.getProgress());
                    scheduleSeekbarUpdate();
                }
            });
            seekBar.setMax(100);
        }
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
                // Use to update the SeekBar position.
            }

            @Override
            public void onStartPlayback() {
                setPauseButtonOnUI();
            }

            @Override
            public void onCompletion() {
                setPlayButtonOnUI();
            }
        });
        Bundle args = getArguments();
        if (args != null) {
            List<AudioEffect> audioEffects =
                    args.getParcelableArrayList(BUNDLE_ARGUMENT_AUDIOEFFECTS);
            audioPlayer.setAudioEffects(audioEffects);
        }
    }

    /**
     * Sets the list of {@code AudioEffect}s.
     *
     * @param audioEffects list of {@code AudioEffect}s
     */
    public void setAudioEffects(List<AudioEffect> audioEffects) {
        audioPlayer.setAudioEffects(audioEffects);
    }

    /**
     * Sets the track list.
     *
     * @param tracks list of {@code Track}
     */
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    /**
     * Sets the recyclerView.
     *
     * @param recyclerView
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    /**
     * Selects an audio track from the track list and initiates playback.
     *
     * @param trackPosNr the {@code Track} id from the tracks list
     */
    public void setTrack(final int trackPosNr) {
        this.trackPosNr = trackPosNr % tracks.size();
        if (this.trackPosNr < 0){
            this.trackPosNr += tracks.size();
        }
        Track track = tracks.get(this.trackPosNr);
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

    /**
     * Plays or pauses the currently selected track.
     */
    public void playPauseTrack() {
        if (currentTrack != null && nextTrack != null) {
            if (currentTrack == nextTrack) {
                // No change in track selection.
                if (audioPlayer.isPlaying()) {
                    // Pause
                    setPlayButtonOnUI();
                    audioPlayer.pausePlayback();
                } else if (audioPlayer.isPaused()) {
                    // Resume playback
                    setPauseButtonOnUI();
                    audioPlayer.resumePlayback();
                } else {
                    // Start playback
                    setPauseButtonOnUI();
                    audioPlayer.play();
                }
            } else {
                // A new track has been selected.
                currentTrack = nextTrack;
                audioPlayer.stopPlayback();
                while (!audioPlayer.isStopped()) {
                    try {
                        // Sleep until the playback thread has stopped.
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Interrupted while waiting for playback thread to stop.");
                    }
                }
                updateTrackPropertiesOnUI();
                audioPlayer.play();
            }
        } else {
            // No track selected. Ok start with the first track
            setTrack(0);
        }
    }

    /**
     * Starts playback of previous track in the track list.
     */
    public void playPreviousTrack() {
        if (trackPosNr > 0) setTrack(--trackPosNr);
    }

    /**
     * Starts playback of next track in the track list.
     */
    public void playNextTrack() {
        if (trackPosNr < tracks.size() - 1) setTrack(++trackPosNr);
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!executorService.isShutdown()) {
            scheduleFuture = executorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            seekBarHandler.post(updateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    /**
     *
     * @param currentMediaListItemView
     */
    public void setCurrentMediaListItemView(View currentMediaListItemView) {
        this.currentMediaListItemView = currentMediaListItemView;
    }

    private void stopSeekbarUpdate() {
        if (scheduleFuture != null) {
            scheduleFuture.cancel(false);
        }
    }

    private void updateTrackPropertiesOnUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int duration = Integer.parseInt(currentTrack.getDuration());
                endTime.setText(DateUtils.formatElapsedTime(duration / 1000));
                //updateSeekBarProgress();
            }
        });
    }

    private void updateSeekBarProgress() {
        //seekBar.setProgress(playerPresenter.getCurrentPosition());
        if (!audioPlayer.isPaused()) {
            int framePosition = audioPlayer.getPlaybackPosition();
            seekBarHandler.postDelayed(updateProgressTask, PROGRESS_UPDATE_INTERNAL);
        }
    }

    private void setPauseButtonOnUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playPauseButton.setImageResource(R.drawable.uamp_ic_pause_white_48dp);

                currentMediaListItemView = recyclerView.findViewHolderForAdapterPosition(trackPosNr).itemView;
                if (previousMediaListItemView != null) {
                    setDrawablesOnPlay();
                }
                if (currentMediaListItemView != null) {
                    resetDrawablesOnPlay();
                }
            }
        });
    }

    private void setPlayButtonOnUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playPauseButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                setDrawablesOnPause();
            }
        });
    }

    private void setDrawablesOnPlay() {
        Drawable playDrawable = ContextCompat.getDrawable(ApplicationContext.getAppContext(),
                R.drawable.ic_play_arrow_black_36dp);
        ImageView playPauseImage = (ImageView) previousMediaListItemView.findViewById(R.id.play_pause);
        playPauseImage.setImageDrawable(playDrawable);

        ImageView equalizerImage = (ImageView) previousMediaListItemView.findViewById(R.id.play_eq);
        equalizerImage.setImageDrawable(null);

        TextView titleTextView = (TextView) previousMediaListItemView.findViewById(R.id.track_title);
        titleTextView.setTextColor(ContextCompat.getColor(ApplicationContext.getAppContext(), R.color.primary_text));
    }

    private void resetDrawablesOnPlay() {
        AnimationDrawable animation = (AnimationDrawable)
                ContextCompat.getDrawable(ApplicationContext.getAppContext(), R.drawable.ic_equalizer_white_36dp);
        animation.start();
        ImageView equalizerImage = (ImageView) currentMediaListItemView.findViewById(R.id.play_eq);
        equalizerImage.setImageDrawable(animation);

        Drawable pauseDrawable = ContextCompat.getDrawable(ApplicationContext.getAppContext(),
                R.drawable.ic_pause_black_36dp);
        ImageView playPauseImage = (ImageView) currentMediaListItemView.findViewById(R.id.play_pause);
        playPauseImage.setImageDrawable(pauseDrawable);

        TextView titleTextView = (TextView) currentMediaListItemView.findViewById(R.id.track_title);
        titleTextView.setTextColor(ContextCompat.getColor(ApplicationContext.getAppContext(), R.color.media_item_icon_playing));
        previousMediaListItemView = currentMediaListItemView;
    }

    private void setDrawablesOnPause() {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(trackPosNr);
        if (viewHolder == null) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            linearLayoutManager.scrollToPositionWithOffset(trackPosNr, 0);
        } else {
            currentMediaListItemView = recyclerView.findViewHolderForAdapterPosition(trackPosNr).itemView;

            Drawable playDrawable = ContextCompat.getDrawable(ApplicationContext.getAppContext(),
                    R.drawable.ic_play_arrow_black_36dp);
            ImageView playPauseImage = (ImageView) currentMediaListItemView.findViewById(R.id.play_pause);
            playPauseImage.setImageDrawable(playDrawable);

            Drawable equalizeDrawable = ContextCompat.getDrawable(ApplicationContext.getAppContext(),
                    R.drawable.ic_equalizer1_white_36dp);
            ImageView equalizerImage = (ImageView) currentMediaListItemView.findViewById(R.id.play_eq);
            equalizerImage.setImageDrawable(equalizeDrawable);
        }
    }

}
