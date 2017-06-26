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
public class SpectrumView extends FrequencyView {

    private static final int MAX_FREQUENCY = 16000;

    private Context context;
    private GraphView graphView;
    private LineGraphSeries<DataPoint> preFilterSeries;
    private LineGraphSeries<DataPoint> postFilterSeries;
    private int fftResolution;


    public SpectrumView(Context context) {
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
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(0);
        graphView.getViewport().setMinY(-120);

        // styling legend
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setTextSize(30);
        graphView.getLegendRenderer().setTextColor(Color.BLACK);
        graphView.getLegendRenderer().setBackgroundColor(Color.WHITE);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graphView.getLegendRenderer().setMargin(30);
        graphView.getLegendRenderer().setWidth(200);
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
        int fs = getSampleRate();
        double deltaFreq = (fs / 2.0d) / values.length;
        int count = (int) (MAX_FREQUENCY / deltaFreq);
        float[] dBMag = new float[count];

        float dBMax = Float.MIN_VALUE;
        for (int i = 0; i < count; i++) {
            dBMag[i] = (float) (10 * Math.log10(values[i]));
            if (dBMag[i] > dBMax) {
                dBMax = dBMag[i];
            }
        }

        DataPoint[] dataPoints = new DataPoint[count];
        for (int i = 0; i < count; i++) {
            dataPoints[i] = new DataPoint(i * 2 * deltaFreq, (10 * Math.log10(values[i])) - dBMax);
        }
        return dataPoints;
    }

    public View getGraphView() {
        if (graphView == null)
            graphView = (GraphView) View.inflate(ApplicationContext.getAppContext(),
                    R.layout.spectrum_view, null);
        return graphView;
    }

    @Override
    public AudioView getInflatedView() {
        return new SpectrumView(context);
    }

    @Override
    public void setFFTResolution(int fftResolution) {
        this.fftResolution = fftResolution;
    }
}
