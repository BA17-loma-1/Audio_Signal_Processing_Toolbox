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

import java.text.DecimalFormat;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Bitcrusher;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.SoftClipper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.TubeDistortion;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation.RingModulation;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation.Tremolo;

/**
 * @author georgrem, stockan1
 */

public class SettingsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static final int DEFAULT_SEEK_BAR_STEPS = 100;
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private List<AudioEffect> audioEffects;
    private OnItemChangedListener listener;
    private TextView textViewRingModFreqCurrentValue;
    private TextView textViewTremoloModFreqCurrentValue;
    private TextView textViewTremoloModAmplCurrentValue;
    private TextView textViewBitcrusherNormFreqCurrentValue;
    private TextView textViewBitcrusherBitDepthCurrentValue;
    private TextView textViewSoftClipperClippingFactorCurrentValue;
    private TextView textViewTubeDistortionGainCurrentValue;
    private TextView textViewTubeDistortionMixCurrentValue;

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

            SeekBar seekBarTremoloModulationFrequency = (SeekBar) view.findViewById(R.id.seekbar_tremolo_frequency);
            seekBarTremoloModulationFrequency.setOnSeekBarChangeListener(this);
            seekBarTremoloModulationFrequency.setMax(Constants.TREMOLO_MAX_MOD_FREQUENCY);

            SeekBar seekBarTremoloModulationAmplitude = (SeekBar) view.findViewById(R.id.seekbar_tremolo_amplitude);
            seekBarTremoloModulationAmplitude.setOnSeekBarChangeListener(this);
            seekBarTremoloModulationAmplitude.setMax(DEFAULT_SEEK_BAR_STEPS);

            SeekBar seekBarBitcrusherNormFreq = (SeekBar) view.findViewById(
                    R.id.seekbar_bitcrusher_norm_freq);
            seekBarBitcrusherNormFreq.setOnSeekBarChangeListener(this);
            seekBarBitcrusherNormFreq.setMax(DEFAULT_SEEK_BAR_STEPS);

            SeekBar seekBarBitcrusherBitDepth = (SeekBar) view.findViewById(
                    R.id.seekbar_bitcrusher_bit_depth);
            seekBarBitcrusherBitDepth.setOnSeekBarChangeListener(this);
            seekBarBitcrusherBitDepth.setMax(Constants.BITCRUSHER_MAX_BIT_DEPTH - 1);

            SeekBar seekBarSoftClipperClippingFactor = (SeekBar) view.findViewById(
                    R.id.seekbar_soft_clipper_clipping_factor);
            seekBarSoftClipperClippingFactor.setOnSeekBarChangeListener(this);
            seekBarSoftClipperClippingFactor.setMax(Constants.SOFT_CLIPPER_MAX_CLIPPING_FACTOR - 1);

            SeekBar seekBarTubeDistortionGain = (SeekBar) view.findViewById(
                    R.id.seekbar_tube_distortion_gain);
            seekBarTubeDistortionGain.setOnSeekBarChangeListener(this);
            seekBarTubeDistortionGain.setMax(DEFAULT_SEEK_BAR_STEPS);

            SeekBar seekBarTubeDistortionMix = (SeekBar) view.findViewById(
                    R.id.seekbar_tube_distortion_mix);
            seekBarTubeDistortionMix.setOnSeekBarChangeListener(this);
            seekBarTubeDistortionMix.setMax(DEFAULT_SEEK_BAR_STEPS);
        }

        textViewRingModFreqCurrentValue = (TextView) view.findViewById(
                R.id.ringmod_freq_current_value);
        textViewTremoloModFreqCurrentValue = (TextView) view.findViewById(
                R.id.tremolo_freq_current_value);
        textViewTremoloModAmplCurrentValue = (TextView) view.findViewById(
                R.id.tremolo_ampl_current_value);
        textViewBitcrusherNormFreqCurrentValue = (TextView) view.findViewById(
                R.id.bitcrusher_norm_freq_current_value);
        textViewBitcrusherBitDepthCurrentValue = (TextView) view.findViewById(
                R.id.bitcrusher_bit_depth_current_value);
        textViewSoftClipperClippingFactorCurrentValue = (TextView) view.findViewById(
                R.id.soft_clipper_clipping_factor_current_value);
        textViewTubeDistortionGainCurrentValue = (TextView) view.findViewById(
                R.id.tube_distortion_gain_current_value);
        textViewTubeDistortionMixCurrentValue = (TextView) view.findViewById(
                R.id.tube_distortion_mix_current_value);

        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TubeDistortion tubeDistortion;
        Bitcrusher bitcrusher;
        Tremolo tremolo;

        switch (seekBar.getId()) {
            case R.id.seekbar_ringmod:
                RingModulation ringMod = getRingModulator();
                if (ringMod != null) {
                    ringMod.setFrequencyModulation(progress);
                    if (textViewRingModFreqCurrentValue != null) {
                        textViewRingModFreqCurrentValue.setText(String.format("%d Hz", progress));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_tremolo_frequency:
                tremolo = getTremolo();
                if (tremolo != null) {
                    tremolo.setFrequencyModulation(progress);
                    if (textViewTremoloModFreqCurrentValue != null) {
                        textViewTremoloModFreqCurrentValue.setText(String.format("%d Hz", progress));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_tremolo_amplitude:
                tremolo = getTremolo();
                if (tremolo != null) {
                    tremolo.setFrequencyModulation(progress);
                    float normAmpl = Constants.TREMOLO_MAX_AMPLITUDE *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    tremolo.setAmplitude(normAmpl);
                    if (textViewTremoloModAmplCurrentValue != null) {
                        textViewTremoloModAmplCurrentValue.setText(DECIMAL_FORMAT.format(normAmpl));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_bitcrusher_norm_freq:
                bitcrusher = getBitcrusher();
                if (bitcrusher != null) {
                    float normFreq = Constants.BITCRUSHER_MAX_NORM_FREQ *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    bitcrusher.setNormFrequency(normFreq);
                    if (textViewBitcrusherNormFreqCurrentValue != null) {
                        textViewBitcrusherNormFreqCurrentValue.setText(DECIMAL_FORMAT.format(normFreq));
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
            case R.id.seekbar_soft_clipper_clipping_factor:
                SoftClipper softClipper = getSoftClipper();
                if (softClipper!= null) {
                    int clippingFactor = progress + 1;
                    softClipper.setClippingFactor((float) clippingFactor);
                    if (textViewSoftClipperClippingFactorCurrentValue != null) {
                        textViewSoftClipperClippingFactorCurrentValue.setText(
                                Integer.toString(clippingFactor));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_tube_distortion_gain:
                tubeDistortion = getTubeDistortion();
                if (tubeDistortion != null) {
                    float gain = Constants.TUBE_DISTORTION_MAX_GAIN *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    tubeDistortion.setGain(gain);
                    if (textViewTubeDistortionGainCurrentValue != null) {
                        textViewTubeDistortionGainCurrentValue.setText(DECIMAL_FORMAT.format(gain));
                    }
                }
                break;
            case R.id.seekbar_tube_distortion_mix:
                tubeDistortion = getTubeDistortion();
                if (tubeDistortion != null) {
                    float mix = progress / (float) DEFAULT_SEEK_BAR_STEPS;
                    tubeDistortion.setMix(mix);
                    if (textViewTubeDistortionMixCurrentValue != null) {
                        textViewTubeDistortionMixCurrentValue.setText(DECIMAL_FORMAT.format(mix));
                    }
                }
                break;
            default:
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
    private Tremolo getTremolo() {
        for (AudioEffect fx : audioEffects) {
            if (fx instanceof Tremolo) {
                return (Tremolo) fx;
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
    private TubeDistortion getTubeDistortion() {
        for (AudioEffect fx : audioEffects) {
            if (fx instanceof TubeDistortion) {
                return (TubeDistortion) fx;
            }
        }
        return null;
    }
}
