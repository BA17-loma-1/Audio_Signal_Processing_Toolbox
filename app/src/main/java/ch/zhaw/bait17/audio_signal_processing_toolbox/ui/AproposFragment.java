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

public class AproposFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.apropos_view, container, false);
        return view;
    }
}
