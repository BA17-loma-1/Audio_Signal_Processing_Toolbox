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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.PlaybackListener;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.player.PlayerPresenter;

import static android.view.View.VISIBLE;

/**
 * Created by georgrem, stockan1 on 25.02.2017.
 */

public class VisualisationActivity extends AppCompatActivity {

    private static final String TAG = VisualisationActivity.class.getSimpleName();

    private List<Track> tracks;
    private Track track;
    private int trackPosNr;
    private SeekBar seekBar;
    private PlayerPresenter playerPresenter;

    private TextView title;
    private TextView artist;
    private TextView currentTime;
    private TextView endTime;

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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        playerPresenter = new PlayerPresenter(this, new PlaybackListener() {
            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onCompletion() {
            }

            @Override
            public void onAudioDataReceived(short[] samples) {
                waveformView.setChannels(playerPresenter.getChannelOut());
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

    public void onClick_prev(View view) {
        trackPosNr--;
        if (trackPosNr < 0) trackPosNr = tracks.size() - 1;
        playTrack();
    }

    public void onClick_play_pause(View view) {
        playTrack();
    }

    public void onClick_next(View view) {
        trackPosNr++;
        if (trackPosNr >= tracks.size()) trackPosNr = 0;
        playTrack();
    }

    private void playTrack() {
        track = tracks.get(trackPosNr);
        playerPresenter.selectTrack(track);
        updateTrackPropertiesOnUI();
    }

    private void updateTrackPropertiesOnUI() {
        title.setText(track.getTitle());
        artist.setText(track.getArtist());
        currentTime.setText("0");
        endTime.setText(DateUtils.formatElapsedTime(Integer.parseInt(track.getDuration()) / 1000));
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
