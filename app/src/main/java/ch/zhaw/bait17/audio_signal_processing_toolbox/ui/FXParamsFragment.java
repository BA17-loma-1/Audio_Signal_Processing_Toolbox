package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.delay.Flanger;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Bitcrusher;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.SoftClipper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.TubeDistortion;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Waveshaper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FIRCombFilter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation.RingModulation;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation.Tremolo;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Constants;

/**
 * @author georgrem, stockan1
 */

public class FXParamsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static final int DEFAULT_SEEK_BAR_STEPS = 100;
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private View view;

    private TextView textViewGainCurrentValue;
    private TextView textViewFIRCombFilterDelayCurrentValue;
    private TextView textViewRingModFreqCurrentValue;
    private TextView textViewTremoloModFreqCurrentValue;
    private TextView textViewTremoloModAmplCurrentValue;
    private TextView textViewFlangerRateCurrentValue;
    private TextView textViewFlangerModAmplCurrentValue;
    private TextView textViewFlangerModDelayCurrentValue;
    private TextView textViewBitcrusherNormFreqCurrentValue;
    private TextView textViewBitcrusherBitDepthCurrentValue;
    private TextView textViewSoftClipperClippingFactorCurrentValue;
    private TextView textViewWaveshaperThresholdCurrentValue;
    private TextView textViewTubeDistortionGainCurrentValue;
    private TextView textViewTubeDistortionMixCurrentValue;

    private SeekBar seekBarGain;
    private SeekBar seekBarFIRCombFilterDelay;
    private SeekBar seekBarRingModulationFrequency;
    private SeekBar seekBarTremoloModulationFrequency;
    private SeekBar seekBarTremoloModulationAmplitude;
    private SeekBar seekBarFlangerRate;
    private SeekBar seekBarFlangerModulationAmplitude;
    private SeekBar seekBarFlangerModulationDelay;
    private SeekBar seekBarBitcrusherNormFreq;
    private SeekBar seekBarBitcrusherBitDepth;
    private SeekBar seekBarSoftClipperClippingFactor;
    private SeekBar seekBarWaveshaperThreshold;
    private SeekBar seekBarTubeDistortionGain;
    private SeekBar seekBarTubeDistortionMix;

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            super.onAttach(context);
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fx_params_view, container, false);

            seekBarGain = (SeekBar) view.findViewById(R.id.seekbar_gain);
            seekBarGain.setMax(DEFAULT_SEEK_BAR_STEPS);
            int defaultGain = (int) (Constants.GAIN_DEFAULT / Constants.GAIN_MAX * DEFAULT_SEEK_BAR_STEPS);
            seekBarGain.setProgress(defaultGain);

            seekBarFIRCombFilterDelay = (SeekBar) view.findViewById(R.id.seekbar_fir_comb_filter);
            seekBarFIRCombFilterDelay.setMax(DEFAULT_SEEK_BAR_STEPS * 10);
            seekBarFIRCombFilterDelay.setProgress((int) (Constants.FIR_COMB_FILTER_DEFAULT_DELAY *
                    (DEFAULT_SEEK_BAR_STEPS * 10) / Constants.FIR_COMB_FILTER_MAX_DELAY));

            seekBarRingModulationFrequency = (SeekBar) view.findViewById(R.id.seekbar_ringmod);
            seekBarRingModulationFrequency.setMax(Constants.RING_MODULATOR_MAX_MOD_FREQUENCY);
            seekBarRingModulationFrequency.setProgress(Constants.RING_MODULATOR_DEFAULT_FREQUENCY);

            seekBarTremoloModulationFrequency = (SeekBar) view.findViewById(R.id.seekbar_tremolo_frequency);
            seekBarTremoloModulationFrequency.setMax(DEFAULT_SEEK_BAR_STEPS);
            seekBarTremoloModulationFrequency.setProgress((int) ((Constants.TREMOLO_DEFAULT_MOD_FREQUENCY *
                   DEFAULT_SEEK_BAR_STEPS) / Constants.TREMOLO_MAX_MOD_FREQUENCY));

            seekBarTremoloModulationAmplitude = (SeekBar) view.findViewById(R.id.seekbar_tremolo_amplitude);
            seekBarTremoloModulationAmplitude.setMax(DEFAULT_SEEK_BAR_STEPS);
            seekBarTremoloModulationAmplitude.setProgress((int) ((Constants.TREMOLO_DEFAULT_AMPLITUDE *
                    DEFAULT_SEEK_BAR_STEPS) / Constants.TREMOLO_MAX_AMPLITUDE));

            seekBarFlangerRate = (SeekBar) view.findViewById(R.id.seekbar_flanger_rate);
            seekBarFlangerRate.setMax(DEFAULT_SEEK_BAR_STEPS);
            seekBarFlangerRate.setProgress((int) ((Constants.FLANGER_DEFAULT_RATE *
                    DEFAULT_SEEK_BAR_STEPS) / Constants.FLANGER_MAX_RATE));

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

            seekBarWaveshaperThreshold = (SeekBar) view.findViewById(
                    R.id.seekbar_waveshaper_threshold);
            seekBarWaveshaperThreshold.setMax(Constants.WAVESHAPER_MAX_THRESHOLD + 1);
            seekBarWaveshaperThreshold.setProgress(Constants.WAVESHAPER_DEFAULT_THRESHOLD);

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


            textViewGainCurrentValue = (TextView) view.findViewById(
                    R.id.linear_gain_current_value);
            textViewGainCurrentValue.setText(DECIMAL_FORMAT.format(Constants.GAIN_DEFAULT));

            textViewFIRCombFilterDelayCurrentValue = (TextView) view.findViewById(
                    R.id.fir_comb_filter_delay_current_value);
            textViewFIRCombFilterDelayCurrentValue.setText(String.format("%.3f s",
                    Constants.FIR_COMB_FILTER_DEFAULT_DELAY));

            textViewRingModFreqCurrentValue = (TextView) view.findViewById(
                    R.id.ringmod_freq_current_value);
            textViewRingModFreqCurrentValue.setText(String.format("%d Hz",
                    (int) Constants.RING_MODULATOR_DEFAULT_FREQUENCY));

            textViewTremoloModFreqCurrentValue = (TextView) view.findViewById(
                    R.id.tremolo_freq_current_value);
            textViewTremoloModFreqCurrentValue.setText(String.format("%1.2f Hz",
                    Constants.TREMOLO_DEFAULT_MOD_FREQUENCY));

            textViewTremoloModAmplCurrentValue = (TextView) view.findViewById(
                    R.id.tremolo_ampl_current_value);
            textViewTremoloModAmplCurrentValue.setText(DECIMAL_FORMAT.format(
                    Constants.TREMOLO_DEFAULT_AMPLITUDE));

            textViewFlangerRateCurrentValue = (TextView) view.findViewById(
                    R.id.flanger_rate_current_value);
            textViewFlangerRateCurrentValue.setText(String.format("%1.2f Hz",
                    Constants.FLANGER_DEFAULT_RATE));

            textViewFlangerModAmplCurrentValue = (TextView) view.findViewById(
                    R.id.flanger_ampl_current_value);
            textViewFlangerModAmplCurrentValue.setText(DECIMAL_FORMAT.format(
                    Constants.FLANGER_DEFAULT_AMPLITUDE));

            textViewFlangerModDelayCurrentValue = (TextView) view.findViewById(
                    R.id.flanger_delay_current_value);
            textViewFlangerModDelayCurrentValue.setText(String.format("%.3f s",
                    Constants.FLANGER_DEFAULT_DELAY));

            textViewBitcrusherNormFreqCurrentValue = (TextView) view.findViewById(
                    R.id.bitcrusher_norm_freq_current_value);
            textViewBitcrusherNormFreqCurrentValue.setText(DECIMAL_FORMAT.format(
                    Constants.BITCRUSHER_DEFAULT_NORM_FREQUENCY));

            textViewBitcrusherBitDepthCurrentValue = (TextView) view.findViewById(
                    R.id.bitcrusher_bit_depth_current_value);
            textViewBitcrusherBitDepthCurrentValue.setText(Integer.toString(
                    Constants.BITCRUSHER_DEFAULT_BITS));

            textViewSoftClipperClippingFactorCurrentValue = (TextView) view.findViewById(
                    R.id.soft_clipper_clipping_factor_current_value);
            textViewSoftClipperClippingFactorCurrentValue.setText(
                    Float.toString(Constants.SOFT_CLIPPER_DEFAULT_CLIPPING_FACTOR));

            textViewWaveshaperThresholdCurrentValue = (TextView) view.findViewById(
                    R.id.waveshaper_threshold_current_value);
            textViewWaveshaperThresholdCurrentValue.setText(
                    Float.toString(Constants.WAVESHAPER_DEFAULT_THRESHOLD));

            textViewTubeDistortionGainCurrentValue = (TextView) view.findViewById(
                    R.id.tube_distortion_gain_current_value);
            textViewTubeDistortionGainCurrentValue.setText(DECIMAL_FORMAT.format(
                    Constants.TUBE_DISTORTION_DEFAULT_GAIN));

            textViewTubeDistortionMixCurrentValue = (TextView) view.findViewById(
                    R.id.tube_distortion_mix_current_value);
            textViewTubeDistortionMixCurrentValue.setText(DECIMAL_FORMAT.format(
                    Constants.TUBE_DISTORTION_DEFAULT_MIX));
        }

        MainActivity activity = (MainActivity) getActivity();
        if (activity.getAudioEffects() != null && activity.getAudioEffects().size() > 0) {
            seekBarGain.setOnSeekBarChangeListener(this);
            seekBarFIRCombFilterDelay.setOnSeekBarChangeListener(this);
            seekBarRingModulationFrequency.setOnSeekBarChangeListener(this);
            seekBarTremoloModulationFrequency.setOnSeekBarChangeListener(this);
            seekBarTremoloModulationAmplitude.setOnSeekBarChangeListener(this);
            seekBarFlangerRate.setOnSeekBarChangeListener(this);
            seekBarFlangerModulationAmplitude.setOnSeekBarChangeListener(this);
            seekBarFlangerModulationDelay.setOnSeekBarChangeListener(this);
            seekBarBitcrusherNormFreq.setOnSeekBarChangeListener(this);
            seekBarBitcrusherBitDepth.setOnSeekBarChangeListener(this);
            seekBarSoftClipperClippingFactor.setOnSeekBarChangeListener(this);
            seekBarWaveshaperThreshold.setOnSeekBarChangeListener(this);
            seekBarTubeDistortionGain.setOnSeekBarChangeListener(this);
            seekBarTubeDistortionMix.setOnSeekBarChangeListener(this);
        }

        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        MainActivity activity = (MainActivity) getActivity();
        Tremolo tremolo;
        Flanger flanger;
        Bitcrusher bitcrusher;
        TubeDistortion tubeDistortion;

        switch (seekBar.getId()) {
            case R.id.seekbar_gain:
                float gain = Constants.GAIN_MAX * (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                activity.setGain(gain);
                if (textViewGainCurrentValue != null) {
                    textViewGainCurrentValue.setText(DECIMAL_FORMAT.format(gain));
                }
                break;
            case R.id.seekbar_fir_comb_filter:
                FIRCombFilter firCombFilter = (FIRCombFilter) activity.getAudioEffectFromType(FIRCombFilter.class);
                if (firCombFilter != null) {
                    float delay = Constants.FIR_COMB_FILTER_MAX_DELAY *
                            (progress / (float) (DEFAULT_SEEK_BAR_STEPS * 10));
                    firCombFilter.setDelay(delay);
                    if (textViewFIRCombFilterDelayCurrentValue != null) {
                        textViewFIRCombFilterDelayCurrentValue.setText(String.format("%.3f s", delay));
                    }
                }
                break;
            case R.id.seekbar_ringmod:
                RingModulation ringMod = (RingModulation) activity.getAudioEffectFromType(RingModulation.class);
                if (ringMod != null) {
                    ringMod.setModulationFrequency(progress);
                    if (textViewRingModFreqCurrentValue != null) {
                        textViewRingModFreqCurrentValue.setText(String.format("%d Hz", progress));
                    }
                }
                break;
            case R.id.seekbar_tremolo_frequency:
                tremolo = (Tremolo) activity.getAudioEffectFromType(Tremolo.class);
                if (tremolo != null) {
                    float modFreq = Constants.TREMOLO_MAX_MOD_FREQUENCY *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    tremolo.setModulationFrequency(modFreq);
                    if (textViewTremoloModFreqCurrentValue != null) {
                        textViewTremoloModFreqCurrentValue.setText(String.format("%1.2f Hz", modFreq));
                    }
                }
                break;
            case R.id.seekbar_tremolo_amplitude:
                tremolo = (Tremolo) activity.getAudioEffectFromType(Tremolo.class);
                if (tremolo != null) {
                    float normAmpl = Constants.TREMOLO_MAX_AMPLITUDE *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    tremolo.setAmplitude(normAmpl);
                    if (textViewTremoloModAmplCurrentValue != null) {
                        textViewTremoloModAmplCurrentValue.setText(DECIMAL_FORMAT.format(normAmpl));
                    }
                }
                break;
            case R.id.seekbar_flanger_rate:
                flanger = (Flanger) activity.getAudioEffectFromType(Flanger.class);
                if (flanger != null) {
                    float rate = Constants.FLANGER_MAX_RATE *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    flanger.setRate(rate);
                    if (textViewFlangerRateCurrentValue != null) {
                        textViewFlangerRateCurrentValue.setText(String.format("%1.2f Hz", rate));
                    }
                }
                break;
            case R.id.seekbar_flanger_amplitude:
                flanger = (Flanger) activity.getAudioEffectFromType(Flanger.class);
                if (flanger != null) {
                    float normAmpl = Constants.FLANGER_MAX_AMPLITUDE *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    flanger.setAmplitude(normAmpl);
                    if (textViewFlangerModAmplCurrentValue != null) {
                        textViewFlangerModAmplCurrentValue.setText(DECIMAL_FORMAT.format(normAmpl));
                    }
                }
                break;
            case R.id.seekbar_flanger_delay:
                flanger = (Flanger) activity.getAudioEffectFromType(Flanger.class);
                if (flanger != null) {
                    float normDelay = (float) (Constants.FLANGER_MAX_DELAY * (progress / (double) DEFAULT_SEEK_BAR_STEPS));
                    flanger.setMaxDelay(normDelay);
                    if (textViewFlangerModDelayCurrentValue != null) {
                        textViewFlangerModDelayCurrentValue.setText(String.format("%.3f s", normDelay));
                    }
                }
                break;
            case R.id.seekbar_bitcrusher_norm_freq:
                bitcrusher = (Bitcrusher) activity.getAudioEffectFromType(Bitcrusher.class);
                if (bitcrusher != null) {
                    float normFreq = Constants.BITCRUSHER_MAX_NORM_FREQ *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    bitcrusher.setNormFrequency(normFreq);
                    if (textViewBitcrusherNormFreqCurrentValue != null) {
                        textViewBitcrusherNormFreqCurrentValue.setText(DECIMAL_FORMAT.format(normFreq));
                    }
                }
                break;
            case R.id.seekbar_bitcrusher_bit_depth:
                bitcrusher = (Bitcrusher) activity.getAudioEffectFromType(Bitcrusher.class);
                if (bitcrusher != null) {
                    int bits = progress + 1;
                    bitcrusher.setBits(bits);
                    if (textViewBitcrusherBitDepthCurrentValue != null) {
                        textViewBitcrusherBitDepthCurrentValue.setText(Integer.toString(bits));
                    }
                }
                break;
            case R.id.seekbar_soft_clipper_clipping_factor:
                SoftClipper softClipper = (SoftClipper) activity.getAudioEffectFromType(SoftClipper.class);
                if (softClipper != null) {
                    int clippingFactor = progress + 1;
                    softClipper.setClippingFactor((float) clippingFactor);
                    if (textViewSoftClipperClippingFactorCurrentValue != null) {
                        textViewSoftClipperClippingFactorCurrentValue.setText(
                                Integer.toString(clippingFactor));
                    }
                }
                break;
            case R.id.seekbar_waveshaper_threshold:
                Waveshaper waveshaper = (Waveshaper) activity.getAudioEffectFromType(Waveshaper.class);
                if (waveshaper != null) {
                    float threshold = progress + 1.0f;
                    waveshaper.setThreshold(threshold);
                    if (textViewWaveshaperThresholdCurrentValue != null) {
                        textViewWaveshaperThresholdCurrentValue.setText(DECIMAL_FORMAT.format(threshold));
                    }
                }
            case R.id.seekbar_tube_distortion_gain:
                tubeDistortion = (TubeDistortion) activity.getAudioEffectFromType(TubeDistortion.class);
                if (tubeDistortion != null) {
                    float distortionGain = Constants.TUBE_DISTORTION_MAX_GAIN *
                            (progress / (float) DEFAULT_SEEK_BAR_STEPS);
                    tubeDistortion.setGain(distortionGain);
                    if (textViewTubeDistortionGainCurrentValue != null) {
                        textViewTubeDistortionGainCurrentValue.setText(DECIMAL_FORMAT.format(distortionGain));
                    }
                }
                break;
            case R.id.seekbar_tube_distortion_mix:
                tubeDistortion = (TubeDistortion) activity.getAudioEffectFromType(TubeDistortion.class);
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
}