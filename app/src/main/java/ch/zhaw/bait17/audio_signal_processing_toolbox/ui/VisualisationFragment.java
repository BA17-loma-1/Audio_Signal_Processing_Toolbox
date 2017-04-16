package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.FFT;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PCMSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PostFilterSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.PreFilterSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.PCMUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.AudioView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.FrequencyView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrogramView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.TimeView;

/**
 * @author georgrem, stockan1
 */
public class VisualisationFragment extends Fragment {

    private static final String TAG = VisualisationFragment.class.getSimpleName();
    private static final String BUNDLE_ARGUMENT_AUDIOVIEWS =
            VisualisationFragment.class.getSimpleName() + ".AUDIOVIEWS";

    private FFT fft;
    private int fftResolution;
    private List<AudioView> views;

    // Creates a new fragment given a array
    // VisualisationFragment.newInstance(views);
    public static VisualisationFragment newInstance(List<AudioView> views) {
        VisualisationFragment fragment = new VisualisationFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BUNDLE_ARGUMENT_AUDIOVIEWS, (ArrayList<AudioView>) views);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fftResolution = Constants.DEFAULT_FFT_RESOLUTION;
        fft = new FFT();

        // Get back arguments
        Bundle arguments = this.getArguments();
        if (arguments.getSerializable(BUNDLE_ARGUMENT_AUDIOVIEWS) != null)
            views = (List<AudioView>) arguments.getSerializable(BUNDLE_ARGUMENT_AUDIOVIEWS);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.content_visualisation, container, false);

        // we have to wait for the drawing phase for the actual measurements
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (views != null && views.size() > 0) {
                    LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.content_visualisation);
                    int viewWidth = linearLayout.getWidth();
                    int viewHeight = linearLayout.getHeight() / views.size();
                    int margin = (int) (0.015 * viewHeight);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            viewWidth - 2 * margin, viewHeight - 2 * margin);
                    layoutParams.setMargins(margin, margin, margin, margin);

                    for (AudioView view : views) {
                        // replace identical instances
                        if (view.getParent() != null)
                            ((ViewGroup) view.getParent()).removeView(view);
                        linearLayout.addView(view, layoutParams);
                    }
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

        if (views != null) {
            for (AudioView view : views) {
                if (view instanceof SpectrogramView) {
                    ((SpectrogramView) view).setFFTResolution(fftResolution);
                }
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
        if (sampleBlock != null && views != null) {
            for (AudioView view : views) {
                if (view.isPreFilterView()) {
                    setViewParameters(view, sampleBlock);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onPostFilterSampleBlockReceived(PostFilterSampleBlock sampleBlock) {
        if (sampleBlock != null && views != null) {
            for (AudioView view : views) {
                if (!view.isPreFilterView()) {
                    setViewParameters(view, sampleBlock);
                }
            }
        }
    }

    /**
     * Sets the views to be displayed in the fragment.
     *
     * @param views     a list of views
     */
    public void setViews(List<AudioView> views) {
        this.views = views;
    }

    /**
     * Sets the view parameters: </br>
     * <ul>
     *     <li>sample rate</li>
     *     <li>channels</li>
     *     <li>PCM samples or magnitude data</li>
     * </ul>
     *
     * @param view          a view
     * @param sampleBlock   a {@code PCMSampleBlock}
     */
    private void setViewParameters(AudioView view, PCMSampleBlock sampleBlock) {
        view.setSampleRate(sampleBlock.getSampleRate());
        view.setChannels(sampleBlock.getSampleRate());

        if (view instanceof FrequencyView) {
            ((FrequencyView) view).setMagnitudes(getPowerSpectrum(sampleBlock.getSamples()));
        }

        if (view instanceof TimeView) {
            ((TimeView) view).setSamples(sampleBlock.getSamples());
        }
    }

    /**
     * <p>
     *      Computes the power spectral density of the given audio samples. <br>
     *      The power spectral density is sometimes simply called power spectrum.
     * </p>
     *
     * @param samples   a block of PCM samples
     * @return          power spectrum
     */
    private float[] getPowerSpectrum(@NonNull short[] samples) {
        // Perform FFT : Time domain -> Frequency domain
        float[] hComplex = fft.getForwardTransform(PCMUtil.short2FloatArray(samples));
        // Calculate power spectrum
        final int FFT_SIZE = hComplex.length;
        float[] hMag = new float[FFT_SIZE / 2];
        for (int i = 0; i < FFT_SIZE / 2; i++) {
            hMag[i] = (hComplex[2*i] * hComplex[2*i]) + (hComplex[(2*i) + 1] * hComplex[(2*i) + 1]);
        }
        return hMag;
    }

}
