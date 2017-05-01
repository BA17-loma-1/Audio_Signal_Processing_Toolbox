/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Util;

/**
 * <p>
 * Based on work by Yavor Ivanov (<a href="https://github.com/yavor87">GitHub</a>)
 * from New Venture Software. </br>
 *
 * Source: <a href="https://github.com/newventuresoftware/WaveformControl">GitHub repo</a>
 * </p>
 *
 */

public class WaveformView extends TimeView {

    private static final int MAX_MAP_ENTRIES = 3;
    private static final float WAVEFORM_PRE_FILTER_STROKE_THICKNESS = 1.0f;
    private static final float WAVEFORM_POST_FILTER_STROKE_THICKNESS = 1.0f;
    private final Map<VisualisationType, short[]> SAMPLES = new HashMap<>(MAX_MAP_ENTRIES );
    private final ConcurrentMap<VisualisationType, float[]> waveformPoints
            = new ConcurrentHashMap<>(MAX_MAP_ENTRIES);

    private Paint preFilterStrokePaint, postFilterStrokePaint;
    private int width;
    private float centerY;

    public WaveformView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WaveformView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setWillNotDraw(false);
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.WaveformView, defStyle, 0);

        float strokeThicknessPreFilter = a.getFloat(R.styleable.WaveformView_waveformStrokeThickness,
                WAVEFORM_PRE_FILTER_STROKE_THICKNESS);
        float strokeThicknessPostFilter = a.getFloat(R.styleable.WaveformView_waveformStrokeThickness,
                WAVEFORM_POST_FILTER_STROKE_THICKNESS);
        int preFilterStrokeColor = a.getColor(R.styleable.WaveformView_waveformColor,
                ContextCompat.getColor(context, R.color.pre_filter_waveform));
        int postFilterStrokeColor = a.getColor(R.styleable.WaveformView_waveformColor,
                ContextCompat.getColor(context, R.color.post_filter_waveform));
        a.recycle();

        final float fontSize = Util.getFontSize(android.R.attr.textAppearanceMedium);

        preFilterStrokePaint = new Paint();
        preFilterStrokePaint.setColor(preFilterStrokeColor);
        preFilterStrokePaint.setStyle(Paint.Style.STROKE);
        preFilterStrokePaint.setStrokeWidth(strokeThicknessPreFilter);
        preFilterStrokePaint.setAntiAlias(false);
        preFilterStrokePaint.setTextSize(fontSize);
        preFilterStrokePaint.setAntiAlias(true);

        postFilterStrokePaint = new Paint();
        postFilterStrokePaint.setColor(postFilterStrokeColor);
        postFilterStrokePaint.setStyle(Paint.Style.STROKE);
        postFilterStrokePaint.setStrokeWidth(strokeThicknessPostFilter);
        postFilterStrokePaint.setAntiAlias(false);
        postFilterStrokePaint.setTextSize(fontSize);
        postFilterStrokePaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        int height = getMeasuredHeight();
        centerY = height / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWaveforms(canvas);
    }

    /**
     * Sets the data to be displayed in the {@code TimeView}.
     *
     * @param preFilterSamples      unfiltered samples
     * @param postFilterSamples     filtered samples
     */
    @Override
    public void setSamples(@NonNull short[] preFilterSamples, @NonNull short[] postFilterSamples) {
        SAMPLES.put(VisualisationType.PRE_FX, Arrays.copyOf(preFilterSamples, preFilterSamples.length));
        SAMPLES.put(VisualisationType.POST_FX, Arrays.copyOf(postFilterSamples, postFilterSamples.length));
        onSamplesChanged();
    }

    @Override
    public AudioView getInflatedView() {
        return (AudioView) View.inflate(ApplicationContext.getAppContext(),
                R.layout.waveform_view, null);
    }

    private void onSamplesChanged() {
        createWaveformPoints();
        postInvalidate();
    }

    private void createWaveformPoints() {
        if (SAMPLES != null) {
            final float max = Short.MAX_VALUE;
            for (Map.Entry<VisualisationType, short[]> entry : SAMPLES.entrySet()) {
                VisualisationType vizType = entry.getKey();
                short[] sampleBlock = entry.getValue();
                float[] points = new float[width * 4];
                float lastX = -1;
                float lastY = -1;
                int pointIndex = 0;

                /*
                    For efficiency, we don't draw all of the samples in the buffer, but only the
                    ones that align with pixel boundaries.
                 */
                waveformPoints.remove(vizType);
                if (sampleBlock.length > 0) {
                    for (int x = 0; x < width; x++) {
                        int index = (int) (((x * 1.0f) / width) * sampleBlock.length);
                        short sample = sampleBlock[index];
                        float y = centerY - ((sample / max) * centerY);

                        if (lastX != -1) {
                            points[pointIndex++] = lastX;
                            points[pointIndex++] = lastY;
                            points[pointIndex++] = x;
                            points[pointIndex++] = y;
                        }

                        lastX = x;
                        lastY = y;
                    }
                    waveformPoints.put(vizType, points);
                }
            }
        }
    }

    private void drawWaveforms(Canvas canvas) {
        final int leftOffset = 20;
        for (Map.Entry<VisualisationType, float[]> entry : waveformPoints.entrySet()) {
            VisualisationType vizType = entry.getKey();
            final float[] points = entry.getValue();
            if (points != null && points.length > 0) {
                switch (vizType) {
                    case PRE_FX:
                        canvas.drawLines(points, preFilterStrokePaint);
                        preFilterStrokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        canvas.drawText(VisualisationType.PRE_FX.toString(), leftOffset,
                                preFilterStrokePaint.getTextSize(), preFilterStrokePaint);
                        preFilterStrokePaint.setStyle(Paint.Style.STROKE);
                        break;
                    case POST_FX:
                        canvas.drawLines(points, postFilterStrokePaint);
                        postFilterStrokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        canvas.drawText(VisualisationType.POST_FX.toString(), leftOffset,
                                preFilterStrokePaint.getTextSize() + postFilterStrokePaint.getTextSize(),
                                postFilterStrokePaint);
                        postFilterStrokePaint.setStyle(Paint.Style.STROKE);
                        break;
                    default:
                }
            }
        }
    }
}
