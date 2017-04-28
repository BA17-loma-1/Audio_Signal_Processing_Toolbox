package ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Bitcrusher;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Overdrive;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.SoftClipper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Waveshaper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FIRFilter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation.RingModulation;

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
        view = inflater.inflate(R.layout.audio_effect_spinner_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.audio_effect_icon);
        TextView name = (TextView) view.findViewById(R.id.audio_effect_label);
        TextView description = (TextView) view.findViewById(R.id.audio_effect_description);
        AudioEffect audioEffect = audioEffects.get(position);
        if (audioEffect != null) {
            name.setText(audioEffect.getLabel());
            description.setText(audioEffect.getDescription());
            if (audioEffect instanceof FIRFilter) {
                switch (((FIRFilter) audioEffect).getFilterSpec().getFilterType()) {
                    case LOWPASS:
                        icon.setImageResource(R.mipmap.icon_lowpass);
                        break;
                    case HIGHPASS:
                        icon.setImageResource(R.mipmap.icon_highpass);
                        break;
                    case BANDPASS:
                        icon.setImageResource(R.mipmap.icon_bandpass);
                        break;
                    case BANDSTOP:
                        icon.setImageResource(R.mipmap.icon_bandstop);
                        break;
                    default:
                        icon.setImageResource(0);
                }
            } else if (audioEffect instanceof Bitcrusher) {
                icon.setImageResource(R.mipmap.icon_bitcrusher);
            } else if (audioEffect instanceof RingModulation) {
                icon.setImageResource(R.mipmap.icon_ringmod);
            } else if (audioEffect instanceof Waveshaper || audioEffect instanceof SoftClipper
                    || audioEffect instanceof Overdrive) {
                icon.setImageResource(R.mipmap.icon_waveshaper);
            }
        } else {
            name.setText(R.string.no_fx);
            description.setText("");
            icon.setImageResource(R.mipmap.icon_nofilter);
        }
        return view;
    }
}