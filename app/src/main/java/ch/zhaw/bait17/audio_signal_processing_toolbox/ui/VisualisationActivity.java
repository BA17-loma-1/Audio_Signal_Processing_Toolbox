package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import ch.zhaw.bait17.audio_signal_processing_toolbox.AudioStream;
import ch.zhaw.bait17.audio_signal_processing_toolbox.DecoderException;
import ch.zhaw.bait17.audio_signal_processing_toolbox.PlaybackListener;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Song;

public class VisualisationActivity extends AppCompatActivity {

    private static final int ACTIVITY_SLEEP_TIME = 10;
    private static final int THREAD_SLEEP_TIME = 10;

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
        setContentView(R.layout.activity_main);

        final WaveformView realtimeWaveformView = (WaveformView) findViewById(R.id.playbackWaveformView);
        final WaveformView recordingWaveformView = (WaveformView) findViewById(R.id.waveformView);

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
            final FloatingActionButton playFab = (FloatingActionButton) findViewById(R.id.playFab);

            try {
                audioStream = new AudioStream(decoder.getHeader(), decoder.getFloat(), new PlaybackListener() {
                    @Override
                    public void onProgress(int progress) {
                        realtimeWaveformView.setMarkerPosition(progress);
                    }

                    @Override
                    public void onCompletion() {
                        realtimeWaveformView.setMarkerPosition(realtimeWaveformView.getAudioLength());
                        playFab.setImageResource(android.R.drawable.ic_media_play);
                    }
                });
            } catch (DecoderException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            realtimeWaveformView.setChannels(decoder.getHeader().getChannels());
            realtimeWaveformView.setSampleRate(audioStream.getSampleRate());
            realtimeWaveformView.setSamples(audioSamples);

            try {
                audioStream = new AudioStream(decoder.getHeader(), decoder.getFloat(), new PlaybackListener() {
                    @Override
                    public void onProgress(int progress) {
                        currentPosition = progress;
                    }
                    @Override
                    public void onCompletion() {
                        currentPosition = audioSamples.length;
                    }
                });
            } catch (DecoderException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            playFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!audioStream.isPlaying()) {
                        audioStream.start();
                        playFab.setImageResource(android.R.drawable.ic_media_pause);
                    } else {
                        audioStream.stop();
                        playFab.setImageResource(android.R.drawable.ic_media_play);
                    }
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, ACTIVITY_SLEEP_TIME);
            }
        };
        handler.postDelayed(timer, THREAD_SLEEP_TIME);
    }

    /*
    private Point[] getCurrentYValues() {
        float[] samples = Arrays.copyOfRange(audioSamples, currentPosition,
                currentPosition + 20);
        Point[] points = new Point[samples.length];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(i ,samples[i]);
        }
        return points;
    }
*/
}
