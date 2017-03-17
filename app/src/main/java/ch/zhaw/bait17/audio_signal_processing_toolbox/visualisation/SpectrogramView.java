package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author georgrem, stockan1
 */

public class SpectrogramView extends View {

    private static final String TAG = SpectrumView.class.getSimpleName();
    private static final int SPECTROGRAM_LENGTH = 8;

    private Context context;
    private int sampleRate;
    private int height, width;
    private float centerY;
    private Rect drawRect;
    private final Queue<PowerSpectrum> spectrogram = new ArrayBlockingQueue<>(SPECTROGRAM_LENGTH);

    public SpectrogramView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SpectrogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SpectrogramView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(final Context context, AttributeSet attrs, int defStyle) {
        this.context = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        centerY = height / 2f;
        drawRect = new Rect(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    public void setSamples(short[] samples) {
        if (samples != null) {
            if (spectrogram.size() == SPECTROGRAM_LENGTH) {
                // Remove the head of the queue.
                spectrogram.remove();
            }
            spectrogram.add(new PowerSpectrum(samples));
            onSamplesChanged();
        }
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    private void onSamplesChanged() {
        postInvalidate();
    }

}
