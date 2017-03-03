/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by georgrem, stockan1 on 03.03.2017.
 */

package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.InputStream;
import ch.zhaw.bait17.audio_signal_processing_toolbox.AudioPlayer;
import ch.zhaw.bait17.audio_signal_processing_toolbox.DecoderException;
import ch.zhaw.bait17.audio_signal_processing_toolbox.PlaybackListener;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Song;

public class VisualisationActivity extends AppCompatActivity {

    private AudioPlayer audioPlayer;
    private Song song;
    private float[] audioSamples;
    private WaveDecoder decoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_player);

        final WaveformView realtimeWaveformView = (WaveformView) findViewById(R.id.waveformView);

        Bundle bundle = getIntent().getExtras();
        song = bundle.getParcelable(MediaListActivity.KEY_SONG);

        try {
            InputStream is = getContentResolver().openInputStream(song.getUri());
            decoder = new WaveDecoder(is);
            audioSamples = decoder.getFloat();
        } catch (FileNotFoundException | DecoderException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (audioSamples != null && decoder != null) {
            final ImageButton playButton = (ImageButton) findViewById(R.id.play_pause);

            audioPlayer = new AudioPlayer(decoder.getShort(),
                    decoder.getHeader().getSampleRate(),
                    decoder.getHeader().getChannels(), new PlaybackListener() {
                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onCompletion() {
                    playButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                }

                @Override
                public void onAudioDataReceived(short[] samples) {
                    realtimeWaveformView.setSamples(samples);
                }
            });

            realtimeWaveformView.setChannels(decoder.getHeader().getChannels());
            realtimeWaveformView.setSampleRate(audioPlayer.getSampleRate());

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!audioPlayer.isPlaying()) {
                        audioPlayer.play();
                        playButton.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                    } else {
                        audioPlayer.stop();
                        playButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                    }
                }
            });

        }
    }

}
