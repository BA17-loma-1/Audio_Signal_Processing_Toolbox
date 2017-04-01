package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;

/**
 * @author georgrem, stockan1
 */

public class VisualisationConfigurationFragment extends Fragment {


    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.visualisation_configuration_view, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
