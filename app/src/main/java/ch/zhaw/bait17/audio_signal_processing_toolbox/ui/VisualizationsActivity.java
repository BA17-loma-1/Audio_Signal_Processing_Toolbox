package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import org.achartengine.GraphicalView;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.SpectrumVisualiser;

/**
 * Created by georgrem, stockan1 on 15.02.2017.
 */
public class VisualizationsActivity extends Activity {

    private static GraphicalView view;
    private SpectrumVisualiser spectrumVisualiser = new SpectrumVisualiser();
    private static Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizations);

        Intent intent = getIntent();  // get the intent
        //String data = intent.getStringExtra(MainActivity.KEY_SONG); // retrieve the data
    }

    @Override
    protected void onStart() {
        super.onStart();
        view = spectrumVisualiser.getView(this);
        setContentView(view);

        thread = new Thread() {
            @Override
            public void run() {
                // Example from http://docs.oracle.com/javase/tutorial/sound/converters.html
                try {


                    view.repaint();
                } catch (Exception ex) {

                }
            }
        };
        thread.start();
    }

    /*
    private WaveHeaderInfo readHeader(InputStream waveStream) {

        return null;
    }
    */

}
