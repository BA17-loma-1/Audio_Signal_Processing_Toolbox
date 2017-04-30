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
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;

/**
 * Renders a third-octave spectrum view.
 *
 * @author georgrem, stockan1
 */
public class SpectrumView extends View {

    private static final String TAG = SpectrumView.class.getSimpleName();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.0K");
    private static final int OCTAVE_BANDS = 31;
    private static final int REFERENCE_CENTER_FREQUENCY = 1000;
    private static final int OCTAVE_BAND_REFERENCE_FREQUENCY = 18;
    private static final int dB_RANGE = 96;

    private double[] centreFrequencies;
    private double[] thirdOctaveFrequencyBoundaries;
    private int width, height;
    private TextPaint textPaint;
    private Paint strokePaint;
    private int sampleRate;
    private float[] magnitudes;

    public SpectrumView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SpectrumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SpectrumView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(final Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.WaveformView, defStyle, 0);

        float strokeThickness = a.getFloat(R.styleable.WaveformView_waveformStrokeThickness, 1f);
        int strokeColor = a.getColor(R.styleable.WaveformView_waveformColor,
                ContextCompat.getColor(context, R.color.default_waveform));
        int textColor = a.getColor(R.styleable.SpectrumView_spectrumFrequencyLabel,
                ContextCompat.getColor(context, R.color.default_spectrum_freq_label));
        a.recycle();

        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(textColor);
        textPaint.setTextSize(getFontSize(getContext(), android.R.attr.textAppearanceSmall));

        strokePaint = new Paint();
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        strokePaint.setStrokeWidth(strokeThickness);
        strokePaint.setAntiAlias(false);

