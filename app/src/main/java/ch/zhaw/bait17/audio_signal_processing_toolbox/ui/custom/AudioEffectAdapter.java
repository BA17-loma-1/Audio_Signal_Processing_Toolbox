package ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * @author georgrem, stockan1
 */

public class AudioEffectAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<AudioEffect> audioEffects;

    public AudioEffectAdapter(List<AudioEffect> audioEffects) {
        this.audioEffects = audioEffects;
        inflater = LayoutInflater.from(ApplicationContext.getAppContext());
    }

    @Override
    public int getCount() {
        return audioEffects.size();
    }

    @Override
    public Object getItem(int position) {
        return audioEffects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.audio_effect_spinner_items, null);
        TextView name = (TextView) view.findViewById(R.id.textView_name);
        TextView description = (TextView) view.findViewById(R.id.textView_description);
        AudioEffect audioEffect = audioEffects.get(position);
        if (audioEffect != null) {
            name.setText(audioEffect.getLabel());
            // description.setVisibility(View.VISIBLE);
            description.setText(audioEffect.getDescription());
        } else {
            name.setText("No filter");
            // description.setVisibility(View.GONE);
            description.setText("");
        }
        return view;
    }
}