package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;

/**
 * TODO: document your custom view class.
 */
public class WaveformView extends View {
    private TextPaint textPaint;
    private Paint strokePaint, fillPaint, markerPaint;

    // Used in draw
    private Rect drawRect;

    private int width, height;
    private float xStep, centerY;
    private int mAudioLength, sampleRate, channels;
    private float[] samples;
    private float[] waveformPoints;

    public WaveformView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WaveformView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.WaveformView, defStyle, 0);

        float strokeThickness = a.getFloat(R.styleable.WaveformView_waveformStrokeThickness, 2f);
        int strokeColor = a.getColor(R.styleable.WaveformView_waveformColor,
                ContextCompat.getColor(context, R.color.default_waveform));
        int mFillColor = a.getColor(R.styleable.WaveformView_waveformFillColor,
                ContextCompat.getColor(context, R.color.default_waveformFill));
        int mMarkerColor = a.getColor(R.styleable.WaveformView_playbackIndicatorColor,
                ContextCompat.getColor(context, R.color.default_playback_indicator));
        int mTextColor = a.getColor(R.styleable.WaveformView_timecodeColor,
                ContextCompat.getColor(context, R.color.default_timecode));

        a.recycle();

        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(mTextColor);
        textPaint.setTextSize(getFontSize(getContext(), android.R.attr.textAppearanceSmall));

        strokePaint = new Paint();
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeThickness);
        strokePaint.setAntiAlias(false);

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(mFillColor);

        markerPaint = new Paint();
        markerPaint.setStyle(Paint.Style.STROKE);
        markerPaint.setStrokeWidth(0);
        markerPaint.setAntiAlias(true);
        markerPaint.setColor(mMarkerColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = getMeasuredWidth();
        height = getMeasuredHeight();
        xStep = width / (mAudioLength * 1.0f);
        centerY = height / 2f;
        drawRect = new Rect(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (waveformPoints != null) {
            canvas.drawLines(waveformPoints, strokePaint);
        }
    }

    public void setSamples(float[] samples) {
        this.samples = samples;
        calculateAudioLength();
        onSamplesChanged();
    }

    public int getAudioLength() {
        return mAudioLength;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
        calculateAudioLength();
    }

    public void setChannels(int channels) {
        this.channels = channels;
        calculateAudioLength();
    }

    private void calculateAudioLength() {
        if (samples == null || sampleRate == 0 || channels == 0)
            return;

        mAudioLength = calculateAudioLength(samples.length, sampleRate, channels);
    }

    private void onSamplesChanged() {
        waveformPoints = new float[width * 4];
        drawRecordingWaveform(samples);
        // Redraw view
        postInvalidate();
    }

    public void drawRecordingWaveform(float[] samples) {
        float lastX = -1;
        float lastY = -1;
        int pointIndex = 0;
        float max = 1;

        /* For efficiency, we don't draw all of the samples in the buffer, but only the ones
           that align with pixel boundaries. */
        for (int x = 0; x < width; x += 2) {
            int index = (int) (((x * 1.0f) / width) * samples.length);
            float sample = samples[index];
            float y = centerY - ((sample / max) * centerY);

            if (lastX != -1) {
                waveformPoints[pointIndex++] = lastX;
                waveformPoints[pointIndex++] = lastY;
                waveformPoints[pointIndex++] = x;
                waveformPoints[pointIndex++] = y;
            }

            lastX = x;
            lastY = y;
        }
    }

    private int calculateAudioLength(int samplesCount, int sampleRate, int channelCount) {
        return ((samplesCount / channelCount) * 1000) / sampleRate;
    }

    private float getFontSize(Context ctx, int textAppearance) {
        TypedValue typedValue = new TypedValue();
        ctx.getTheme().resolveAttribute(textAppearance, typedValue, true);
        int[] textSizeAttr = new int[] { android.R.attr.textSize };
        TypedArray arr = ctx.obtainStyledAttributes(typedValue.data, textSizeAttr);
        float fontSize = arr.getDimensionPixelSize(0, -1);
        arr.recycle();
        return fontSize;
    }

}
