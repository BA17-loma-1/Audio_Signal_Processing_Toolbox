package ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.ViewName;

/**
 * @author georgrem, stockan1
 */

public class ViewAdapter extends BaseAdapter {

    private ViewName[] viewNames;
    private LayoutInflater inflater;

    public ViewAdapter(ViewName[] viewNames) {
        this.viewNames = Arrays.copyOf(viewNames, viewNames.length);
        inflater = LayoutInflater.from(ApplicationContext.getAppContext());
    }

    @Override
    public int getCount() {
        return viewNames.length;
    }

    @Override
    public Object getItem(int position) {
        return viewNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.view_spinner_items, null);
        TextView names = (TextView) view.findViewById(R.id.textView_name);
        if (position < viewNames.length && position >= 0) {
            ViewName viewName = viewNames[position];
            if (viewName != null) {
                names.setText(viewName.toString());
            }
        }
        return view;
    }
}