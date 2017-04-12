package ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FilterType;

/**
 * @author georgrem, stockan1
 */

public class FilterAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Filter> filters;

    public FilterAdapter(List<Filter> filters) {
        this.filters = filters;
        inflater = LayoutInflater.from(ApplicationContext.getAppContext());
    }

    @Override
    public int getCount() {
        return filters.size();
    }

    @Override
    public Object getItem(int position) {
        return filters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.filter_spinner_items, null);
        TextView name = (TextView) view.findViewById(R.id.textview_name);
        TextView description = (TextView) view.findViewById(R.id.textview_description);
        Filter filter = filters.get(position);
        if (filter != null) {
            FilterType filterType = filter.getFilterSpec().getFilterType();
            name.setText(FilterType.getLabel(filterType));
            // description.setVisibility(View.VISIBLE);
            description.setText(filter.getFilterSpec().toString());
        } else {
            name.setText(FilterType.getLabel(FilterType.UNKNOWN));
            // description.setVisibility(View.GONE);
            description.setText("");
        }
        return view;
    }
}