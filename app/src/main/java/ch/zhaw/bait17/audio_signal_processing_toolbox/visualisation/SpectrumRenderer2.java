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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.zhaw.bait17.audio_signal_processing_toolbox.FFT;
import ch.zhaw.bait17.audio_signal_processing_toolbox.PCMUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WindowType;

/**
 * @author georgrem, stockan1
 */
public class SpectrumRenderer2 {

    private final int HISTORY_LENGTH = 3;
    private static final String TAG = SpectrumRenderer2.class.getSimpleName();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.0K");
    private static final int dB_RANGE = 96;

    private FFT fft;
    private int width, heigth;
    private TextPaint textPaint;
    private Paint paint;
    List<float[]> spectrumHistory;
    private Context context;

    /**
     * Creates an instances of SpectrumRenderer.
     *
     * @param paint
     */
    public SpectrumRenderer2(Paint paint, TextPaint textPaint, Context context) {
        this.paint = paint;
        this.textPaint = textPaint;
        fft = new FFT(WindowType.HAMMING);
        spectrumHistory = new ArrayList<>(HISTORY_LENGTH);
        this.context = context;
    }

    public void render(Canvas canvas, short[] samples, int sampleRate) {
        if (samples != null || sampleRate == 0) {
            float[] spectrum = getPowerSpectrum(samples);

            final int nFFT = spectrum.length;
            final float deltaFrequency = sampleRate / nFFT;

            float[] frequencies = new float[spectrum.length];
            for (int i = 0; i < frequencies.length; i++) {
                frequencies[i] = (float) Math.log10(i * deltaFrequency);
            }

            float[] spectrumPoints = new float[frequencies.length * 2];
            for (int i = 0; i < spectrumPoints.length; i++) {
                spectrumPoints[i] = (i % 2 == 0) ? frequencies[i / 2] : spectrum[i / 2];
            }

            canvas.scale(50.0f, 50.0f);
            canvas.drawPoints(spectrumPoints, paint);
        }
    }

    private float[] getPowerSpectrum(short[] samples) {
        float[] spectrum = new float[samples.length / 2];
        if (samples != null && samples.length > 0) {
            float[] floatSamples = PCMUtil.short2FloatArray(samples);
            float[] complexFFT = fft.getForwardTransform(floatSamples);
            for (int i = 0; i < spectrum.length; i++) {
                spectrum[i] = 20 * (float) Math.log10(Math.sqrt(complexFFT[i] * complexFFT[i]) + (complexFFT[i + 1] * complexFFT[i + 1]));
            }
        }
        return spectrum;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    private static String getFormattedValue(double value) {
        return DECIMAL_FORMAT.format(value);
    }

}
