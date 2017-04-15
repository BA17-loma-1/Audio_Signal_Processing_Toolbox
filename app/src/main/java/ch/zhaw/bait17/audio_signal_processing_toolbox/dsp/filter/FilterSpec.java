package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * @author georgrem, stockan1
 */

public class FilterSpec implements Parcelable {

    private FilterType filterType;
    private int order;
    private float fpass1;
    private float fpass2;
    private float Apass1;
    private float Apass2;
    private float fstop1;
    private float fstop2;
    private float Astop1;
    private float Astop2;

    private FilterSpec(Builder builder) {
        filterType = builder.filterType;
        order = builder.order;
        fpass1 = builder.fpass1;
        fpass2 = builder.fpass2;
        Apass1 = builder.Apass1;
        Apass2 = builder.Apass2;
        fstop1 = builder.fstop1;
        fstop2 = builder.fstop2;
        Astop1 = builder.Astop1;
        Astop2 = builder.Astop2;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public float getFpass1() {
        return fpass1;
    }

    public float getFpass2() {
        return fpass2;
    }

    public float getApass1() {
        return Apass1;
    }

    public float getApass2() {
        return Apass2;
    }

    public float getFstop1() {
        return fstop1;
    }

    public float getFstop2() {
        return fstop2;
    }

    public float getAstop1() {
        return Astop1;
    }

    public float getAstop2() {
        return Astop2;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    @SuppressLint("DefaultLocale")
    public String getDescription() {
        String output = "";
        switch (filterType) {
            case LOWPASS:
                output = String.format("FIR, order %d, fpass %5.0f Hz, Apass %.2f dB, fstop %5.0f Hz, Astop %3.0f dB",
                        order, fpass1, Apass1, fstop1, Astop1);
                break;
            case HIGHPASS:
                output = String.format("FIR, order %d, fstop %5.0f Hz, Astop %3.0f dB, fpass %5.0f Hz, Apass %.2f dB",
                        order, fstop1, Astop1, fpass1, Apass1);
                break;
            case BANDPASS:
                output = String.format("FIR, order %d, fstop1 %5.0f Hz, Astop1 %3.0f dB, fpass1 %5.0f Hz, Apass %.2f dB, fpass2 %5.0f Hz, fstop2 %5.0f Hz, Astop2 %3.0f dB",
                        order, fstop1, Astop1, fpass1, Apass1, fpass2, fstop2, Astop2);
                break;
            case BANDSTOP:
                output = String.format("FIR, order %d, fpass1 %5.0f Hz, Apass1 %.2f dB, fstop1 %5.0f Hz, Astop %3.0f dB, fstop2 %5.0f Hz, fpass2 %5.0f Hz, Apass2 %.2f dB",
                        order, fpass1, Apass1, fstop1, Astop1, fstop2, fpass2, Apass2);
                break;
            default:
                break;
        }
        return output;
    }

    /**
     * <p>
     *     Builder for FilterSpec objects. Call {@link #build()} after setting all filter properties
     *     to get a FilterSpec object.
     *     See Joshua Bloch, Effective Java, second edition, Upper Saddle River : Addison-Wesley, 2014
     * </p>
     * <p>
     *     {@code FilterSpec} is immutable. The builder's setter methods return the builder itself
     *     so that invocations can be chained.
     * </p>
     */
    public static class Builder {
        // Required parameters
        private final FilterType filterType;
        private int order;

        // Optional parameters (not really ...)
        private float fpass1;
        private float fpass2;
        private float Apass1;
        private float Apass2;
        private float fstop1;
        private float fstop2;
        private float Astop1;
        private float Astop2;

        /**
         * Constructs a builder objects with the required parameters.
         *
         * @param filterType the filter type
         */
        public Builder(@NonNull FilterType filterType, int order) {
            this.filterType = filterType;
            this.order = order;
        }

        /**
         * Frequency at the start of the pass band specified in Hertz [Hz].
         *
         * @param fpass1
         * @return
         */
        public Builder frequencyPassBand1(float fpass1) {
            this.fpass1 = fpass1;
            return this;
        }

        /**
         * Frequency at the end of the pass band specified in Hertz [Hz].
         *
         * @param fpass2
         * @return
         */
        public Builder frequencyPassBand2(float fpass2) {
            this.fpass2 = fpass2;
            return this;
        }

        /**
         * Amount of ripple allowed in the pass band specified in decibels [dB].
         *
         * @param Apass1
         * @return
         */
        public Builder amountRipplePassBand1(float Apass1) {
            this.Apass1 = Apass1;
            return this;
        }

        /**
         * Amount of ripple allowed in the pass band specified in decibels [dB].
         *
         * @param Apass2
         * @return
         */
        public Builder amountRipplePassBand2(float Apass2) {
            this.Apass2 = Apass2;
            return this;
        }

        /**
         * Frequency at the edge of the start of the first stop band specified in Hertz [Hz].
         *
         * @param fstop1
         * @return
         */
        public Builder frequencyStopBand1(float fstop1) {
            this.fstop1 = fstop1;
            return this;
        }

        /**
         * Frequency at the edge of the start of the second stop band specified in Hertz [Hz].
         *
         * @param fstop2
         * @return
         */
        public Builder frequencyStopBand2(float fstop2) {
            this.fstop2 = fstop2;
            return this;
        }

        /**
         * Attenuation in the first stop band specified in decibels [dB].
         *
         * @param Astop1
         * @return
         */
        public Builder attenuationStopBand1(float Astop1) {
            this.Astop1 = Astop1;
            return this;
        }

        /**
         * Attenuation in the second stop band specified in decibels [dB].
         *
         * @param Astop2
         * @return
         */
        public Builder attenuationStopBand2(float Astop2) {
            this.Astop2 = Astop2;
            return this;
        }

        /**
         * <p>
         *     Builds a FilterSpec object and returns it.
         *     Optional parameters for the FilterSpec object must be set via the builder's
         *     setter methods prior to call the build method.
         * </p>
         *
         * @return
         */
        public FilterSpec build() {
            return new FilterSpec(this);
        }

    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.filterType == null ? -1 : this.filterType.ordinal());
        dest.writeInt(this.order);
        dest.writeFloat(this.fpass1);
        dest.writeFloat(this.fpass2);
        dest.writeFloat(this.Apass1);
        dest.writeFloat(this.Apass2);
        dest.writeFloat(this.fstop1);
        dest.writeFloat(this.fstop2);
        dest.writeFloat(this.Astop1);
        dest.writeFloat(this.Astop2);
    }

    protected FilterSpec(Parcel in) {
        int tmpFilterType = in.readInt();
        this.filterType = tmpFilterType == -1 ? null : FilterType.values()[tmpFilterType];
        this.order = in.readInt();
        this.fpass1 = in.readFloat();
        this.fpass2 = in.readFloat();
        this.Apass1 = in.readFloat();
        this.Apass2 = in.readFloat();
        this.fstop1 = in.readFloat();
        this.fstop2 = in.readFloat();
        this.Astop1 = in.readFloat();
        this.Astop2 = in.readFloat();
    }

    public static final Creator<FilterSpec> CREATOR = new Creator<FilterSpec>() {
        @Override
        public FilterSpec createFromParcel(Parcel source) {
            return new FilterSpec(source);
        }

        @Override
        public FilterSpec[] newArray(int size) {
            return new FilterSpec[size];
        }
    };

}
