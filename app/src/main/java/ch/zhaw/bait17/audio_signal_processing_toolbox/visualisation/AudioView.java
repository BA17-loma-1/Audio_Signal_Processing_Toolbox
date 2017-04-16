package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * All audio data visualisations must extend this class.
 *
 * @author georgrem, stockan1
 */

public abstract class AudioView extends View {

    private int sampleRate, channels;
    private boolean preFilterView = true;

    public AudioView(Context context) {
        super(context);
    }

    public AudioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Returns the sample rate.
     *
     * @return
     */
    protected int getSampleRate() {
        return sampleRate;
    }

    /**
     * Sets the sample rate.
     *
     * @param sampleRate
     */
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * Returns the number of audio channels.
     *
     * @return
     */
    protected int getChannels() {
        return channels;
    }

    /**
     * Sets the number of channels.
     *
     * @param channels
     */
    public void setChannels(int channels) {
        this.channels = channels;
    }

    /**
     * Configures the view to show visualisation prior to filtering.
     *
     * @param preFilterView true defines the view as a pre-audio_effect_view view
     */
    public void setPreFilterView(boolean preFilterView) {
        this.preFilterView = preFilterView;
    }

    /**
     * Returns true if the view is defined as a pre-audio_effect_view view.
     *
     * @return
     */
    public boolean isPreFilterView() {
        return preFilterView;
    }


    /**
     * "Inflating" a view means taking the layout XML and parsing it to create the view object
     *
     * @return AudioView
     */
    public abstract AudioView getInflatedView();

}
