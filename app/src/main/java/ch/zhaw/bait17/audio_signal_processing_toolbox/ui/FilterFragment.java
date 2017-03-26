package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;

/**
 * @author georgrem, stockan1
 */

public class FilterFragment extends Fragment {

    /*
        In certain cases, your fragment may want to accept certain arguments.
        A common pattern is to create a static newInstance method for creating a Fragment with arguments.
        This is because a Fragment must have only a constructor with no arguments.
        From: {@link https://guides.codepath.com/android/Creating-and-Using-Fragments#communicating-with-fragments}
     */
    public static FilterFragment newInstance(Filter filter) {
        FilterFragment fragment = new FilterFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable("filter_view", filter);
        fragment.setArguments(arguments);
        return fragment;
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_view, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
