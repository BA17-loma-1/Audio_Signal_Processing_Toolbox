package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * All audio data visualisations must extend this class.
 *
 * @author georgrem, stockan1
 */

public abstract class AudioView extends View {

    private VisualisationType visualisationType = VisualisationType.PRE_FX;
    private int sampleRate, channels;

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
     * @return      sample rate
     */
    protected int getSampleRate() {
        return sampleRate;
    }

    /**
     * Sets the sample rate.
     *
     * @param sampleRate    sample rate
     */
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * Returns the number of audio channels.
     *
     * @return      channels
     */
    protected int getChannels() {
        return channels;
    }

    /**
     * Sets the number of channels.
     *
     * @param channels      channels
     */
    public void setChannels(int channels) {
        this.channels = channels;
    }

    /**
     * Returns the {@code VisualisationType} of this view.
     * See {@link VisualisationType}
     *
     * @return      type of visualisation
     */
    public VisualisationType getVisualisationType() {
        return visualisationType;
    }

    /**
     * Sets the {@code VisualisationType} of this view.
     *
     * @param visualisationType     the type of visualisation
     */
    public void setVisualisationType(@NonNull VisualisationType visualisationType) {
        this.visualisationType = visualisationType;
    }

    /**
     * "Inflating" a view means taking the layout XML and parsing it to create the view object
     *
     * @return AudioView
     */
    public AudioView getInflatedView() {
        return null;
    };

}
