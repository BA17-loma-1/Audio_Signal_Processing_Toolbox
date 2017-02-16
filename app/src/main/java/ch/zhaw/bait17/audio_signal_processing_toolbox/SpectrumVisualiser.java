package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.graphics.Color;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by georgrem, stockan1 on 15.02.2017.
 */
public class SpectrumVisualiser {

    private XYSeries dataset = new XYSeries("Spectrum");
    private XYMultipleSeriesDataset multiDataset = new XYMultipleSeriesDataset();
    // Single renderer for a series
    private XYSeriesRenderer renderer = new XYSeriesRenderer();
    // MultiRenderer controls the full chart
    private XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

    public SpectrumVisualiser() {
        renderer.setColor(Color.RED);
        renderer.setDisplayBoundingPoints(true);
        // Add a single renderer to the MultiRenderer
        multiRenderer.addSeriesRenderer(renderer);
        // Include low and max values
        multiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        multiRenderer.setPanEnabled(false, false);
        multiRenderer.setYAxisMin(0.0);
        multiRenderer.setShowGrid(true);
        // Axis labels
        multiRenderer.setXTitle("Frequency");
        multiRenderer.setYTitle("Amplitude");
    }

    /**
     * Sets the spectral data to be displayed in the bar chart.
     * @param data
     * @param samplingFrequency
     */
    public void setSpectrum(double[] data, int samplingFrequency) {
        double deltaFrequency = samplingFrequency / data.length;
        dataset.clear();
        for (int i = 0; i < data.length; i++) {
            dataset.add(i*deltaFrequency, data[i]);
        }
        multiDataset.addSeries(dataset);
    }

    public GraphicalView getView(Context context) {
        GraphicalView view = ChartFactory.getBarChartView(context, multiDataset,
                multiRenderer, BarChart.Type.DEFAULT);
        return view;
    }

}
