/**
 * Spectrogram Android application
 * Copyright (c) 2013 Guillaume Adam  http://www.galmiza.net/

 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the use of this software.
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:

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
import android.util.Log;
import android.view.MotionEvent;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Colour;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.HeatMap;

public class SpectrogramView extends AudioView {

    private static final Colour[] gradient = HeatMap.RAINBOW;
    private static final String TAG = SpectrogramView.class.getSimpleName();
    private static final double dB_RANGE = 60;
    private static final double dB_BOTTOM = -30;
    private static final double db_PEAK = dB_RANGE - Math.abs(dB_BOTTOM);


    private Paint paint = new Paint();
    private Bitmap bitmap;
    private Canvas canvas;
    private int pos;
    private int width, height;
    private int fftWindowSize;
    private float[] magnitudes;

    public SpectrogramView(Context context) {
        super(context);
        init();
    }

    public SpectrogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpectrogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
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
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        return true;
    }

    public void setFFTWindowSize(int fftWindowSize) {
        this.fftWindowSize = fftWindowSize;
    }

    public void setMagnitudes(@NonNull float[] hMag) {
        magnitudes = new float[hMag.length];
        System.arraycopy(hMag, 0, magnitudes, 0, hMag.length);
        postInvalidate();
    }

    /**
     * Called whenever a redraw is needed
     * Renders spectrogram and scale on the right
     * Frequency scale can be linear or logarithmic
     */
    @Override
    public void onDraw(Canvas canvas) {
        if (magnitudes == null) {
            return;
        }

        double mindB = 0;
        double maxdB = 0;
        int widthColorGradient = 30;
        int widthFrequencyAxis = 60;
        int spectrogramWidth = width - widthColorGradient - widthFrequencyAxis;
        paint.setStrokeWidth(3);
        boolean logFrequency = false;

        // Update buffer bitmap
        for (int i = 0; i < height; i++) {
            float j = getValueFromRelativePosition((float) (height - i) / height, 1, getSampleRate() / 2, logFrequency);
            j /= getSampleRate() / 2;
            float mag = magnitudes[(int) (j * magnitudes.length / 2)];
            double dB = (10 * Math.log10(mag));
            if (i == 0) {
                mindB = dB;
            }
            if (dB > maxdB) {
                maxdB = dB;
            }
            if (dB < mindB) {
                mindB = dB;
            }
            Colour colour = getColour(dB);
            paint.setColor(colour.getRGB());
            int x = pos % spectrogramWidth;
            int y = i;
            this.canvas.drawPoint(x, y, paint);
            //this.canvas.drawPoint(x, y, paint); // make color brighter
            //this.canvas.drawPoint(pos%rWidth, height-i, paint); // make color even brighter
        }

        //Log.d(TAG, String.format("min dB = %f, max dB = %f, dB range = %f", mindB, maxdB, maxdB - mindB));

        // Draw bitmap
        if (pos < spectrogramWidth) {
            canvas.drawBitmap(bitmap, widthColorGradient, 0, paint);
        } else {
            canvas.drawBitmap(bitmap, (float) widthColorGradient - pos % spectrogramWidth, 0, paint);
            canvas.drawBitmap(bitmap, (float) widthColorGradient + (spectrogramWidth - pos % spectrogramWidth), 0, paint);
        }

        // Draw gradient (decibel scale)
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, widthColorGradient, height, paint);
        for (int i = 0; i < height; i++) {
            int index = (int) (gradient.length - 1 - (i / (float) height) * (gradient.length - 1));
            Colour colour = gradient[index];
            paint.setColor(colour.getRGB());
            canvas.drawLine(0, i, widthColorGradient - 5, i, paint);
        }

        // Draw frequency scale
        float ratio = 0.8f * getResources().getDisplayMetrics().density;
        paint.setTextSize(12f * ratio);
        paint.setColor(Color.WHITE);
        canvas.drawRect(spectrogramWidth + widthColorGradient, 0, width, height, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText("kHz", spectrogramWidth + widthColorGradient, 12 * ratio, paint);

        int minFrequency = (int) Math.floor(Math.log10(10));
        int maxFrequency = (int) Math.ceil(Math.log10(getSampleRate() / 2));

        if (logFrequency) {
            for (int i = minFrequency; i < maxFrequency; i++) {
                float y = getRelativePosition((float) Math.pow(10,i), 1, getSampleRate() / 2, logFrequency);
                canvas.drawText("1e"+i, spectrogramWidth + widthColorGradient, (1f-y)*height, paint);
            }
        } else {
            for (int i=0; i<(getSampleRate() - 500)/2; i+=1000)
                canvas.drawText(" "+i/1000, spectrogramWidth + widthColorGradient, height*(1f-(float) i/(getSampleRate() / 2)), paint);
        }

        pos += paint.getStrokeWidth();
    }

    /**
     * Converts relative position of a value within given boundaries
     * Log=true for logarithmic scale
     */
    private float getRelativePosition(float value, float minValue, float maxValue, boolean log) {
        if (log) {
            return ((float) Math.log10( 1+ value - minValue) / (float) Math.log10(1 + maxValue - minValue));
        } else {
            return (value - minValue) / (maxValue - minValue);
        }
    }

    /**
     * Returns a value from its relative position within given boundaries
     * Log=true for logarithmic scale
     */
    private float getValueFromRelativePosition(float position, float minValue, float maxValue, boolean log) {
        if (log) {
            return (float) (Math.pow(10, position * Math.log10(1 + maxValue - minValue)) + minValue - 1);
        } else {
            return minValue + position * (maxValue - minValue);
        }
    }

    public Colour getColour(double dB) {
        if (dB <= dB_BOTTOM || Double.compare(dB, Double.NEGATIVE_INFINITY) == 0) {
            dB = dB_BOTTOM;
        }
        if (dB >= db_PEAK || Double.compare(dB, Double.POSITIVE_INFINITY) == 0) {
            dB = db_PEAK;
        }
        // Scaled dB
        double dbScaled = dB + Math.abs(dB_BOTTOM);
        int index = (int) ((dbScaled / dB_RANGE) * (gradient.length - 1));
        return gradient[index];
    }

}