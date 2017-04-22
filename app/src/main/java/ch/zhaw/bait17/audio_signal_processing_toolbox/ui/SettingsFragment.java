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
import android.widget.SeekBar;

import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.RingModulation;

/**
 * @author georgrem, stockan1
 */

public class SettingsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private List<AudioEffect> audioEffects;
    private OnItemChangedListener listener;
    private SeekBar seekBarRingModulationFrequency;


    // the event that the fragment will use to communicate
    public interface OnItemChangedListener {
        void onParameterChanged(List<AudioEffect> audioEffect);
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
        if (context instanceof OnItemChangedListener) {
            listener = (OnItemChangedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement SettingsFragment.OnItemSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_view, container, false);

        if(audioEffects != null) {
            seekBarRingModulationFrequency = (SeekBar) view.findViewById(R.id.seekbar_ringmod);
            seekBarRingModulationFrequency.setOnSeekBarChangeListener(this);
        }

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_ringmod:
                for (AudioEffect audioEffect : audioEffects) {
                    if (audioEffect instanceof RingModulation) {
                        ((RingModulation) audioEffect).setFrequency(progress);
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setAudioEffects(List<AudioEffect> audioEffects) {
        this.audioEffects = audioEffects;
    }
}
