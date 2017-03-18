package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @auhor georgrem, stockan1
 */

public class AudioFormat implements Parcelable {



    private String audioFormat;

    public AudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    public AudioFormat(Parcel in) {
        audioFormat = in.readString();
    }

    @Override
    public String toString() {
        return audioFormat;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(audioFormat);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<SupportedAudioFormat> CREATOR = new Creator<SupportedAudioFormat>() {
        @Override
        public SupportedAudioFormat createFromParcel(Parcel in) {
            return SupportedAudioFormat.values()[in.readInt()];
        }

        @Override
        public SupportedAudioFormat[] newArray(int size) {
            return new SupportedAudioFormat[size];
        }
    };

}
