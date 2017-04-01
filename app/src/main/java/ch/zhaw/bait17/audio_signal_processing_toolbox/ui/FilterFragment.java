package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FilterType;

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
    public static FilterFragment newInstance(Filter[] filters) {
        FilterFragment fragment = new FilterFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelableArray("filters", filters);
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

    @Override
    public void onStart() {
        super.onStart();
        // During startup, check if there are arguments passed to the fragment.
        Bundle bundle = getArguments();
        if (bundle != null) {
            Filter[] filters = (Filter[]) bundle.getParcelableArray("filters");
            setFilterSpecs(filters);
        }
    }

    private void setFilterSpecs(Filter[] filters) {
        TextView lowPassTextView = (TextView) getView().findViewById(R.id.textview_low_pass_filter_spec);
        TextView highPassTextView = (TextView) getView().findViewById(R.id.textview_high_pass_filter_spec);
        TextView bandPassTextView = (TextView) getView().findViewById(R.id.textview_band_pass_filter_spec);
        TextView bandStopTextView = (TextView) getView().findViewById(R.id.textview_band_stop_filter_spec);
        for (Filter filter : filters) {
            FilterType type = filter.getFilterSpec().getFilterType();
            switch (type) {
                case LOWPASS:
                    if (lowPassTextView != null) {
                        lowPassTextView.setText(filter.getFilterSpec().toString());
                    }
                    break;
                case HIGHPASS:
                    if (highPassTextView != null) {
                        highPassTextView.setText(filter.getFilterSpec().toString());
                    }
                    break;
                case BANDPASS:
                    if (bandPassTextView != null) {
                        bandPassTextView.setText(filter.getFilterSpec().toString());
                    }
                    break;
                case BANDSTOP:
                    if (bandStopTextView != null) {
                        bandStopTextView.setText(filter.getFilterSpec().toString());
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
