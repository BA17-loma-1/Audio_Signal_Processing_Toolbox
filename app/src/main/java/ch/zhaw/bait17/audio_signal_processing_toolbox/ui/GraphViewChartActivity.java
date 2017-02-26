package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import ch.zhaw.bait17.audio_signal_processing_toolbox.AudioStream;
import ch.zhaw.bait17.audio_signal_processing_toolbox.DecoderException;
import ch.zhaw.bait17.audio_signal_processing_toolbox.PlaybackListener;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Song;

public class GraphViewChartActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private Runnable timer;
    private double graphLastXValue = 5d;
    private LineGraphSeries<DataPoint> graphSeries;
    private Song song;
    private WaveDecoder decoder;
    float[] audioSamples;
    private AudioStream audioStream;
    private int currentPosition;
    private static final int ACTIVITY_SLEEP_TIME = 10;
    private static final int THREAD_SLEEP_TIME = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view_chart);

        Bundle bundle = getIntent().getExtras();
        song = bundle.getParcelable(MediaListActivity.KEY_SONG);

        try {
            InputStream is = getContentResolver().openInputStream(song.getUri());
            decoder = new WaveDecoder(is);
            audioSamples = decoder.getFloat();
        } catch (DecoderException | IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        initGraph(graph);

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
            audioStream.start();
        } catch (DecoderException e) {
            e.printStackTrace();
        }
    }

    public void initGraph(GraphView graph) {
        // first graphSeries is a line
        graphSeries = new LineGraphSeries<>();
        graphSeries.setDrawDataPoints(true);
        graphSeries.setDrawBackground(false);
        graph.addSeries(graphSeries);

        graphSeries.setDrawDataPoints(false);
        graphSeries.setThickness(3);
        StaticLabelsFormatter formatter = new StaticLabelsFormatter(graph);
        formatter.setHorizontalLabels(new String[]{"",""});
        formatter.setVerticalLabels(new String[]{"",""});
        graph.getGridLabelRenderer().setLabelFormatter(formatter);

        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);

        //graph.getGridLabelRenderer().setLabelVerticalWidth(100);
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {
                graphSeries.resetData(getCurrentYValues());
                handler.postDelayed(this, ACTIVITY_SLEEP_TIME);
            }
        };
        handler.postDelayed(timer, THREAD_SLEEP_TIME);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(timer);
    }

    @NonNull
    private DataPoint[] getCurrentYValues() {
        float[] samples = Arrays.copyOfRange(audioSamples, currentPosition,
                currentPosition + 20);
        DataPoint[] values = new DataPoint[samples.length];
        for (int i = 0; i < samples.length; i++) {
            values[i] = new DataPoint(i ,samples[i]);
        }
        return values;
    }
}
