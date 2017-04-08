package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author georgrem, stockan1
 */

public class CustomSpinnerAdapter extends BaseAdapter {

    private Context context;
    private int images[];
    private String[] viewNames;
    private LayoutInflater inflater;

    public CustomSpinnerAdapter(Context applicationContext, int[] images, String[] viewNames) {
        this.context = applicationContext;
        this.images = images;
        this.viewNames = viewNames;
        inflater = (LayoutInflater.from(applicationContext));
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
        view = inflater.inflate(R.layout.custom_spinner_items, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        icon.setImageResource(images[position]);
        names.setText(viewNames[position]);
        return view;
    }
}