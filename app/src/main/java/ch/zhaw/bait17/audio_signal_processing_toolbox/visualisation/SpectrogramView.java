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
import java.util.Locale;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Colour;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.PCMUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Util;

/**
 * <p>
 *      Draws the spectrogram onto a {@code Canvas}. <br>
 *      Spectra of transformed PCM sample blocks are "laid side by side" to form the spectrogram. <br>
 *      This corresponds to computing the squared magnitude of short-time Fourier transform (STFT).
 * </p>
 */

public class SpectrogramView extends FrequencyView {

    private static final float FULL_SCALE = PCMUtil.getFullScaleValue();
    private static final float OFFSET_TOP = Util.getFontSize(android.R.attr.textAppearanceSmall) + 10;
    private static final int MAGNITUDE_AXIS_WIDTH = 80;
    private static final int FREQUENCY_AXIS_WIDTH = 70;
    private static final int db_PEAK = 0;
    private static final int SPECTROGRAM_PAINT_STROKE_WIDTH = 3;

    private float[] spectralDensity;
    private int fftResolution;
    private String windowName;
    private Colour[] colormap;
    private Paint bitmapPaint;
    private Paint textPaint;
    private Bitmap bitmap;
    private Canvas canvas;
    private int pos;
    private int width, height;
    private int dBFloor = ApplicationContext.getPreferredDBFloor();

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
        height = (int) (h - OFFSET_TOP);
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
     * Sets the name of the window function being used.
     *
     * @param windowName        the name of the window function
     */
    @Override
    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    /**
     * Sets the magnitude floor used in the visualisation.
     *
     * @param magnitudeFloor   the magnitude floor
     */
    @Override
    public void setMagnitudeFloor(int magnitudeFloor) {
        dBFloor = magnitudeFloor;
    }

    /**
     * Sets the colormap used to draw the bitmap of the spectrogram.
     *
     * @param colormap  an array of {@code Colour}
     */
    public void setColormap(Colour[] colormap) {
        if (this.colormap.length == colormap.length) {
            System.arraycopy(colormap, 0, this.colormap, 0, colormap.length);
        }
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

        float[] magnitudes = new float[spectralDensity.length];
        System.arraycopy(spectralDensity, 0, magnitudes, 0, spectralDensity.length);

        final int spectrogramWidth = width - MAGNITUDE_AXIS_WIDTH - FREQUENCY_AXIS_WIDTH;

        for (int i = 0; i < height; i++) {
            float j = getValueFromRelativePosition(
                    (height - i) / (float) height, 1, getSampleRate() / 2);
            float mag = magnitudes[(int) (j * magnitudes.length / 2)];
            // Compute dB relative to full scale (dBFS)
            // Full scale is the maximum value of a PCM sample
            double dBFS = 10 * Math.log10(Math.abs(mag) / FULL_SCALE);
            bitmapPaint.setColor(getColour(dBFS).getRGB());
            this.canvas.drawPoint(pos % spectrogramWidth, i, bitmapPaint);
        }

        // Draw the spectrogram as a bitmap
        if (pos < spectrogramWidth) {
            canvas.drawBitmap(bitmap, MAGNITUDE_AXIS_WIDTH, OFFSET_TOP, bitmapPaint);
        } else {
            // Draw "old" squared magnitude of short-time Fourier transform (STFT)
            canvas.drawBitmap(bitmap, (float) MAGNITUDE_AXIS_WIDTH - (pos % spectrogramWidth),
                    OFFSET_TOP, bitmapPaint);
            // Draw "new" squared magnitude of short-time Fourier transform (STFT)
            canvas.drawBitmap(bitmap, (float) MAGNITUDE_AXIS_WIDTH +
                    (spectrogramWidth - pos % spectrogramWidth), OFFSET_TOP, bitmapPaint);
        }

        drawMagnitudeAxis(canvas);
        drawFrequencyAxis(canvas, spectrogramWidth);
        drawInfo(canvas);

        pos += bitmapPaint.getStrokeWidth();
    }

