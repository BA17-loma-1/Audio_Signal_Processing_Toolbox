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

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.fft.FFT;
import ch.zhaw.bait17.audio_signal_processing_toolbox.pcm.PCMSampleBlock;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.AudioView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.FrequencyView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrogramView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.TimeView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.VisualisationType;

/**
 * @author georgrem, stockan1
 */
public class VisualisationFragment extends Fragment {

    private static final String BUNDLE_ARGUMENT_AUDIOVIEWS =
            VisualisationFragment.class.getSimpleName() + ".AUDIOVIEWS";

    private FFT fft;
    private List<AudioView> views;
    private View rootView;

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

        // Get back arguments
        Bundle arguments = this.getArguments();
        if (arguments.getSerializable(BUNDLE_ARGUMENT_AUDIOVIEWS) != null)
            views = (List<AudioView>) arguments.getSerializable(BUNDLE_ARGUMENT_AUDIOVIEWS);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // set default view on startup
        if (rootView == null && views == null) {
            views = new ArrayList<>();
            AudioView spectrogramView = new SpectrogramView(ApplicationContext.getAppContext());
            spectrogramView.getInflatedView();
            spectrogramView.setVisualisationType(VisualisationType.PRE_FX);
            views.add(spectrogramView);
        }

        rootView = inflater.inflate(R.layout.content_visualisation, container, false);

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

    @Override
    public void onStart() {
        super.onStart();
        // Register to EventBus
        EventBus.getDefault().register(this);

        fft = new FFT(Constants.DEFAULT_FFT_RESOLUTION);
        if (views != null) {
            for (AudioView view : views) {
                if (view instanceof SpectrogramView) {
                    ((SpectrogramView) view).setFFTResolution(Constants.DEFAULT_FFT_RESOLUTION);
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregister from EventBus
        EventBus.getDefault().unregister(this);
    }

    /**
     * EventBus subscriber - receives {@code PCMSampleBlock}s from the publisher
     * {@link ch.zhaw.bait17.audio_signal_processing_toolbox.player.AudioPlayer}
     *
     * @param sampleBlock a {@code PCMSampleBlock}
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onPCMSampleBlockReceived(PCMSampleBlock sampleBlock) {
        if (sampleBlock != null && views != null) {
            for (AudioView view : views) {
                if (view != null) {
                    setAudioViewParameters(view, sampleBlock);
                }
            }
        }
    }

    /**
     * Sets the views to be displayed in the fragment.
     *
     * @param views a list of views
     */
    public void setViews(List<AudioView> views) {
        this.views = views;
    }

    /**
     * <p>
     * Sets the {@code AudioView} parameters: </br>
     * <ul>
     * <li>sample rate</li>
     * <li>channels</li>
     * </ul>
     * </p>
     * <p>
     * Furthermore this method sets the samples or power spectral density according
     * to the type of {code view}.
     * </p>
     *
     * @param view        an {@code AudioView}
     * @param sampleBlock a {@code PCMSampleBlock}
     */
    private void setAudioViewParameters(@NonNull AudioView view, @NonNull PCMSampleBlock sampleBlock) {
        view.setSampleRate(sampleBlock.getSampleRate());
        view.setChannels(sampleBlock.getChannels());
        if (view instanceof TimeView) {
            setTimeViewParameters((TimeView) view, sampleBlock);
        }
        if (view instanceof FrequencyView) {
            setFrequencyViewParameters((FrequencyView) view, sampleBlock);
        }
    }

    /**
     * Sets the samples to the {@code TimeView}.
     *
     * @param timeView    a {@code TimeView}
     * @param sampleBlock a {@code SampleBlock}
     */
    private void setTimeViewParameters(@NonNull TimeView timeView,
                                       @NonNull PCMSampleBlock sampleBlock) {
        VisualisationType visualisationType = timeView.getVisualisationType();
        switch (visualisationType) {
            case PRE_FX:
                timeView.setSamples(sampleBlock.getPreFilterSamples(), new short[0]);
                break;
            case POST_FX:
                timeView.setSamples(new short[0], sampleBlock.getPostFilterSamples());
                break;
            default:
                timeView.setSamples(sampleBlock.getPreFilterSamples(),
                        sampleBlock.getPostFilterSamples());
        }
    }

    /**
     * <p>
     * Transforms the sample data into the frequency domain and sets the computed
     * power spectral density to the {@code FrequencyView}.
     * </p>
     * <p>
     * The FFT resolution is controlled via the {@code FFT} instance.
     * </p>
     *
     * @param frequencyView a {@code FrequencyView}
     * @param sampleBlock   a {@code SampleBlock}
     */
    private void setFrequencyViewParameters(@NonNull FrequencyView frequencyView,
                                            @NonNull PCMSampleBlock sampleBlock) {
        VisualisationType visualisationType = frequencyView.getVisualisationType();
        switch (visualisationType) {
            case PRE_FX:
                frequencyView.setSpectralDensity(
                        fft.getPowerSpectrum(sampleBlock.getPreFilterSamples(), sampleBlock.getChannels()),
                        new float[0]);
                break;
            case POST_FX:
                frequencyView.setSpectralDensity(
                        new float[0],
                        fft.getPowerSpectrum(sampleBlock.getPostFilterSamples(), sampleBlock.getChannels()));
                break;
            default:
                frequencyView.setSpectralDensity(
                        fft.getPowerSpectrum(sampleBlock.getPreFilterSamples(), sampleBlock.getChannels()),
                        fft.getPowerSpectrum(sampleBlock.getPostFilterSamples(), sampleBlock.getChannels()));
        }
    }
}
