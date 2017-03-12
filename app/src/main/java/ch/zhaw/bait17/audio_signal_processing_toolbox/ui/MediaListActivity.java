package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import java.util.List;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.TrackAdapter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;

public class MediaListActivity extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    public final static String KEY_TRACK = "ch.zhaw.bait17.audio_signal_processing_toolbox.TRACK";
    public final static String KEY_TRACKS = "ch.zhaw.bait17.audio_signal_processing_toolbox.TRACKS";
    private List<Track> tracks;
    private boolean permissionIsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        tracks = new ArrayList<>();
        String mediumType = getIntent().getStringExtra(MediaBrowserActivity.KEY_MEDIUMTYPE);
        switch (mediumType) {
            case MediaBrowserActivity.SAMPLE:
                tracks = getSongListFromRawFolder();
                break;
            case MediaBrowserActivity.DEVICE:
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE);
                tracks = getSongListFromDevice();
                break;
            default:
                break;
        }

        Collections.sort(tracks, new Comparator<Track>() {
            public int compare(Track a, Track b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        ListView listView = (ListView) findViewById(R.id.media_list);
        TrackAdapter trackAdapter = new TrackAdapter(this, (ArrayList)tracks);
        listView.setAdapter(trackAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Track track = (Track) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(MediaListActivity.this, VisualisationActivity.class);
                int trackPosNr = tracks.indexOf(track);
                intent.putExtra(KEY_TRACK, trackPosNr);  // write the data
                intent.putParcelableArrayListExtra(KEY_TRACKS, (ArrayList)tracks);
                startActivity(intent); // and start the activity

                /*
                CardView controls = (CardView) findViewById(R.id.controls_container);
                RelativeLayout inner = (RelativeLayout) findViewById(R.id.playback_controls);
                TextView title = (TextView) inner.findViewById(R.id.song_title);
                TextView artist = (TextView) inner.findViewById(R.id.song_artist);
                ImageView imageView = (ImageView) findViewById(R.id.album_art);

                controls.setVisibility(VISIBLE);
                title.setText(track.getTitle());
                artist.setText(track.getArtist());

                inner.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaListActivity.this, VisualisationActivity.class);
                        int trackPosNr = tracks.indexOf(track);
                        intent.putExtra(KEY_TRACK, trackPosNr);  // write the data
                        intent.putParcelableArrayListExtra(KEY_TRACKS, (ArrayList)tracks);
                        startActivity(intent); // and start the activity
                    }
                });
                */
            }
        });
    }

    private List<Track> getSongListFromRawFolder() {
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            int rawId = getResources().getIdentifier(field.getName(), "raw", getPackageName());
            if (rawId != 0) {
                TypedValue value = new TypedValue();
                getResources().getValue(rawId, value, true);
                String[] s = value.string.toString().split("/");
                String filename = s[s.length - 1];
                if (filename.endsWith(".wav") || filename.endsWith(".mp3")) {
                    Log.i("filename", filename);
                    filename = filename.split("\\.")[0];
                    tracks.add(getSong(rawId, filename));
                }
            }
        }
        return tracks;
    }

    private Track getSong(int resId, String filename) {
        String title, artist, album, duration;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String uri = "android.resource://" + getPackageName() + File.separator + resId;
        try {
            mmr.setDataSource(getApplicationContext(), Uri.parse(uri));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        title = title == null ? filename : title;
        artist = artist == null ? "<unknown>" : artist;
        album = album == null ? "<unknown>" : album;
        return new Track(title, artist, album, duration, uri.toString());
    }

    private List<Track> getSongListFromDevice() {
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
                String uri = "file:///" + musicCursor.getString(nameColumn);
                if (name.endsWith(".wav")) {
                    tracks.add(new Track(title, artist, album, duration, uri));
                } else if (name.endsWith(".mp3")) {
                    tracks.add(new Track(title, artist, album, duration, uri));
                }
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();
        return tracks;
    }

    private void requestPermission(String permission, Integer requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(MediaListActivity.this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionIsGranted = true;
            } else {
                Toast.makeText(this, "You don't have permission to read from external storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
