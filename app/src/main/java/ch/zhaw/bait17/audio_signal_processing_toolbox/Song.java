package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.net.Uri;

public class Song {

    private String title, artist, album, duration;
    private Uri uri;


    public Song(String title, String artist, String album, String duration, Uri uri) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.uri = uri;
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

    public Uri getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration='" + duration + '\'' +
                ", uri=" + uri +
                '}';
    }
}