        calculateCentreFrequencies();
        calculateThirdOctaveBands();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        render(canvas);
    }

    /**
     * Sets the resolution of the FFT. Sometimes called the FFT windows size.
     * The input value is usually a power of 2.
     * For good results the window size should be in the range [2^11, 2^15].
     * The input value should not exceed 2^15.
     *
     * @param fftResolution     power of 2 in the range [2^11, 2^15]
     */
    public void setFFTResolution(int fftResolution) {
        magnitudes = new float[fftResolution];
    }

    /**
     * Sets the spectral density to be displayed in the {@code FrequencyView}.
     * hMag represents the power spectrum of a time series.
     *
     * @param hMag array of {@code float} representing magnitudes (power spectrum of a time series)
     */
    public void setSpectralDensity(@NonNull float[] hMag) {
        System.arraycopy(hMag, 0, magnitudes, 0, hMag.length);
        postInvalidate();
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public AudioView getInflatedView() {
        return (AudioView) View.inflate(ApplicationContext.getAppContext(),
                R.layout.spectrum_view, null);
    }

    public double[] getCentreFrequencies() {
        double[] retVal = new double[centreFrequencies.length];
        System.arraycopy(centreFrequencies, 0, retVal, 0, centreFrequencies.length);
        return retVal;
    }

    public double[] getThirdOctaveFrequencyBands() {
        double[] retVal = new double[thirdOctaveFrequencyBoundaries.length];
        System.arraycopy(thirdOctaveFrequencyBoundaries, 0, retVal, 0, thirdOctaveFrequencyBoundaries.length);
        return retVal;
    }

    private void render(@NonNull Canvas canvas) {
        if (sampleRate > 0 && magnitudes != null) {
            float[] hMag = new float[magnitudes.length];
            System.arraycopy(magnitudes, 0, hMag, 0, magnitudes.length);


            int nFFT = hMag.length;
            double deltaFrequency = sampleRate / (double) nFFT;

            float barWidth = width / (float) OCTAVE_BANDS;
            float dcMagnitude = (float) (10 * Math.log10(Math.abs(hMag[0])));

            //Log.i(TAG, String.format("bar width: %f", barWidth));
            //Log.i(TAG, String.format("canvas width: %d  canvas height: %d", canvas.getWidth(), canvas.getHeight()));
            //Log.i(TAG, String.format("measured width: %d  measured height: %d", width, heigth));

            Map<Double, RectF> magnitudeBars = new LinkedHashMap<>();
            // DC -> bin m[0]
            magnitudeBars.put(0d, new RectF(0, height - (height / (float) (dB_RANGE * dcMagnitude)),
                    barWidth, height - 40));

            double frequency = 0;
            int bin = 0;
            int countRect = 1;
            for (int i = 1; i < thirdOctaveFrequencyBoundaries.length - 1; i += 3) {
                double upperBound = thirdOctaveFrequencyBoundaries[i + 1];
                float meanMagnitude = 0;
                int k = 0;
                while ((frequency = bin * deltaFrequency) <= upperBound) {
                    meanMagnitude += Math.abs(hMag[bin]);
                    bin++;
                    k++;
                }
                meanMagnitude /= k;
                magnitudeBars.put(frequency, new RectF((countRect * barWidth) + 5,
                        height - (height / dB_RANGE * (float) (10 * Math.log10(meanMagnitude))),
                        (countRect * barWidth) + barWidth,
                        height - 40));
                countRect++;
            }

            //Log.i(TAG, String.format("magnitude bars: %d", magnitudeBars.size()));

            int count = 0;
            for (Map.Entry<Double, RectF> entry : magnitudeBars.entrySet()) {
                // Render frequency band
                canvas.drawRect(entry.getValue(), strokePaint);

                // Render frequency label text
                double freq = entry.getKey();
                String frequencyLabel = null;
                if (freq < REFERENCE_CENTER_FREQUENCY && count % 2 == 0) {
                    frequencyLabel = Long.toString((long) freq);
                } else {
                    if (count % 2 == 0) {
                        frequencyLabel = getFormattedValue(freq / 1000);
                    }
                }
                count++;

                if (frequencyLabel != null) {
                    canvas.drawText(frequencyLabel, entry.getValue().centerX(),
                            canvas.getHeight(), textPaint);
                }
            }

        }
    }

    /**
     * Go through all centre frequencies and calculate lower and upper frequency bounds.
     */
    private void calculateThirdOctaveBands() {
        if (centreFrequencies != null) {
            thirdOctaveFrequencyBoundaries = new double[(centreFrequencies.length * 2) + 1];
            int centreFrequencyIndex = 0;
            double factor = Math.pow(2.0, 1d / 6);
            double firstLowerBound = centreFrequencies[centreFrequencyIndex] / factor;
            thirdOctaveFrequencyBoundaries[0] = firstLowerBound;

            for (int i = 1; i < thirdOctaveFrequencyBoundaries.length; ) {
                double upperBound = centreFrequencies[centreFrequencyIndex] * factor;
                // Centre frequency
                thirdOctaveFrequencyBoundaries[i++] = centreFrequencies[centreFrequencyIndex];
                thirdOctaveFrequencyBoundaries[i++] = upperBound;
                centreFrequencyIndex++;
            }
        }
    }

    private void calculateCentreFrequencies() {
        centreFrequencies = new double[OCTAVE_BANDS + 1];
        centreFrequencies[OCTAVE_BAND_REFERENCE_FREQUENCY] = REFERENCE_CENTER_FREQUENCY;
        final double factor = Math.pow(2, 1d/3);
        // Fill lower than centre
        for (int i = OCTAVE_BAND_REFERENCE_FREQUENCY - 1; i >= 0; i--) {
            centreFrequencies[i] = centreFrequencies[i + 1] / factor;
        }
        // Fill higher than centre
        for (int i = OCTAVE_BAND_REFERENCE_FREQUENCY; i < centreFrequencies.length-1; i++) {
            centreFrequencies[i + 1] = centreFrequencies[i] * factor;
        }
    }

    private float getFontSize(Context ctx, int textAppearance) {
        TypedValue typedValue = new TypedValue();
        ctx.getTheme().resolveAttribute(textAppearance, typedValue, true);
        int[] textSizeAttr = new int[]{android.R.attr.textSize};
        TypedArray arr = ctx.obtainStyledAttributes(typedValue.data, textSizeAttr);
        float fontSize = arr.getDimensionPixelSize(0, -1);
        arr.recycle();
        Log.d(TAG, String.format("FONT SIZE: %f", fontSize));
        return fontSize;
    }

    private String getFormattedValue(double value) {
        return DECIMAL_FORMAT.format(value);
    }

}
