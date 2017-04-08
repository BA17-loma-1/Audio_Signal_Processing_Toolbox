package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import java.util.Arrays;
import java.util.Iterator;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PCMSampleBlock;
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

    private static final String TAG = VisualisationFragment.class.getSimpleName();
    private static final String KEY_AUDIOVIEWS = VisualisationFragment.class.getSimpleName() + ".AUDIOVIEWS";

    private int fftResolution;
    private CircularFifoQueue<short[]> trunkBuffer;
    private AudioView[] views;


    // Creates a new fragment given a array
    // VisualisationFragment.newInstance(views);
    public static VisualisationFragment newInstance(AudioView[] views) {
        VisualisationFragment fragment = new VisualisationFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_AUDIOVIEWS, views);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        Bundle args = this.getArguments();
        if (args.getSerializable(KEY_AUDIOVIEWS) != null)
            views = (AudioView[]) args.getSerializable(KEY_AUDIOVIEWS);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fftResolution = Constants.DEFAULT_FFT_RESOLUTION;
        trunkBuffer = new CircularFifoQueue<>();

        final View rootView = inflater.inflate(R.layout.content_visualisation, container, false);

        // we have to wait for the drawing phase for the actual measurements
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.content_visualisation);
                int viewHeight = linearLayout.getHeight() / 2;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, viewHeight);

                for (AudioView view : views) {
                    // replace identical instances
                    if (view.getParent() != null)
                        ((ViewGroup) view.getParent()).removeView(view);
                    linearLayout.addView(view, layoutParams);
                }

            }
        });

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

        for (AudioView view : views) {
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
            for (AudioView view : views) {
                if (view.isPreFilterView()) {
                    setViewParameters(view, sampleBlock);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onPostFilterSampleBlockReceived(PostFilterSampleBlock sampleBlock) {
        if (sampleBlock != null) {
            for (AudioView view : views) {
                if (!view.isPreFilterView()) {
                    setViewParameters(view, sampleBlock);
                }
            }
        }
    }

    private void setViewParameters(AudioView view, PCMSampleBlock sampleBlock) {
        view.setSampleRate(sampleBlock.getSampleRate());
        view.setChannels(sampleBlock.getSampleRate());
        if (view instanceof LineSpectrumView) {
            LineSpectrumView lineSpectrumView = (LineSpectrumView) view;
            lineSpectrumView.setSamples(sampleBlock.getSamples());
        }
        if (view instanceof SpectrogramView) {
            SpectrogramView spectrogramView = (SpectrogramView) view;
            initSpectrogramView(sampleBlock, spectrogramView);
        }
        if (view instanceof SpectrumView) {
            SpectrumView spectrumView = (SpectrumView) view;
            spectrumView.setSamples(sampleBlock.getSamples());
        }
        if (view instanceof WaveformView) {
            WaveformView waveformView = (WaveformView) view;
            waveformView.setSamples(sampleBlock.getSamples());
        }
    }

    private void initSpectrogramView(PCMSampleBlock sampleBlock, SpectrogramView spectrogramView) {
        short[] samples = sampleBlock.getSamples();
        int trunkSize = Util.gcd(samples.length, fftResolution);
        if (trunkSize == 1) {
            trunkBuffer.offer(samples);
        } else {
            truncate(samples, trunkSize);
        }

        boolean enoughData = false;
        int sampleBlockSize = 0;
        Iterator<short[]> iter = trunkBuffer.iterator();
        while (iter.hasNext() && !enoughData) {
            sampleBlockSize += iter.next().length;
            if (sampleBlockSize >= fftResolution) {
                enoughData = true;
            }
        }

        if (enoughData) {
            sendMagnitudesToSpectrogramView(spectrogramView);
        }
    }

    private void sendMagnitudesToSpectrogramView(SpectrogramView spectrogramView) {
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

    public void setViews(AudioView[] views) {
        this.views = views;
    }
}
