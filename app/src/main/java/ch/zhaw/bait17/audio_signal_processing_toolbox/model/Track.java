package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Plain object to hold a audio track attributes.
 * This is a parcelable object since we need to pass audio track objects between fragments.
 *
 * @author georgrem, stockan1
 */
public class Track implements Comparable<Track>, Parcelable {

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
     * <p>
     *     This field is needed for Android to be able to create new objects,
     *     individually or as arrays.
     * </p>
     * <p>
     *     If you don’t do that, Android framework will throw exception Parcelable protocol
     *     requires a Parcelable.Creator object called CREATOR.
     * </p>
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
        StringBuilder sb = new StringBuilder();
        sb.append("Track{")
            .append("title='" + title)
            .append("', artist='" +  artist)
            .append("', album='" + album)
            .append("', duration='" + duration)
            .append("', uri='" + uri)
            .append("', audioFormat='" + audioFormat)
            .append("'}");
        String s = sb.toString();
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Track)) {
            return false;
        }
        Track track = (Track) o;
        return track.artist.equalsIgnoreCase(artist) && track.title.equalsIgnoreCase(title)
                && track.album.equalsIgnoreCase(album) && track.audioFormat== audioFormat;
    }

    @Override
    public int compareTo(@NonNull Track track) {
        int artistDiff = artist.compareToIgnoreCase(track.artist);
        if (artistDiff != 0) {
            return artistDiff;
        }
        int titleDiff = title.compareToIgnoreCase(track.title);
        if (titleDiff != 0) {
            return titleDiff;
        }
        // Artist and title are equal, compare album name
        return album.compareToIgnoreCase(track.album);
    }

}
