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
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

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

    private Track track;
    private SeekBar seekBar;
    private PlayerPresenter playerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizations);

        final WaveformView waveformView = (WaveformView) findViewById(R.id.waveformView);
        final SpectrumView spectrumView = (SpectrumView) findViewById(R.id.spectrumView);
        final ImageButton playButton = (ImageButton) findViewById(R.id.play_pause);

        track = getIntent().getExtras().getParcelable(MediaListActivity.KEY_SONG);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
                playButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
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

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerPresenter.selectTrack(track);
            }
        });
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
