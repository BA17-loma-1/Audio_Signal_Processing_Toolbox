package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MediaListActivity extends AppCompatActivity {

    private static MediaPlayer mediaPlayer;
    private ArrayList<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        mediaPlayer = new MediaPlayer();
        songs = new ArrayList<Song>();

        Intent intent = getIntent();
        String mediumType = intent.getStringExtra(MediaBrowserActivity.KEY_MEDIUMTYPE);
        switch (mediumType) {
            case MediaBrowserActivity.SAMPLE:
                songs = getSongListFromRawFolder();
                break;
            case MediaBrowserActivity.DEVICE:
                Log.i("case: ", "device");
                break;
            default:
                break;
        }

        ListView listView = (ListView) findViewById(R.id.media_list);
    }

    private Song getSong(String name) {
        String title, artist, album, duration;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            AssetFileDescriptor afd = getAssets().openFd(name);
            mmr.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        try {
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            title = "Unknown";
            artist = "Unknown";
            album = "Unknown";
            duration = "Unknown";
        }
        return new Song(name, title, artist, album, duration);
    }

    private ArrayList<Song> getSongListFromRawFolder() {
        AssetManager assetManager = getApplicationContext().getAssets();
        try {
            for (String filename : assetManager.list("")) {
                if (filename.matches(".*([wav|mp3])")) {
                    Song song = getSong(filename);
                    songs.add(song);
                    Log.i("printout: ", song.toString());
                }
            }
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return songs;
    }

}
