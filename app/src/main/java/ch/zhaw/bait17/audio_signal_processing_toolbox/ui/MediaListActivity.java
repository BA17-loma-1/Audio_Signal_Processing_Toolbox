package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.SongAdapter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.WaveDecoder;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Song;

public class MediaListActivity extends AppCompatActivity {

    private ArrayList<Song> songs;
    private Song song;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private boolean permissionIsGranted = false;
    public final static String KEY_SONG = "ch.zhaw.bait17.audio_signal_processing_toolbox.SONG";

    private AudioPlayerService serviceReference;
    private boolean isBound;
    private String TAG = "bound";

    private short[] audioSamples;
    private WaveDecoder decoder;


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


        Log.i(TAG, "Service starting...");
        Intent intent = new Intent(this, AudioPlayerService.class);
        startService(intent);

        ListView listView = (ListView) findViewById(R.id.media_list);
        SongAdapter songAdapter = new SongAdapter(this, songs);
        listView.setAdapter(songAdapter);
        /*
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                song = (Song) adapterView.getItemAtPosition(i);

                CardView controls = (CardView) findViewById(R.id.controls_container);
                RelativeLayout inner = (RelativeLayout) findViewById(R.id.playback_controls);
                TextView title = (TextView) inner.findViewById(R.id.song_title);
                TextView artist = (TextView) inner.findViewById(R.id.song_artist);
                ImageView imageView = (ImageView) findViewById(R.id.album_art);

                controls.setVisibility(VISIBLE);
                title.setText(song.getTitle());
                artist.setText(song.getArtist());

                try (InputStream is = getContentResolver().openInputStream(song.getUri());) {
                    decoder = new WaveDecoder(is);
                    audioSamples = decoder.getShort();
                } catch (IOException | DecoderException e) {
                    Toast.makeText(MediaListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (audioSamples != null && decoder != null && serviceReference != null) {
                    serviceReference.setSamples(decoder.getShort());
                    serviceReference.setSampleRate(decoder.getHeader().getSampleRate());
                    serviceReference.setChannelOut(decoder.getHeader().getChannels());

                    serviceReference.initialize();
                    serviceReference.play();
                }

                inner.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaListActivity.this, VisualisationActivity.class);
                        intent.putExtra(KEY_SONG, song);  // write the data
                        startActivity(intent); // and start the activity
                    }
                });
            }
        });
         */

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
                    Log.i("filename", filename);
                    filename = filename.split("\\.")[0];
                    songs.add(getSong(rawId, filename));
                }
            }
        }
        return songs;
    }

    private Song getSong(int resId, String filename) {
        String title, artist, album, duration;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + File.separator + resId);
        try {
            mmr.setDataSource(getApplicationContext(), soundUri);
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
        return new Song(title, artist, album, duration, soundUri);
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



    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Bound service connected");
            serviceReference = ((AudioPlayerService.LocalBinder) service).getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Problem: bound service disconnected");
            serviceReference = null;
            isBound = false;
        }
    };

    private void doUnbindService() {
        Toast.makeText(this, "Unbinding...", Toast.LENGTH_SHORT).show();
        unbindService(myConnection);
        isBound = false;
    }

    private void doBindToService() {
        Toast.makeText(this, "Binding...", Toast.LENGTH_SHORT).show();
        if (!isBound) {
            Intent bindIntent = new Intent(this, AudioPlayerService.class);
            isBound = bindService(bindIntent, myConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "ch.zhaw.moba2.sandbox.MainActivity - onStart - binding...");
        doBindToService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "ch.zhaw.moba2.sandbox.MainActivity - onStop - unbinding...");
        doUnbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroying activity...");
        if (isFinishing()) {
            Log.i(TAG, "activity is finishing");
            Intent intentStopService = new Intent(this, AudioPlayerService.class);
            stopService(intentStopService);
        }
    }

}
