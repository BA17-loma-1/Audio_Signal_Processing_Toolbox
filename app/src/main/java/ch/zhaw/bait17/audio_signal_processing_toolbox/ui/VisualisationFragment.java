package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PostFilterSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PreFilterSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Util;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.AudioView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.LineSpectrumView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.PowerSpectrum;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrogramView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrumView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.WaveformView;

public class VisualisationFragment extends Fragment {

    private static final String KEY_AUDIOVIEW_MAP = VisualisationFragment.class.getSimpleName() + ".AUDIOVIEW_MAP";

    private Map<ViewPosition, AudioView> views;
    private int fftResolution;
    private CircularFifoQueue<short[]> trunkBuffer;

    private enum ViewPosition {
        BOTTOM("bottom"), TOP("top");

        private String position;

        ViewPosition(String position) {
            this.position = position;
        }

        @Override
        public String toString() {
            return position;
        }
    }

    // Creates a new fragment given a map
    // VisualisationFragment.newInstance(views);
    public static VisualisationFragment newInstance(HashMap<ViewPosition, AudioView> views) {
        VisualisationFragment fragment = new VisualisationFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_AUDIOVIEW_MAP, views);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        // Bundle args = this.getArguments();
        // if(args.getSerializable(KEY_AUDIOVIEW_MAP) != null)
        //     views = (HashMap<ViewPosition, AudioView>) args.getSerializable(KEY_AUDIOVIEW_MAP);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fftResolution = Constants.DEFAULT_FFT_RESOLUTION;
        trunkBuffer = new CircularFifoQueue<>();

        View rootView = inflater.inflate(R.layout.content_visualisation, container, false);
        SpectrogramView topView = (SpectrogramView) rootView.findViewById(R.id.view_top);
        //WaveformView bottomView = (WaveformView) rootView.findViewById(R.id.view_bottom);
        LineSpectrumView bottomView = (LineSpectrumView) rootView.findViewById(R.id.view_bottom2);

        if (topView != null) {
            addView(topView, ViewPosition.TOP);
        }
        if (bottomView != null) {
            addView(bottomView, ViewPosition.BOTTOM);
        }

        return rootView;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //TextView frequency = (TextView) spectrogramView.findViewById(R.id.textview_spectrogram_header);
        //frequency.setText("FFT size: " + fftResolution);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Register for event bus
        EventBus.getDefault().register(this);

        for (AudioView view : views.values()) {
            if (view instanceof SpectrogramView) {
                ((SpectrogramView) view).setFFTWindowSize(fftResolution);
            }
        }
    }

    @Override
    public void onStop() {
        // Unregister from event bus
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onPreFilterSampleBlockReceived(PreFilterSampleBlock sampleBlock) {
        if (sampleBlock != null) {
            setSampleRate(sampleBlock.getSampleRate());
            setChannels(sampleBlock.getSampleRate());

            WaveformView waveformView = getWaveformView();
            if (waveformView != null) {
                waveformView.setSamples(sampleBlock.getSamples());
            }

            LineSpectrumView lineSpectrumView = getLineSpectrumView();
            if (lineSpectrumView != null) {
                lineSpectrumView.setSamples(sampleBlock.getSamples());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onPostFilterSampleBlockReceived(PostFilterSampleBlock sampleBlock) {
        if (sampleBlock != null) {
            setSampleRate(sampleBlock.getSampleRate());
            setChannels(sampleBlock.getSampleRate());
            short[] samples = sampleBlock.getSamples();
            int trunkSize = Util.gcd(samples.length, fftResolution);
            if (trunkSize == 1) {
                trunkBuffer.offer(samples);
            } else {
                truncate(samples, trunkSize);
            }

            boolean enoughData = false;
            int sampleBlockSize = 0;
            short[] trunk = null;
            Iterator<short[]> iter = trunkBuffer.iterator();
            while (iter.hasNext() && !enoughData) {
                sampleBlockSize += iter.next().length;
                if (sampleBlockSize >= fftResolution) {
                    enoughData = true;
                }
            }

            if (enoughData) {
                sendMagnitudesToSpectrogramView();
            }
        }
    }

    private void sendMagnitudesToSpectrogramView() {
        SpectrogramView spectrogramView = getSpectrogramView();
        if (spectrogramView != null) {
            int totalTrunkSize = 0;
            short[] trunk = null;
            short[] fftBuffer = new short[0];
            while ((trunk = trunkBuffer.poll()) != null && totalTrunkSize < fftResolution) {
                totalTrunkSize += trunk.length;
                short[] temp = new short[fftBuffer.length];
                // fftBuffer samples are now stored in temp
                System.arraycopy(fftBuffer, 0, temp, 0, fftBuffer.length);
                fftBuffer = new short[temp.length + trunk.length];
                // Copy back to fftBuffer
                System.arraycopy(temp, 0, fftBuffer, 0, temp.length);
                // Append new trunk of samples
                System.arraycopy(trunk, 0, fftBuffer, temp.length, trunk.length);
            }

            if (fftBuffer != null) {
                // long start = System.nanoTime();
                PowerSpectrum magnitudes = getTransform(fftBuffer);
                // long end = System.nanoTime();
                // Log.d(TAG, "FFT computation [ms]: " + (end - start) / 1000000);
                spectrogramView.setMagnitudes(magnitudes.getPowerSpectrum());
            }
        }
    }

    private void truncate(short[] samples, int trunkSize) {
        // Divide into small trunks of equal length
        for (int i = 0; i < samples.length / trunkSize; i++) {
            trunkBuffer.offer(Arrays.copyOfRange(samples, i * trunkSize, (i * trunkSize) + trunkSize));
        }
    }

    private PowerSpectrum getTransform(@NonNull short[] samples) {
        return new PowerSpectrum(samples);
    }

    private void addView(@NonNull AudioView view, @NonNull ViewPosition position) {
        if (views == null) {
            views = new HashMap<>();
        }
        views.put(position, view);
    }

    private void setSampleRate(int sampleRate) {
        for (AudioView view : views.values()) {
            view.setSampleRate(sampleRate);
        }
    }

    private void setChannels(int channels) {
        for (AudioView view : views.values()) {
            view.setChannels(channels);
        }
    }

    @Nullable
    private SpectrogramView getSpectrogramView() {
        SpectrogramView spectrogramView = null;
        for (AudioView view : views.values()) {
            if (view instanceof SpectrogramView) {
                spectrogramView = (SpectrogramView) view;
                break;
            }
        }
        return spectrogramView;
    }

    @Nullable
    private SpectrumView getSpectrumView() {
        SpectrumView spectrumView = null;
        for (AudioView view : views.values()) {
            if (view instanceof SpectrumView) {
                spectrumView = (SpectrumView) view;
                break;
            }
        }
        return spectrumView;
    }

    @Nullable
    private WaveformView getWaveformView() {
        WaveformView waveformView = null;
        for (AudioView view : views.values()) {
            if (view instanceof WaveformView) {
                waveformView = (WaveformView) view;
                break;
            }
        }
        return waveformView;
    }

    @Nullable
    private LineSpectrumView getLineSpectrumView() {
        LineSpectrumView lineSpectrumView = null;
        for (AudioView view : views.values()) {
            if (view instanceof LineSpectrumView) {
                lineSpectrumView = (LineSpectrumView) view;
                break;
            }
        }
        return lineSpectrumView;
    }

}