    private void drawFrequencyAxis(@NonNull Canvas canvas, final int spectrogramWidth) {
        final int frequencyStep = 1000;
        bitmapPaint.setColor(Color.WHITE);
        canvas.drawRect(MAGNITUDE_AXIS_WIDTH + spectrogramWidth, 0, width, height + OFFSET_TOP, bitmapPaint);
        canvas.drawText("kHz", spectrogramWidth + MAGNITUDE_AXIS_WIDTH, textPaint.getTextSize(), textPaint);
        for (int i = 0; i < (getSampleRate() - (frequencyStep / 2)) / 2; i += frequencyStep) {
            float y = OFFSET_TOP + (height * (1.0f - (float) i / (getSampleRate() / 2)));
            canvas.drawText(" " + (i / frequencyStep), spectrogramWidth + MAGNITUDE_AXIS_WIDTH, y, textPaint);
        }
    }

    private void drawMagnitudeAxis(@NonNull Canvas canvas) {
        final float textWidth = MAGNITUDE_AXIS_WIDTH * 0.75f;
        final int dBStep = 10;

        bitmapPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, MAGNITUDE_AXIS_WIDTH, height + OFFSET_TOP, bitmapPaint);

        // Colormap
        for (int i = 0; i < height; i++) {
            int index = (int) (colormap.length - 1 - (i / (float) height) * (colormap.length - 1));
            Colour colour = colormap[index];
            bitmapPaint.setColor(colour.getRGB());
            canvas.drawLine(0, i + OFFSET_TOP, MAGNITUDE_AXIS_WIDTH - textWidth, i + OFFSET_TOP, bitmapPaint);
        }

        // Axis label
        final int magnitudeRange = Math.abs(dBFloor);
        canvas.drawText("dB", MAGNITUDE_AXIS_WIDTH - textWidth, textPaint.getTextSize(), textPaint);
        for (int i = db_PEAK; i >= -magnitudeRange; i -= dBStep) {
            canvas.drawText(Integer.toString(i), MAGNITUDE_AXIS_WIDTH - textWidth,
                    OFFSET_TOP + textPaint.getTextSize() + ((height - textPaint.getTextSize()) * (1.0f - ((magnitudeRange + i) / (float) magnitudeRange))), textPaint);
        }
    }

    private void drawInfo(@NonNull Canvas canvas) {
        canvas.drawText(String.format(Locale.getDefault(), "  FFT resolution: %d  |  Window: %s",
                fftResolution, windowName), MAGNITUDE_AXIS_WIDTH, textPaint.getTextSize(), textPaint);
    }

    /**
     * Returns the {@code Colour} that corresponds to the specific dB value.
     *
     * @param dB    a double value representing a magnitude expressed in dB
     * @return      a {@code Colour}
     */
    private Colour getColour(double dB) {
        final int magnitudeFloor = dBFloor;

        if (dB <= magnitudeFloor || Double.compare(dB, Double.NEGATIVE_INFINITY) == 0) {
            dB = magnitudeFloor;
        }
        if (dB >= db_PEAK || Double.compare(dB, Double.POSITIVE_INFINITY) == 0) {
            dB = db_PEAK;
        }
        int index = (int) (colormap.length - 1 - (Math.abs(dB) / Math.abs(magnitudeFloor) * (colormap.length - 1)));
        return colormap[index];
    }

    /**
     * If onDraw() is not called after invalidate(), this method does the job.
     * See <a href="http://stackoverflow.com/questions/17595546/why-ondraw-is-not-called-after-invalidate#17595671">stackoverflow.com</a>
     *
     */
    private void initialiseView() {
        setWillNotDraw(false);
        initialiseBitmapPaint();
        initialiseTextPaint();
        colormap = ApplicationContext.getPreferredColormap();
    }

    private float getValueFromRelativePosition(float position, float minValue, float maxValue) {
        return (minValue + position * (maxValue - minValue)) / maxValue;
    }

    private void initialiseBitmapPaint() {
        bitmapPaint = new Paint();
        bitmapPaint.setStrokeWidth(SPECTROGRAM_PAINT_STROKE_WIDTH);
        bitmapPaint.setColor(Color.WHITE);
        bitmapPaint.setAntiAlias(true);
    }

    private void initialiseTextPaint() {
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(Util.getFontSize(android.R.attr.textAppearanceSmall) - 4.0f);
        textPaint.setAntiAlias(true);
    }
}
