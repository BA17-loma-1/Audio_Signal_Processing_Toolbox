/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import java.util.LinkedHashMap;
import java.util.Map;
import ch.zhaw.bait17.audio_signal_processing_toolbox.FFT;
import ch.zhaw.bait17.audio_signal_processing_toolbox.PCMUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WindowType;

/**
 *
 * Created by georgrem, stockan1 on 04.03.2017.
 */
public class SpectrumRenderer {

    private FFT fft = new FFT(WindowType.HAMMING);
    private static final int OCTAVE_BANDS = 31;
    private static final int REFERENCE_CENTER_FREQUENCY = 1000;
    private static final int OCTAVE_BAND_REFERENCE_FREQUENCY = 18;
    private static double[] centreFrequencies;
    private static double[] thirdOctaveFrequencyBoundaries;
    private TextPaint textPaint;
    private Paint paint;

    public SpectrumRenderer(Paint paint) {
        this.paint = paint;
        calculateCentreFrequencies();
        calculateThirdOctaveBands();

        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(24);
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
        // Fill lower centre
        for (int i = OCTAVE_BAND_REFERENCE_FREQUENCY - 1; i >= 0; i--) {
            centreFrequencies[i] = centreFrequencies[i + 1] / factor;
        }
        // Fill higher centre
        for (int i = OCTAVE_BAND_REFERENCE_FREQUENCY; i < centreFrequencies.length-1; i++) {
            centreFrequencies[i + 1] = centreFrequencies[i] * factor;
        }
    }

    public void render(Canvas canvas, short[] samples, int sampleRate) {
        if (samples != null || sampleRate == 0) {
            float[] spectrum = getRealSpectrum(samples);

            int nFFT = spectrum.length;
            double deltaFrequency = sampleRate / nFFT;

            float barWidth = (canvas.getWidth() / OCTAVE_BANDS) + 10;
            float dcMagnitude = (float) (20 * Math.log10(Math.abs(spectrum[0])));

            Map<Double, RectF> magnitudeBars = new LinkedHashMap<>();
            // DC -> bin m[0]
            magnitudeBars.put(0d, new RectF(0, canvas.getHeight() - dcMagnitude, barWidth, canvas.getHeight()-30));

            double frequency = 0;
            int k = 1;
            int countRect = 1;
            for (int i = 1; i < thirdOctaveFrequencyBoundaries.length - 1; i += 3) {
                double upperBound = thirdOctaveFrequencyBoundaries[i + 1];
                float maxMagnitude = Float.MIN_VALUE;
                while ((frequency = k * deltaFrequency) <= upperBound) {
                    // Find max
                    if (Math.abs(spectrum[2 * k]) > maxMagnitude) {
                        maxMagnitude = Math.abs(spectrum[2 * k]);
                    }
                    k++;
                }
                magnitudeBars.put(frequency, new RectF((countRect * barWidth) + 5,
                        canvas.getHeight() - 5 * (float) (20 * Math.log10(maxMagnitude)),
                        (countRect * barWidth) + barWidth,
                        canvas.getHeight()-30));
                countRect++;
            }

            int count = 0;
            for (Map.Entry<Double, RectF> entry : magnitudeBars.entrySet()) {
                if (count++ % 2 == 0) {
                    canvas.drawText(Double.toString(entry.getKey()), entry.getValue().centerX(),
                            canvas.getHeight(), textPaint);
                }
                canvas.drawRect(entry.getValue(), paint);
            }
        }
    }

    /**
     *
     * @return
     */
    public double[] getCentreFrequencies() {
        return centreFrequencies;
    }

    /**
     *
     * @return
     */
    public double[] getThirdOctaveFrequencyBands() {
        return thirdOctaveFrequencyBoundaries;
    }

    private float[] getRealSpectrum(short[] samples) {
        float[] spectrum = new float[samples.length / 2];
        if (samples != null && samples.length > 0) {
            float[] floatSamples = PCMUtil.short2FloatArray(samples);
            float[] complexFFT = fft.getForwardTransform(floatSamples);
            for (int i = 0; i < spectrum.length; i++) {
                spectrum[i] = complexFFT[2 * i];
            }
        }
        return spectrum;
    }

}
