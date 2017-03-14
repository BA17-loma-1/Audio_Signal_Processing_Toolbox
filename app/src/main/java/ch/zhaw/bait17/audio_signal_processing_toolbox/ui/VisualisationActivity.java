/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ch.zhaw.bait17.audio_signal_processing_toolbox.player.PlaybackListener;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.player.PlayerPresenter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrumView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.WaveformView;

import static android.view.View.VISIBLE;

/**
 * Created by georgrem, stockan1 on 25.02.2017.
 */

public class VisualisationActivity extends AppCompatActivity {

    private static final String TAG = VisualisationActivity.class.getSimpleName();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private List<Track> tracks;
    private Track track;
    private int trackPosNr;
    private SeekBar seekBar;
    private PlayerPresenter playerPresenter;
    private TextView title;
    private TextView artist;
    private TextView currentTime;
    private TextView endTime;

    private final Handler handler = new Handler();
    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };
    private final ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;
    private PlaybackStateCompat lastPlaybackState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizations);

        title = (TextView) findViewById(R.id.track_title);
        artist = (TextView) findViewById(R.id.track_artist);
        currentTime = (TextView) findViewById(R.id.currentTime);
        endTime = (TextView) findViewById(R.id.endTime);

        final WaveformView waveformView = (WaveformView) findViewById(R.id.waveformView);
        final SpectrumView spectrumView = (SpectrumView) findViewById(R.id.spectrumView);

        trackPosNr = getIntent().getExtras().getInt(MediaListActivity.KEY_TRACK);
        tracks = getIntent().getExtras().getParcelableArrayList(MediaListActivity.KEY_TRACKS);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTime.setText(DateUtils.formatElapsedTime(progress / 1000));
                playerPresenter.seekToPosition(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playerPresenter.seekToPosition(seekBar.getProgress());
                //scheduleSeekbarUpdate();
            }
        });

        playerPresenter = new PlayerPresenter(this, new PlaybackListener() {
            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onCompletion() {
                //scheduleSeekbarUpdate();
                playNextTrack();
                Log.i(TAG, "onCompletion");
            }

            @Override
            public void onAudioDataReceived(short[] samples) {
                waveformView.setChannels(playerPresenter.getChannels());
                waveformView.setSampleRate(playerPresenter.getSampleRate());
                waveformView.setSamples(samples);

                spectrumView.setSampleRate(playerPresenter.getSampleRate());
                spectrumView.setSamples(samples);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spectrumView.setVisibility(VISIBLE);
                    }
                });
            }
        });
    }

    public void onClick_play_pause(View view) {
        playTrack();
    }

    public void onClick_prev(View view) {
        playPreviousTrack();
    }

    public void onClick_next(View view) {
        playNextTrack();
    }

    private void playTrack() {
        track = tracks.get(trackPosNr);
        playerPresenter.selectTrack(track);
        updateTrackPropertiesOnUI();
    }

    private void playPreviousTrack() {
        trackPosNr--;
        if (trackPosNr < 0) trackPosNr = tracks.size() - 1;
        playTrack();
    }

    private void playNextTrack() {
        trackPosNr++;
        if (trackPosNr >= tracks.size()) trackPosNr = 0;
        playTrack();
    }

    private void updateTrackPropertiesOnUI() {
        title.setText(track.getTitle());
        artist.setText(track.getArtist());
        int duration = Integer.parseInt(track.getDuration());
        seekBar.setMax(duration);
        endTime.setText(DateUtils.formatElapsedTime(duration / 1000));
        //scheduleSeekbarUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerPresenter.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerPresenter.resume();
    }

    @Override
    protected void onDestroy() {
        playerPresenter.destroy();
        super.onDestroy();
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!scheduledExecutorService.isShutdown()) {
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            handler.post(updateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    private void updateProgress() {
        seekBar.setProgress(playerPresenter.getCurrentPosition());
    }
}
