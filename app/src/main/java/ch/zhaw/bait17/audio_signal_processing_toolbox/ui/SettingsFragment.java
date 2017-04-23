package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.RingModulation;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Bitcrusher;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.SoftClipper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Waveshaper;

/**
 * @author georgrem, stockan1
 */

public class SettingsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private List<AudioEffect> audioEffects;
    private OnItemChangedListener listener;
    private TextView textViewRingModFreqCurrentValue;
    private TextView textViewBitcrusherNormFreqCurrentValue;
    private TextView textViewBitcrusherBitDepthCurrentValue;
    private TextView textViewWaveshaperThresholdCurrentValue;

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

        if (audioEffects != null) {
            SeekBar seekBarRingModulationFrequency = (SeekBar) view.findViewById(R.id.seekbar_ringmod);
            seekBarRingModulationFrequency.setOnSeekBarChangeListener(this);
            seekBarRingModulationFrequency.setMax(Constants.RING_MODULATOR_MAX_MOD_FREQ);

            SeekBar seekBarBitCrusherNormFreq = (SeekBar) view.findViewById(
                    R.id.seekbar_bitcrusher_norm_freq);
            seekBarBitCrusherNormFreq.setOnSeekBarChangeListener(this);
            seekBarBitCrusherNormFreq.setMax(100);

            SeekBar seekBarBitcrusherBitDepth = (SeekBar) view.findViewById(
                    R.id.seekbar_bitcrusher_bit_depth);
            seekBarBitcrusherBitDepth.setOnSeekBarChangeListener(this);
            seekBarBitcrusherBitDepth.setMax(Constants.BITCRUSHER_MAX_BIT_DEPTH - 1);

            SeekBar seekBarSoftClipperClippingFactor = (SeekBar) view.findViewById(
                    R.id.seekbar_waveshaper_threshold);
            seekBarSoftClipperClippingFactor.setOnSeekBarChangeListener(this);
            seekBarSoftClipperClippingFactor.setMax(1000);
        }

        textViewRingModFreqCurrentValue = (TextView) view.findViewById(
                R.id.ringmod_freq_current_value);
        textViewBitcrusherNormFreqCurrentValue = (TextView) view.findViewById(
                R.id.bitcrusher_norm_freq_current_value);
        textViewBitcrusherBitDepthCurrentValue = (TextView) view.findViewById(
                R.id.bitcrusher_bit_depth_current_value);
        textViewWaveshaperThresholdCurrentValue = (TextView) view.findViewById(
                R.id.waveshaper_threshold_current_value);

        return view;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Bitcrusher bitcrusher = null;

        switch (seekBar.getId()) {
            case R.id.seekbar_ringmod:
                RingModulation ringMod = getRingModulator();
                if (ringMod != null) {
                    ringMod.setFrequency(progress);
                    if (textViewRingModFreqCurrentValue != null) {
                        textViewRingModFreqCurrentValue.setText(String.format("%d Hz", progress));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_bitcrusher_norm_freq:
                bitcrusher = getBitcrusher();
                if (bitcrusher != null) {
                    float normFreq = Constants.BITCRUSHER_MAX_NORM_FREQ * (progress / 100.0f);
                    bitcrusher.setNormFrequency(normFreq);
                    if (textViewBitcrusherNormFreqCurrentValue != null) {
                        textViewBitcrusherNormFreqCurrentValue.setText(Float.toString(normFreq));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_bitcrusher_bit_depth:
                bitcrusher = getBitcrusher();
                if (bitcrusher != null) {
                    int bits = progress + 1;
                    bitcrusher.setBits(bits);
                    if (textViewBitcrusherBitDepthCurrentValue != null) {
                        textViewBitcrusherBitDepthCurrentValue.setText(Integer.toString(bits));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_waveshaper_threshold:
                Waveshaper waveshaper = getWaveshaper();
                if (waveshaper != null) {
                    float threshold = progress / 1000.0f;
                    if (textViewWaveshaperThresholdCurrentValue != null) {
                        textViewWaveshaperThresholdCurrentValue.setText(
                                Float.toString(threshold));
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

    @Nullable
    private RingModulation getRingModulator() {
        for (AudioEffect fx : audioEffects) {
            if (fx instanceof RingModulation) {
                return (RingModulation) fx;
            }
        }
        return null;
    }

    @Nullable
    private Bitcrusher getBitcrusher() {
        for (AudioEffect fx : audioEffects) {
            if (fx instanceof Bitcrusher) {
                return (Bitcrusher) fx;
            }
        }
        return null;
    }

    @Nullable
    private SoftClipper getSoftClipper() {
        for (AudioEffect fx : audioEffects) {
            if (fx instanceof SoftClipper) {
                return (SoftClipper) fx;
            }
        }
        return null;
    }

    @Nullable
    private Waveshaper getWaveshaper() {
        for (AudioEffect fx : audioEffects) {
            if (fx instanceof Waveshaper) {
                return (Waveshaper) fx;
            }
        }
        return null;
    }

}
