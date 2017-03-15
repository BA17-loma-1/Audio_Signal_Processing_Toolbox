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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.List;

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

public class VisualisationActivity extends AppCompatActivity implements OnSeekBarChangeListener {

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;

    private List<Track> tracks;
    private Track track;
    private PlayerPresenter playerPresenter;
    private TextView title;
    private TextView artist;
    private TextView currentTime;
    private TextView endTime;
    private SeekBar seekBar;
    private ImageButton playPauseButton;
    private int trackPosNr;

    private final Handler seekHandler = new Handler();

    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizations);

        final WaveformView waveformView = (WaveformView) findViewById(R.id.waveformView);
        final SpectrumView spectrumView = (SpectrumView) findViewById(R.id.spectrumView);

        title = (TextView) findViewById(R.id.track_title);
        artist = (TextView) findViewById(R.id.track_artist);
        currentTime = (TextView) findViewById(R.id.currentTime);
        endTime = (TextView) findViewById(R.id.endTime);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        playPauseButton = (ImageButton) findViewById(R.id.play_pause);

        trackPosNr = getIntent().getExtras().getInt(MediaListActivity.KEY_TRACK);
        tracks = getIntent().getExtras().getParcelableArrayList(MediaListActivity.KEY_TRACKS);

        playerPresenter = new PlayerPresenter(this, new PlaybackListener() {
            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onCompletion() {
                playPauseButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                playNextTrack();
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
        if (playerPresenter.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
        } else {
            playPauseButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
        }
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
        updateSeekBarProgress();
    }

    private void updateSeekBarProgress() {
        seekBar.setProgress(playerPresenter.getCurrentPosition());
        seekHandler.postDelayed(updateProgressTask, PROGRESS_UPDATE_INTERNAL);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentTime.setText(DateUtils.formatElapsedTime(progress / 1000));
        playerPresenter.seekToPosition(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        playerPresenter.seekToPosition(seekBar.getProgress());
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

}
