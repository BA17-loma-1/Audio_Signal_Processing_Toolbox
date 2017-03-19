package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Plain object to hold a audiotrack attributes
 * This is a parcelable object since we need to pass a audiotrack
 * objects between activities
 *
 * @author georgrem, stockan1
 */
public class Track implements Parcelable {

    private String title, artist, album, duration, uri;
    private SupportedAudioFormat audioFormat;


    public Track(String title, String artist, String album,
                 String duration, String uri, SupportedAudioFormat audioFormat) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.uri = uri;
        this.audioFormat = audioFormat;
    }

    /**
     * Use when reconstructing Track object from parcel
     * This will be used only by the 'CREATOR'
     *
     * @param in a parcel to read this object
     */
    public Track(Parcel in) {
        this.title = in.readString();
        this.artist = in.readString();
        this.album = in.readString();
        this.duration = in.readString();
        this.uri = in.readString();
        try {
            this.audioFormat = SupportedAudioFormat.valueOf(in.readString());
        } catch (IllegalArgumentException x) {
            this.audioFormat = null;
        }
    }

    /**
     * Define the kind of object that you gonna parcel
     */
    @Override
    public int describeContents() {
        return hashCode();
    }

    /**
     * Actual object serialization happens here, Write object content
     * to parcel one by one, reading should be done according to this write order
     *
     * @param dest  parcel
     * @param flags Additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(duration);
        dest.writeString(uri);
        dest.writeString(audioFormat.toString());
    }

    /**
     * <p>This field is needed for Android to be able to
     * create new objects, individually or as arrays</p>
     * <p>If you don’t do that, Android framework will throw exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR</p>
     */
    public static final Parcelable.Creator<Track> CREATOR
            = new Parcelable.Creator<Track>() {
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    public String getAudioFormat() {
        return audioFormat.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDuration() {
        return duration;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "Track{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration='" + duration + '\'' +
                ", uri='" + uri + '\'' +
                ", audioFormat=" + audioFormat +
                '}';
    }
}
