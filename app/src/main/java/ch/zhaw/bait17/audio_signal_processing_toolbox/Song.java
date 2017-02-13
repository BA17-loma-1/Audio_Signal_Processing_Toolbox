package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * Created by georgrem and stockan1 on 13.02.2017.
 */

public class Song {

    private long id;
    private String title;
    private String artist;

    public Song(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}
