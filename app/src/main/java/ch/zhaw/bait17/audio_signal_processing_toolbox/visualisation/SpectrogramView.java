/**
 * Spectrogram Android application
 * Copyright (c) 2013 Guillaume Adam  http://www.galmiza.net/
 * <p>
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the use of this software.
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * <p>
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software. If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Colour;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.HeatMap;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Util;

public class SpectrogramView extends FrequencyView {

    private static final int db_PEAK = 50;
    private static final int dB_FLOOR = -60;
    private static final int dB_RANGE = Math.abs(dB_FLOOR) + db_PEAK;
    private static final int SPECTROGRAM_PAINT_STROKE_WIDTH = 3;

    private float[] spectralDensity;
    private int fftResolution;
    private Colour[] gradient = HeatMap.LSD;
    private Paint paint = new Paint();
    private Bitmap bitmap;
    private Canvas canvas;
    private int pos;
    private int width, height;

    /**
     *
     *
     * @param context
     */
    public SpectrogramView(Context context) {
        super(context);
        initialiseView();
    }

    /**
     *
     *
     * @param context
     * @param attrs
     */
    public SpectrogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialiseView();
    }

    /**
     *
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public SpectrogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (width > 0 && height > 0) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
        }
    }

    @Override
    public AudioView getInflatedView() {
        return (AudioView) View.inflate(ApplicationContext.getAppContext(),
                R.layout.spectrogram_view, null);
    }

    /**
     * Sets the resolution of the FFT. Sometimes called the FFT window size.
     * The input value is usually a power of 2.
     * For good results the window size should be in the range [2^11, 2^15].
     * The input value should not exceed 2^15.
     *
     * @param fftResolution     power of 2 in the range [2^11, 2^15]
     */
    @Override
    public void setFFTResolution(int fftResolution) {
        this.fftResolution = fftResolution;
    }

    /**
     * Sets the power spectral density to be displayed in the {@code FrequencyView}.
     * {@code preFilterMagnitude} represents the power spectral density of the unfiltered samples.
     * {@code postFilterMagnitude} represents the power spectral density of the filtered samples.
     *
     * @param preFilterMagnitude        unfiltered magnitude data
     * @param postFilterMagnitude       filtered magnitude data
     */
    @Override
    public void setSpectralDensity(@NonNull float[] preFilterMagnitude,
                                            @NonNull float[] postFilterMagnitude) {
        if (preFilterMagnitude.length > 0) {
            spectralDensity = Arrays.copyOf(preFilterMagnitude, preFilterMagnitude.length);
        } else if (postFilterMagnitude.length > 0) {
            spectralDensity = Arrays.copyOf(postFilterMagnitude, postFilterMagnitude.length);
        }
        postInvalidate();
    }

    /**
     * Called whenever a redraw is needed.
     * Renders spectrogram and scales on each side.
     *
     */
    @Override
    public void onDraw(Canvas canvas) {
        if (spectralDensity == null || getSampleRate() == 0) {
            return;
        }

        final int magnitudeAxisWidth = 80;
        final int frequencyAxisWidth = 70;
        final int spectrogramWidth = width - magnitudeAxisWidth - frequencyAxisWidth;
        double mindB = 0;
        double maxdB = 0;

        paint.setStrokeWidth(SPECTROGRAM_PAINT_STROKE_WIDTH);

        // Update buffer bitmap
        for (int i = 0; i < height; i++) {
            float j = getValueFromRelativePosition(
                    (height - i) / (float) height, 1, getSampleRate() / 2);
            float mag = spectralDensity[(int) (j * spectralDensity.length / 2)];
            double dB = 10 * Math.log10(mag);
            if (i == 0) {
                mindB = dB;
            }
            if (dB > maxdB) {
                maxdB = dB;
            }
            if (dB < mindB) {
                mindB = dB;
            }
            paint.setColor(getColour(dB).getRGB());
            this.canvas.drawPoint(pos % spectrogramWidth, i, paint);
        }

        // Draw bitmap
        if (pos < spectrogramWidth) {
            canvas.drawBitmap(bitmap, magnitudeAxisWidth, 0, paint);
        } else {
            canvas.drawBitmap(bitmap, (float) magnitudeAxisWidth - (pos % spectrogramWidth),
                    0, paint);
            canvas.drawBitmap(bitmap, (float) magnitudeAxisWidth +
                    (spectrogramWidth - pos % spectrogramWidth), 0, paint);
        }

        drawMagnitudeAxis(canvas, magnitudeAxisWidth);
        drawFrequencyAxis(canvas, magnitudeAxisWidth, spectrogramWidth);

        pos += paint.getStrokeWidth();
    }

    private void drawFrequencyAxis(@NonNull Canvas canvas, final int gradientWidth,
                                   final int spectrogramWidth) {
        final int frequencyStep = 1000;
        paint.setColor(Color.WHITE);
        canvas.drawRect(gradientWidth + spectrogramWidth, 0, width, height, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(Util.getFontSize(android.R.attr.textAppearanceSmall));
        canvas.drawText("kHz", spectrogramWidth + gradientWidth, paint.getTextSize(), paint);
        for (int i = 0; i < (getSampleRate() - (frequencyStep / 2)) / 2; i += frequencyStep) {
            float y = height * (1.0f - (float) i / (getSampleRate() / 2));
            canvas.drawText(" " + (i / frequencyStep), spectrogramWidth + gradientWidth, y, paint);
        }
    }

    private void drawMagnitudeAxis(@NonNull Canvas canvas, final int gradientWidth) {
        final float textWidth = gradientWidth * 0.75f;
        final int dBStep = 10;

        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, gradientWidth, height, paint);

        // Heat map
        for (int i = 0; i < height; i++) {
            int index = (int) (gradient.length - 1 - (i / (float) height) * (gradient.length - 1));
            Colour colour = gradient[index];
            paint.setColor(colour.getRGB());
            canvas.drawLine(0, i, gradientWidth - textWidth, i, paint);
        }

        // Axis label
        paint.setColor(Color.BLACK);
        paint.setTextSize(Util.getFontSize(android.R.attr.textAppearanceSmall));
        canvas.drawText("dB", gradientWidth - textWidth, paint.getTextSize(), paint);
        for (int i = dB_RANGE; i >= 0; i -= dBStep) {
            canvas.drawText(Integer.toString(dB_FLOOR + i), gradientWidth - textWidth,
                    height * (1f - (float) i / dB_RANGE), paint);
        }
    }

    /**
     * If onDraw() is not called after invalidate(), this method does the job.
     * See <a href="http://stackoverflow.com/questions/17595546/why-ondraw-is-not-called-after-invalidate#17595671">stackoverflow.com</a>
     *
     */
    private void initialiseView() {
        setWillNotDraw(false);
        paint.setAntiAlias(true);
    }

    private float getValueFromRelativePosition(float position, float minValue, float maxValue) {
        return (minValue + position * (maxValue - minValue)) / maxValue;
    }

    /**
     * Returns the {@code Colour} that corresponds to the specific dB value.
     *
     * @param dB    a double value representing a magnitude expressed in dB
     * @return      a {@code Colour}
     */
    private Colour getColour(double dB) {
        if (dB <= dB_FLOOR || Double.compare(dB, Double.NEGATIVE_INFINITY) == 0) {
            dB = dB_FLOOR;
        }
        if (dB >= db_PEAK || Double.compare(dB, Double.POSITIVE_INFINITY) == 0) {
            dB = db_PEAK;
        }
        double dbScaled = dB + Math.abs(dB_FLOOR);
        int index = (int) ((dbScaled / dB_RANGE) * (gradient.length - 1));
        return gradient[index];
    }
}
