package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;

/**
 * @author georgrem, stockan1
 */
public class LineSpectrumView extends AudioView {

    private Paint strokePaint, fillPaint, markerPaint;
    private int width, height;
    private short[] samples;
    private float[] spectrumPoints;

    public LineSpectrumView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public LineSpectrumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LineSpectrumView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setWillNotDraw(false);
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

        a.recycle();

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (samples != null) {
            float[] amplitudes = getSpectrumPoints(samples);

            float[] logAmplitudes = new float[amplitudes.length];
            for (int i = 0; i < logAmplitudes.length; i++) {
                logAmplitudes[i] = 10 * (float) Math.log10(amplitudes[i]);
            }

            drawSpectrumCurveShape(amplitudes, 50);
            canvas.drawLines(spectrumPoints, strokePaint);

            drawSpectrumLineShape(amplitudes, 100, canvas);

            drawSpectrumCurveShape(logAmplitudes, 150);
            canvas.drawLines(spectrumPoints, strokePaint);

            drawSpectrumBarShape(amplitudes, 200, canvas);
        }
    }

    private void drawSpectrumCurveShape(float[] amplitudes, int ZERO_DEZ_REF) {
        spectrumPoints = new float[amplitudes.length * 4];
        int pointIndex = 0;
        float lastX = -1;
        float lastY = -1;

        /* For efficiency, we don't draw all of the samples in the buffer, but only the ones
           that align with pixel boundaries. */
        for (int x = 0; x < width; x++) {
            int index = (int) (((x * 1.0f) / width) * amplitudes.length);
            float amplitude = amplitudes[index];
            float y = ZERO_DEZ_REF - amplitude;

            if (lastX != -1) {
                spectrumPoints[pointIndex++] = lastX;
                spectrumPoints[pointIndex++] = lastY;
                spectrumPoints[pointIndex++] = x;
                spectrumPoints[pointIndex++] = y;
            }
            lastX = x;
            lastY = y;
        }
    }

    private void drawSpectrumLineShape(float[] amplitudes, int ZERO_DEZ_REF, Canvas canvas) {
        /* For efficiency, we don't draw all of the samples in the buffer, but only the ones
           that align with pixel boundaries. */
        for (int x = 0; x < width; x++) {
            int index = (int) (((x * 1.0f) / width) * amplitudes.length);
            float sample = amplitudes[index];
            float downy = ZERO_DEZ_REF - (sample);
            int upy = ZERO_DEZ_REF;
            canvas.drawLine(x, downy, x, upy, strokePaint);
        }
    }

    private void drawSpectrumBarShape(float[] amplitudes, int ZERO_DEZ_REF, Canvas canvas) {
        /* For efficiency, we don't draw all of the samples in the buffer, but only the ones
           that align with pixel boundaries. */
        int scaledWidt = width/5;
        for (int x = 0; x < scaledWidt; x++) {
            int index = (int) (((x * 1.0f) / scaledWidt) * amplitudes.length);
            float sample = amplitudes[index];
            float downy = ZERO_DEZ_REF - (sample);
            int upy = ZERO_DEZ_REF;
            canvas.scale(1, 1);
            canvas.drawRect(x * 5, downy, x * 5 + 5, upy, strokePaint);
        }
    }

    public void setSamples(short[] samples) {
        this.samples = samples;
        if (this.samples != null) {
            postInvalidate();
        }
    }

    private float[] getSpectrumPoints(short[] samples) {
        float[] spectrum = getPowerSpectrum(samples);
        for (int i = 0; i < spectrum.length; i++) {
            spectrum[i] = -10 * (float) Math.log10(spectrum[i]);
        }
        return spectrum;
    }

    private float[] getPowerSpectrum(@NonNull short[] samples) {
        return new PowerSpectrum(samples).getPowerSpectrum();
    }

}
