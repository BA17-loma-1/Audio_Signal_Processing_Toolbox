package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.ApplicationContext;

/**
 * @author georgrem, stockan1
 */
public class LineSpectrumGraphView extends FrequencyView {

    private Context context;
    private GraphView graphView;
    private LineGraphSeries<DataPoint> preFilterSeries;
    private LineGraphSeries<DataPoint> postFilterSeries;


    public LineSpectrumGraphView(Context context) {
        super(context);
        this.context = context;
        initGraph();
    }

    private void initGraph() {
        getGraphView();

        // styling grid/labels
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.DKGRAY);
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.DKGRAY);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Frequency [Hz]");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Audio Spectrum [dB]");
        graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLUE);
        graphView.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLUE);

        // styling legend
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setTextSize(25);
        graphView.getLegendRenderer().setBackgroundColor(Color.argb(150, 50, 0, 0));
        graphView.getLegendRenderer().setTextColor(Color.WHITE);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphView.getLegendRenderer().setMargin(30);
        graphView.getLegendRenderer().setFixedPosition(150, 0);
    }

    private void initPreFilterSeries() {
        // styling series
        preFilterSeries.setTitle("PRE_FX");
        preFilterSeries.setColor(Color.BLACK);
    }

    private void initPostFilterSeries() {
        // styling series
        postFilterSeries.setTitle("POST_FX");
        postFilterSeries.setColor(Color.RED);
    }

    @Override
    public void setSpectralDensity(@NonNull float[] preFilterMagnitude, @NonNull float[] postFilterMagnitude) {
        if (preFilterMagnitude.length > 0) {
            preFilterSeries = new LineGraphSeries(getDataPoints(preFilterMagnitude));
            initPreFilterSeries();
        }
        if (postFilterMagnitude.length > 0) {
            postFilterSeries = new LineGraphSeries(getDataPoints(postFilterMagnitude));
            initPostFilterSeries();
        }

        graphView.post(new Runnable() {
            @Override
            public void run() {
                graphView.removeAllSeries();
                if (preFilterSeries != null)
                    graphView.addSeries(preFilterSeries);
                if (postFilterSeries != null)
                    graphView.addSeries(postFilterSeries);
            }
        });
    }

    private DataPoint[] getDataPoints(float[] values) {
        int count = values.length / 2;
        DataPoint[] dataPoints = new DataPoint[count];
        for (int i = 0; i < count; i++) {
            dataPoints[i] = new DataPoint(i, Math.log10(values[i]));
        }
        return dataPoints;
    }

    public View getGraphView() {
        if (graphView == null)
            graphView = (GraphView) View.inflate(ApplicationContext.getAppContext(),
                    R.layout.line_spectrum_graph_view, null);
        return graphView;
    }

    @Override
    public AudioView getInflatedView() {
        return new LineSpectrumGraphView(context);
    }

    @Override
    public void setFFTResolution(int fftResolution) {
    }

}
