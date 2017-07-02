package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.time;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;

/**
 * <p>
 * Based on Professor A D Marshall's (University of Cardiff, UK) MATLAB implementation of a wah wah.
 * </p>
 * <p>
 * Source: <a href="http://users.cs.cf.ac.uk/Dave.Marshall/CM0268/PDF/10_CM0268_Audio_FX.pdf">link</a>
 * </p>
 */

public class WahWah extends AudioEffect {

    private static final String LABEL = "Wah-wah";
    private static final String DESCRIPTION = "A bandpass filter with a time varying centre " +
            "(resonant) frequency and a small bandwidth.";

    private float dampingFactor = 0.05f;  // lower the damping factor the smaller the pass band
    // centre cutoff frequencies of variable bandpass filter
    private int minCenterCutoffFrequency = 500;
    private int maxCenterCutoffFrequency = 3000;
    private float deltaCentreFrequency = 0.1f;  // change in centre frequency per sample (Hz)
    private int whaFrequency = 2000;  // how many Hz per second are cycled through
    private long index = 0;
    private float currentTriangleWaveFrequency;
    private int sampleRate;
    private float yh_previous = 0;
    private float yb_previous = 0;
    private float yl_previous = 0;


    /**
     * * Creates an instance of {@code WahWah}.
     */
    public WahWah() {
        currentTriangleWaveFrequency = minCenterCutoffFrequency;
    }

    protected WahWah(Parcel in) {
        this.dampingFactor = in.readFloat();
        this.minCenterCutoffFrequency = in.readInt();
        this.maxCenterCutoffFrequency = in.readInt();
        this.deltaCentreFrequency = in.readFloat();
        this.whaFrequency = in.readInt();
        this.index = in.readLong();
        this.currentTriangleWaveFrequency = in.readFloat();
        this.sampleRate = in.readInt();
        this.yh_previous = in.readFloat();
        this.yb_previous = in.readFloat();
        this.yl_previous = in.readFloat();
    }

    /**
     * @param input  an array of {@code float} containing the input samples
     *               {@code float} values must be normalised in the range [-1,1]
     * @param output an array of {@code float} of same length as the input samples array
     */
    @Override
    public void apply(@NonNull float[] input, @NonNull float[] output) {
        if (input.length == output.length) {
            float[] yh = new float[input.length];
            float[] yb = new float[input.length];
            float[] yl = new float[input.length];
            float maxYb = 0;

            // difference equation coefficients
            float f1 = (float) (2 * Math.sin((Math.PI * minCenterCutoffFrequency) / sampleRate));
            float q1 = 2 * dampingFactor;       // this dictates size of the pass bands

            for (int i = 0; i < input.length; i++) {

                // create triangle wave of centre frequency value
                if (currentTriangleWaveFrequency < minCenterCutoffFrequency
                        || currentTriangleWaveFrequency > maxCenterCutoffFrequency) {
                    deltaCentreFrequency *= -1; // reverse edge direction
                }
                currentTriangleWaveFrequency += deltaCentreFrequency;

                if (index++ == 0) {
                    // first sample, to avoid referencing of negative signals
                    yh[0] = input[i];
                    yb[0] = f1 * yh[0];
                    yl[0] = f1 * yb[0];
                } else {
                    if (i == 0) {
                        yh[0] = yh_previous;
                        yb[0] = yb_previous;
                        yl[0] = yl_previous;
                    } else {
                        yh[i] = input[i] - yl[i - 1] - q1 * yb[i - 1];
                        yb[i] = f1 * yh[i] + yb[i - 1];
                        yl[i] = f1 * yb[i] + yl[i - 1];

                        f1 = (float) (2 * Math.sin((Math.PI * currentTriangleWaveFrequency) / sampleRate));
                    }

                    if (Math.abs(yb[i]) > maxYb)
                        maxYb = Math.abs(yb[i]);
                }
            }
            yh_previous = yh[input.length - 1];
            yb_previous = yb[input.length - 1];
            yl_previous = yl[input.length - 1];

            for (int i = 0; i < input.length; i++)
                output[i] = yb[i] / maxYb; // apply normalised value
        }
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Sets the sample rate.
     *
     * @param sampleRate sample rate
     */

    public void setSamplingFrequency(int sampleRate) {
        if (sampleRate > 0) {
            this.sampleRate = sampleRate;
            // 0.1 => at 44100 samples per second should mean  4.41kHz triangle wave shift per sec
            deltaCentreFrequency = (float) whaFrequency / sampleRate;
        }
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.dampingFactor);
        dest.writeInt(this.minCenterCutoffFrequency);
        dest.writeInt(this.maxCenterCutoffFrequency);
        dest.writeFloat(this.deltaCentreFrequency);
        dest.writeInt(this.whaFrequency);
        dest.writeLong(this.index);
        dest.writeFloat(this.currentTriangleWaveFrequency);
        dest.writeInt(this.sampleRate);
        dest.writeFloat(this.yh_previous);
        dest.writeFloat(this.yb_previous);
        dest.writeFloat(this.yl_previous);
    }

    public static final Creator<WahWah> CREATOR = new Creator<WahWah>() {
        @Override
        public WahWah createFromParcel(Parcel source) {
            return new WahWah(source);
        }

        @Override
        public WahWah[] newArray(int size) {
            return new WahWah[size];
        }
    };
}
