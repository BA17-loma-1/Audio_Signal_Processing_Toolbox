package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.InputStream;
import ch.zhaw.bait17.audio_signal_processing_toolbox.AudioStream;
import ch.zhaw.bait17.audio_signal_processing_toolbox.DecoderException;
import ch.zhaw.bait17.audio_signal_processing_toolbox.PlaybackListener;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Song;

public class VisualisationActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private Runnable timer;
    private AudioStream audioStream;
    private Song song;
    private float[] audioSamples;
    private WaveDecoder decoder;
    private int currentPosition;

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
            final ImageButton playFab = (ImageButton) findViewById(R.id.play_pause);

            try {
                audioStream = new AudioStream(decoder.getHeader(), decoder.getFloat(), new PlaybackListener() {
                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onCompletion() {
                        playFab.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                    }

                    @Override
                    public void onAudioDataReceived(float[] samples) {
                        realtimeWaveformView.setSamples(samples);
                    }
                });
            } catch (DecoderException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            realtimeWaveformView.setChannels(decoder.getHeader().getChannels());
            realtimeWaveformView.setSampleRate(audioStream.getSampleRate());

            playFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!audioStream.isPlaying()) {
                        audioStream.start();
                        playFab.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                    } else {
                        audioStream.stop();
                        playFab.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                    }
                }
            });

        }
    }

}
