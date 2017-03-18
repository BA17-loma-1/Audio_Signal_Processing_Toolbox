package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.AudioFormat;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.SupportedAudioFormat;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;

/**
 * @author georgrem, stockan1
 */

public class MediaListActivity extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    public final static String KEY_TRACK = "ch.zhaw.bait17.audio_signal_processing_toolbox.TRACK";
    public final static String KEY_TRACKS = "ch.zhaw.bait17.audio_signal_processing_toolbox.TRACKS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        loadTrackList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadTrackList();
        } else {
            Toast.makeText(this, "You don't have permission to read from external storage.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTrackList() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            final List<Track> tracks = getAllTracks();
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
                    intent.putExtra(KEY_TRACK, trackPosNr);
                    intent.putParcelableArrayListExtra(KEY_TRACKS, (ArrayList)tracks);
                    startActivity(intent);
                }
            });
        } else {
            requestReadExternalStoragePermission();
        }
    }

    private List<Track> getAllTracks() {
        List<Track> allTracks = new ArrayList<>();
        allTracks.addAll(getTracksFromRawFolder());
        allTracks.addAll(getTracksFromDevice());
        return allTracks;
    }

    private List<Track> getTracksFromRawFolder() {
        List<Track> tracksInRaw = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            int rawId = getResources().getIdentifier(field.getName(), "raw", getPackageName());
            if (rawId != 0) {
                TypedValue value = new TypedValue();
                getResources().getValue(rawId, value, true);
                String[] s = value.string.toString().split("/");
                String filename = s[s.length - 1];
                if (filename.endsWith(SupportedAudioFormat.WAVE.toString())
                        || filename.endsWith(SupportedAudioFormat.MP3.toString())) {
                    Log.i("filename", filename);
                    filename = filename.split("\\.")[0];
                    tracksInRaw.add(getTrack(rawId, filename));
                }
            }
        }
        return tracksInRaw;
    }

    private Track getTrack(int resId, String filename) {
        String title, artist, album, duration, audioFormat;
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
        audioFormat = Track.getSupportedAudioFormats().get(1);
        return new Track(title, artist, album, duration, uri, audioFormat);
    }

    private List<Track> getTracksFromDevice() {
        List<Track> tracksOnDevice = new ArrayList<>();
        List<String> supportedAudioFormats = Track.getSupportedAudioFormats();
        Cursor musicCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int nameColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int mimeTypeColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
            do {
                String name = musicCursor.getString(nameColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                String duration = musicCursor.getString(durationColumn);
                String mimeType = musicCursor.getString(mimeTypeColumn);
                String file = "file:///" + musicCursor.getString(nameColumn);
                if (supportedAudioFormats.contains(mimeType)) {
                    tracksOnDevice.add(new Track(title, artist, album, duration, file, mimeType));
                }
            }
            while (musicCursor.moveToNext());
        }
        if (musicCursor != null) musicCursor.close();
        return tracksOnDevice;
    }

    private void requestReadExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(findViewById(R.id.media_list),
                    "Read permission to external storage is required in order to access your audio files.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MediaListActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(MediaListActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

}
