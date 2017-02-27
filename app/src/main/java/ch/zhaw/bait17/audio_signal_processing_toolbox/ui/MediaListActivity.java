package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.SongAdapter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Song;

public class MediaListActivity extends AppCompatActivity {

    private ArrayList<Song> songs;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private boolean permissionIsGranted = false;
    public final static String KEY_SONG = "ch.zhaw.bait17.audio_signal_processing_toolbox.SONG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        songs = new ArrayList<>();
        String mediumType = getIntent().getStringExtra(MediaBrowserActivity.KEY_MEDIUMTYPE);
        switch (mediumType) {
            case MediaBrowserActivity.SAMPLE:
                songs = getSongListFromRawFolder();
                break;
            case MediaBrowserActivity.DEVICE:
                do {
                    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE);
                } while (!permissionIsGranted);
                songs = getSongListFromDevice();
                break;
            default:
                break;
        }

        Collections.sort(songs, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        ListView listView = (ListView) findViewById(R.id.media_list);
        SongAdapter songAdapter = new SongAdapter(this, songs);
        listView.setAdapter(songAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Song song = (Song) adapterView.getItemAtPosition(i);
                // create intent to an other activity
                Intent intent = new Intent(MediaListActivity.this, VisualisationActivity.class);
                intent.putExtra(KEY_SONG, song);  // write the data
                startActivity(intent); // and start the activity
            }
        });
    }

    private ArrayList<Song> getSongListFromRawFolder() {
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            int rawId = getResources().getIdentifier(field.getName(), "raw", getPackageName());
            if (rawId != 0) {
                TypedValue value = new TypedValue();
                getResources().getValue(rawId, value, true);
                String[] s = value.string.toString().split("/");
                String filename = s[s.length - 1];
                if (filename.endsWith(".wav") || filename.endsWith(".mp3")) {
                    Log.i("filename", s[s.length - 1]);
                    songs.add(getSong(rawId));
                }
            }
        }
        return songs;
    }

    private Song getSong(int resId) {
        String title, artist, album, duration;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + File.separator + resId);
        try {
            mmr.setDataSource(getApplicationContext(), soundUri);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        String hasAudio = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
        title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        title = title == null ? "Unknown title" : title;
        artist = artist == null ? "Unknown artist" : artist;
        album = album == null ? "Unknown album" : album;
        return hasAudio == null ? null : new Song(title, artist, album, duration, soundUri);
    }

    private ArrayList<Song> getSongListFromDevice() {
        ContentResolver contentResolver = getContentResolver();
        Cursor musicCursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int nameColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                String name = musicCursor.getString(nameColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                String duration = musicCursor.getString(durationColumn);
                Uri soundUri = Uri.parse("file:///" + musicCursor.getString(nameColumn));
                if (name.endsWith(".wav") || name.endsWith(".mp3")) {
                    songs.add(new Song(title, artist, album, duration, soundUri));
                }
            }
            while (musicCursor.moveToNext());
        }
        return songs;
    }

    private void requestPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MediaListActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MediaListActivity.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MediaListActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MediaListActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            // permission is already granted
            permissionIsGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionIsGranted = true;
                } else {
                    Toast.makeText(this, "You don't have permission to read from external storage.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
