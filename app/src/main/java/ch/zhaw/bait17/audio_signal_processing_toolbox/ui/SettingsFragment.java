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

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.delay.Flanger;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Bitcrusher;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.SoftClipper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.TubeDistortion;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation.RingModulation;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation.Tremolo;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Constants;

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
    private TextView textViewFlangerModFreqCurrentValue;
    private TextView textViewFlangerModAmplCurrentValue;
    private TextView textViewFlangerModDelayCurrentValue;
    private TextView textViewBitcrusherNormFreqCurrentValue;
    private TextView textViewBitcrusherBitDepthCurrentValue;
    private TextView textViewSoftClipperClippingFactorCurrentValue;
    private TextView textViewTubeDistortionGainCurrentValue;
    private TextView textViewTubeDistortionMixCurrentValue;
    private View view;
    private SeekBar seekBarRingModulationFrequency;
    private SeekBar seekBarTremoloModulationFrequency;
    private SeekBar seekBarTremoloModulationAmplitude;
    private SeekBar seekBarFlangerModulationFrequency;
    private SeekBar seekBarFlangerModulationAmplitude;
    private SeekBar seekBarFlangerModulationDelay;
    private SeekBar seekBarBitcrusherNormFreq;
    private SeekBar seekBarBitcrusherBitDepth;
    private SeekBar seekBarSoftClipperClippingFactor;
    private SeekBar seekBarTubeDistortionGain;
    private SeekBar seekBarTubeDistortionMix;

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
        if (view == null) {
            view = inflater.inflate(R.layout.settings_view, container, false);
            seekBarRingModulationFrequency = (SeekBar) view.findViewById(R.id.seekbar_ringmod);
            seekBarRingModulationFrequency.setMax(Constants.RING_MODULATOR_MAX_MOD_FREQUENCY);
            seekBarRingModulationFrequency.setProgress(Constants.RING_MODULATOR_DEFAULT_FREQUENCY);

            seekBarTremoloModulationFrequency = (SeekBar) view.findViewById(R.id.seekbar_tremolo_frequency);
            seekBarTremoloModulationFrequency.setMax(Constants.TREMOLO_MAX_MOD_FREQUENCY);
            seekBarTremoloModulationFrequency.setProgress(Constants.TREMOLO_DEFAULT_FREQUENCY);

            seekBarTremoloModulationAmplitude = (SeekBar) view.findViewById(R.id.seekbar_tremolo_amplitude);
            seekBarTremoloModulationAmplitude.setMax(DEFAULT_SEEK_BAR_STEPS);
            seekBarTremoloModulationAmplitude.setProgress((int) ((Constants.TREMOLO_DEFAULT_AMPLITUDE *
                    DEFAULT_SEEK_BAR_STEPS) / Constants.TREMOLO_MAX_AMPLITUDE));

            seekBarFlangerModulationFrequency = (SeekBar) view.findViewById(R.id.seekbar_flanger_frequency);
            seekBarFlangerModulationFrequency.setMax(Constants.FLANGER_MAX_MOD_FREQUENCY);
            seekBarFlangerModulationFrequency.setProgress(Constants.FLANGER_DEFAULT_FREQUENCY);

            seekBarFlangerModulationAmplitude = (SeekBar) view.findViewById(R.id.seekbar_flanger_amplitude);
            seekBarFlangerModulationAmplitude.setMax(DEFAULT_SEEK_BAR_STEPS);
            seekBarFlangerModulationAmplitude.setProgress((int) ((Constants.FLANGER_DEFAULT_AMPLITUDE *
                    DEFAULT_SEEK_BAR_STEPS) / Constants.FLANGER_MAX_AMPLITUDE));

            seekBarFlangerModulationDelay = (SeekBar) view.findViewById(R.id.seekbar_flanger_delay);
            seekBarFlangerModulationDelay.setMax(DEFAULT_SEEK_BAR_STEPS);
            seekBarFlangerModulationDelay.setProgress((int) ((Constants.FLANGER_DEFAULT_DELAY *
                    DEFAULT_SEEK_BAR_STEPS) / Constants.FLANGER_MAX_DELAY));

            seekBarBitcrusherNormFreq = (SeekBar) view.findViewById(
                    R.id.seekbar_bitcrusher_norm_freq);
            seekBarBitcrusherNormFreq.setMax(DEFAULT_SEEK_BAR_STEPS);
            seekBarBitcrusherNormFreq.setProgress((int) ((Constants.BITCRUSHER_DEFAULT_NORM_FREQUENCY *
                    DEFAULT_SEEK_BAR_STEPS) / Constants.BITCRUSHER_MAX_NORM_FREQ));

            seekBarBitcrusherBitDepth = (SeekBar) view.findViewById(
                    R.id.seekbar_bitcrusher_bit_depth);
            seekBarBitcrusherBitDepth.setMax(Constants.BITCRUSHER_MAX_BIT_DEPTH - 1);
            seekBarBitcrusherBitDepth.setProgress(Constants.BITCRUSHER_DEFAULT_BITS);

            seekBarSoftClipperClippingFactor = (SeekBar) view.findViewById(
                    R.id.seekbar_soft_clipper_clipping_factor);
            seekBarSoftClipperClippingFactor.setMax(Constants.SOFT_CLIPPER_MAX_CLIPPING_FACTOR - 1);
            seekBarSoftClipperClippingFactor.setProgress((int) ((Constants.SOFT_CLIPPER_DEFAULT_CLIPPING_FACTOR *
                    DEFAULT_SEEK_BAR_STEPS) / Constants.SOFT_CLIPPER_MAX_CLIPPING_FACTOR));

            seekBarTubeDistortionGain = (SeekBar) view.findViewById(
                    R.id.seekbar_tube_distortion_gain);
            seekBarTubeDistortionGain.setMax(DEFAULT_SEEK_BAR_STEPS);
            seekBarTubeDistortionGain.setProgress((int) ((Constants.TUBE_DISTORTION_DEFAULT_GAIN *
                    DEFAULT_SEEK_BAR_STEPS) / Constants.TUBE_DISTORTION_MAX_GAIN));

            seekBarTubeDistortionMix = (SeekBar) view.findViewById(
                    R.id.seekbar_tube_distortion_mix);
            seekBarTubeDistortionMix.setMax(DEFAULT_SEEK_BAR_STEPS);
            seekBarTubeDistortionMix.setProgress((int) ((Constants.TUBE_DISTORTION_DEFAULT_MIX *
                    DEFAULT_SEEK_BAR_STEPS) / Constants.TUBE_DISTORTION_MAX_MIX));


            textViewRingModFreqCurrentValue = (TextView) view.findViewById(
                    R.id.ringmod_freq_current_value);
            textViewRingModFreqCurrentValue.setText(String.format("%d Hz", (int) Constants.RING_MODULATOR_DEFAULT_FREQUENCY));

            textViewTremoloModFreqCurrentValue = (TextView) view.findViewById(
                    R.id.tremolo_freq_current_value);
            textViewTremoloModFreqCurrentValue.setText(String.format("%d Hz", Constants.TREMOLO_DEFAULT_FREQUENCY));

            textViewTremoloModAmplCurrentValue = (TextView) view.findViewById(
                    R.id.tremolo_ampl_current_value);
            textViewTremoloModAmplCurrentValue.setText(DECIMAL_FORMAT.format(Constants.TREMOLO_DEFAULT_AMPLITUDE));

            textViewFlangerModFreqCurrentValue = (TextView) view.findViewById(
                    R.id.flanger_freq_current_value);
            textViewFlangerModFreqCurrentValue.setText(String.format("%d Hz", Constants.FLANGER_DEFAULT_FREQUENCY));

            textViewFlangerModAmplCurrentValue = (TextView) view.findViewById(
                    R.id.flanger_ampl_current_value);
            textViewFlangerModAmplCurrentValue.setText(DECIMAL_FORMAT.format(Constants.FLANGER_DEFAULT_AMPLITUDE));

            textViewFlangerModDelayCurrentValue = (TextView) view.findViewById(
                    R.id.flanger_delay_current_value);
            textViewFlangerModDelayCurrentValue.setText(String.format("%.3f ms", Constants.FLANGER_DEFAULT_DELAY));

            textViewBitcrusherNormFreqCurrentValue = (TextView) view.findViewById(
                    R.id.bitcrusher_norm_freq_current_value);
            textViewBitcrusherNormFreqCurrentValue.setText(DECIMAL_FORMAT.format(Constants.BITCRUSHER_DEFAULT_NORM_FREQUENCY));

            textViewBitcrusherBitDepthCurrentValue = (TextView) view.findViewById(
                    R.id.bitcrusher_bit_depth_current_value);
            textViewBitcrusherBitDepthCurrentValue.setText(Integer.toString(Constants.BITCRUSHER_DEFAULT_BITS));

            textViewSoftClipperClippingFactorCurrentValue = (TextView) view.findViewById(
                    R.id.soft_clipper_clipping_factor_current_value);
            textViewSoftClipperClippingFactorCurrentValue.setText(
                    Float.toString(Constants.SOFT_CLIPPER_DEFAULT_CLIPPING_FACTOR));

            textViewTubeDistortionGainCurrentValue = (TextView) view.findViewById(
                    R.id.tube_distortion_gain_current_value);
            textViewTubeDistortionGainCurrentValue.setText(DECIMAL_FORMAT.format(Constants.TUBE_DISTORTION_DEFAULT_GAIN));

            textViewTubeDistortionMixCurrentValue = (TextView) view.findViewById(
                    R.id.tube_distortion_mix_current_value);
            textViewTubeDistortionMixCurrentValue.setText(DECIMAL_FORMAT.format(Constants.TUBE_DISTORTION_DEFAULT_MIX));
        }

        if (audioEffects != null) {
            seekBarRingModulationFrequency.setOnSeekBarChangeListener(this);
            seekBarTremoloModulationFrequency.setOnSeekBarChangeListener(this);
            seekBarTremoloModulationAmplitude.setOnSeekBarChangeListener(this);
            seekBarFlangerModulationFrequency.setOnSeekBarChangeListener(this);
            seekBarFlangerModulationAmplitude.setOnSeekBarChangeListener(this);
            seekBarFlangerModulationDelay.setOnSeekBarChangeListener(this);
            seekBarBitcrusherNormFreq.setOnSeekBarChangeListener(this);
            seekBarBitcrusherBitDepth.setOnSeekBarChangeListener(this);
            seekBarSoftClipperClippingFactor.setOnSeekBarChangeListener(this);
            seekBarTubeDistortionGain.setOnSeekBarChangeListener(this);
            seekBarTubeDistortionMix.setOnSeekBarChangeListener(this);
        }

        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TubeDistortion tubeDistortion;
        Bitcrusher bitcrusher;
        Tremolo tremolo;
        Flanger flanger;

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
                    float normAmpl = Constants.TREMOLO_MAX_AMPLITUDE *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    tremolo.setAmplitude(normAmpl);
                    if (textViewTremoloModAmplCurrentValue != null) {
                        textViewTremoloModAmplCurrentValue.setText(DECIMAL_FORMAT.format(normAmpl));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_flanger_frequency:
                flanger = getFlanger();
                if (flanger != null) {
                    flanger.setFrequencyModulation(progress);
                    if (textViewFlangerModFreqCurrentValue != null) {
                        textViewFlangerModFreqCurrentValue.setText(String.format("%d Hz", progress));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_flanger_amplitude:
                flanger = getFlanger();
                if (flanger != null) {
                    float normAmpl = Constants.FLANGER_MAX_AMPLITUDE *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    flanger.setAmplitude(normAmpl);
                    if (textViewFlangerModAmplCurrentValue != null) {
                        textViewFlangerModAmplCurrentValue.setText(DECIMAL_FORMAT.format(normAmpl));
                    }
                }
                listener.onParameterChanged(audioEffects);
                break;
            case R.id.seekbar_flanger_delay:
                flanger = getFlanger();
                if (flanger != null) {
                    double normDelay = Constants.FLANGER_MAX_DELAY *
                            (progress / (double) DEFAULT_SEEK_BAR_STEPS);
                    flanger.setMaxDelayInSamples(normDelay);
                    if (textViewFlangerModDelayCurrentValue != null) {
                        textViewFlangerModDelayCurrentValue.setText(String.format("%.3f ms", normDelay));
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
                if (softClipper != null) {
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
    private Flanger getFlanger() {
        for (AudioEffect fx : audioEffects) {
            if (fx instanceof Flanger) {
                return (Flanger) fx;
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
