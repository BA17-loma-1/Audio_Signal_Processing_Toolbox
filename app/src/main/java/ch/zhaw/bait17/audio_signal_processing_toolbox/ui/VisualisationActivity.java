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

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import android.content.Context;
import android.os.IBinder;

import ch.zhaw.bait17.audio_signal_processing_toolbox.PlaybackListener;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Song;

/**
 * Created by georgrem, stockan1 on 25.02.2017.
 */

public class VisualisationActivity extends AppCompatActivity {

    private AudioPlayerService audioPlayerService;
    private Song song;
    private short[] audioSamples;
    private WaveDecoder decoder;
    private SeekBar seekBar;

    private AudioPlayerService serviceReference;
    private boolean isBound;
    private String TAG = "bound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizations);

        final WaveformView waveformView = (WaveformView) findViewById(R.id.waveformView);
        final SpectrumView spectrumView = (SpectrumView) findViewById(R.id.spectrumView);

        song = getIntent().getExtras().getParcelable(MediaListActivity.KEY_SONG);
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

        Log.i(TAG, "Service starting...");
        Intent intent = new Intent(this, AudioPlayerService.class);
        startService(intent);



        final ImageButton playButton = (ImageButton) findViewById(R.id.play_pause);

        if (serviceReference != null) {
            serviceReference.setListener(new PlaybackListener() {
                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onCompletion() {
                    playButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                }

                @Override
                public void onAudioDataReceived(short[] samples) {
                    waveformView.setSamples(samples);
                    spectrumView.setSamples(samples);
                }
            });

            waveformView.setChannels(audioPlayerService.getChannelOut());
            waveformView.setSampleRate(audioPlayerService.getSampleRate());
            spectrumView.setSampleRate(audioPlayerService.getSampleRate());

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!audioPlayerService.isPlaying()) {
                        audioPlayerService.play();
                        playButton.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                    } else {
                        audioPlayerService.pause();
                        playButton.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                    }
                }
            });
        }


    }


    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Bound service connected");
            serviceReference = ((AudioPlayerService.LocalBinder) service).getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Problem: bound service disconnected");
            serviceReference = null;
            isBound = false;
        }
    };

    private void doUnbindService() {
        Toast.makeText(this, "Unbinding...", Toast.LENGTH_SHORT).show();
        unbindService(myConnection);
        isBound = false;
    }

    private void doBindToService() {
        Toast.makeText(this, "Binding...", Toast.LENGTH_SHORT).show();
        if (!isBound) {
            Intent bindIntent = new Intent(this, AudioPlayerService.class);
            isBound = bindService(bindIntent, myConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "ch.zhaw.moba2.sandbox.MainActivity - onStart - binding...");
        doBindToService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "ch.zhaw.moba2.sandbox.MainActivity - onStop - unbinding...");
        doUnbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroying activity...");
        if (isFinishing()) {
            Log.i(TAG, "activity is finishing");
            Intent intentStopService = new Intent(this, AudioPlayerService.class);
            stopService(intentStopService);
        }
    }

}
