package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom.FilterAdapter;

/**
 * @author georgrem, stockan1
 */

public class FilterFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String BUNDLE_ARGUMENT_FILTERS = FilterFragment.class.getSimpleName() + ".FILTERS";

    private List<Filter> filters;
    private Filter[] activeFilters;


    // listener of the interface type
    private OnItemSelectedListener listener;

    // the event that the fragment will use to communicate
    public interface OnItemSelectedListener {
        void onFilterItemSelected(Filter[] filter);
    }

    /*
        In certain cases, your fragment may want to accept certain arguments.
        A common pattern is to create a static newInstance method for creating a Fragment with arguments.
        This is because a Fragment must have only a constructor with no arguments.
        From: {@link https://guides.codepath.com/android/Creating-and-Using-Fragments#communicating-with-fragments}
    */
    public static FilterFragment newInstance(List<Filter> filters) {
        FilterFragment fragment = new FilterFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BUNDLE_ARGUMENT_FILTERS, (Serializable) filters);
        fragment.setArguments(arguments);
        return fragment;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        // This method avoid to call super.onAttach(context) if I'm not using api 23 or more
        if (Build.VERSION.SDK_INT >= 23) {
            super.onAttach(context);
            onAttachToContext(context);
        }
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(activity);
        }
    }

    /*
     * This method will be called from one of the two previous method
     * Store the listener (activity) that will have events fired once the fragment is attached
     */
    private void onAttachToContext(Context context) {
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement FilterFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        Bundle arguments = this.getArguments();
        if (arguments.getSerializable(BUNDLE_ARGUMENT_FILTERS) != null)
            filters = (List<Filter>) arguments.getSerializable(BUNDLE_ARGUMENT_FILTERS);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_view, container, false);

        activeFilters = new Filter[4];
        FilterAdapter filterAdapter = new FilterAdapter(filters);

        Spinner spinnerFirstFilter = (Spinner) view.findViewById(R.id.spinner_first_filter);
        spinnerFirstFilter.setAdapter(filterAdapter);
        spinnerFirstFilter.setOnItemSelectedListener(this);

        Spinner spinnerSecondFilter = (Spinner) view.findViewById(R.id.spinner_second_filter);
        spinnerSecondFilter.setAdapter(filterAdapter);
        spinnerSecondFilter.setOnItemSelectedListener(this);

        Spinner spinnerThirdFilter = (Spinner) view.findViewById(R.id.spinner_third_filter);
        spinnerThirdFilter.setAdapter(filterAdapter);
        spinnerThirdFilter.setOnItemSelectedListener(this);

        Spinner spinnerFourthFilter = (Spinner) view.findViewById(R.id.spinner_fourth_filter);
        spinnerFourthFilter.setAdapter(filterAdapter);
        spinnerFourthFilter.setOnItemSelectedListener(this);

        return view;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Filter filter = (Filter) parent.getItemAtPosition(position);
        switch (parent.getId()) {
            case R.id.spinner_first_filter:
                activeFilters[0] = filter;
                break;
            case R.id.spinner_second_filter:
                activeFilters[1] = filter;
                break;
            case R.id.spinner_third_filter:
                activeFilters[2] = filter;
                break;
            case R.id.spinner_fourth_filter:
                activeFilters[3] = filter;
                break;
            default:
                break;
        }
        // fire the event
        if (listener != null)
            listener.onFilterItemSelected(activeFilters);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
