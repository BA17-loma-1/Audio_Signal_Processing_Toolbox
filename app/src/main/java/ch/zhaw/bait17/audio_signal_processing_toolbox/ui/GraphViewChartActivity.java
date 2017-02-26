package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import ch.zhaw.bait17.audio_signal_processing_toolbox.DecoderException;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Song;

public class GraphViewChartActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private double graphLastXValue = 5d;
    private LineGraphSeries<DataPoint> mSeries;
    private Song song;
    private WaveDecoder decoder;
    byte[] pcmData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view_chart);

        Bundle bundle = getIntent().getExtras();
        song = bundle.getParcelable(MediaListActivity.KEY_SONG);

        try {
            InputStream is = getContentResolver().openInputStream(song.getUri());
            decoder = new WaveDecoder(is);
            pcmData = decoder.getRawPCM();
        } catch (DecoderException | IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        initGraph(graph);
    }

    public void initGraph(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(4);

        graph.getGridLabelRenderer().setLabelVerticalWidth(100);

        // first mSeries is a line
        mSeries = new LineGraphSeries<>();
        mSeries.setDrawDataPoints(true);
        mSeries.setDrawBackground(true);
        graph.addSeries(mSeries);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Runnable() {
            @Override
            public void run() {
                graphLastXValue += 0.1d;
                mSeries.appendData(new DataPoint(graphLastXValue, getCurrentYValue()), true, 50);
                mHandler.postDelayed(this, 330);
            }
        };
        mHandler.postDelayed(mTimer, 1500);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mTimer);
    }

    double mLastYValue = 0;
    int i = 0;

    private double getCurrentYValue() {
        return mLastYValue += pcmData[i++];
    }
}
