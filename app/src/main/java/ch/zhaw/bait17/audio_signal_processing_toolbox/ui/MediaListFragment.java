package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.common.base.Joiner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.MediaListType;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.SupportedAudioFormat;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.stream.HttpHandler;
import ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom.TrackAdapter;

/**
 * @author georgrem, stockan1
 */

public class MediaListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = MediaListFragment.class.getSimpleName();
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    private View rootView;
    private Context context;
    private OnTrackSelectedListener listener;
    private List<Track> tracks;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressDialog progressDialog;
    private TrackAdapter trackAdapter;
    private MediaListType mediaListType;
    // URL to get tracks JSON
    private String url;

    public interface OnTrackSelectedListener {
        void onTrackSelected(int trackPos);
    }


    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        // This method avoid to call super.onAttach(context) if I'm not using api 23 or more
        if (Build.VERSION.SDK_INT >= 23) {
            super.onAttach(context);
            onAttachToContext(context);
        }
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(activity);
        }
    }

    /*
     * This method will be called from one of the two previous method
     */
    private void onAttachToContext(Context context) {
        this.context = context;
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
            listener = (OnTrackSelectedListener) activity;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MediaListFragment.OnItemSelectedListener");
        }
    }


    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.media_list_view, container, false);
            init();
            loadTrackList();
        }
        return rootView;
    }

    private void init() {
        tracks = new ArrayList<>();

        // Setup search field
        searchView = (SearchView) rootView.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        // Setup search results list
        recyclerView = (RecyclerView) rootView.findViewById(R.id.media_list);
        trackAdapter = new TrackAdapter(new TrackAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track track) {
                if (listener != null) {
                    int trackPosNr = tracks.indexOf(track);
                    listener.onTrackSelected(trackPosNr);
                }
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ApplicationContext.getAppContext()));
        recyclerView.setAdapter(trackAdapter);
    }


    /*
        This method is not being called by requestReadExternalStoragePermission().
        It requires API level 21 and above.
        Needs a fix to work around.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadTrackList();
        } else {
            Toast.makeText(context, "You don't have permission to read from external storage.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void reloadList() {
        switch (mediaListType) {
            case MY_MUSIC:
                searchView.setVisibility(View.GONE);
                init();
                loadTrackList();
                break;
            case STREAM:
                searchView.setVisibility(View.VISIBLE);
                init();
                break;
            default:
                break;
        }
    }

    private void loadTrackList() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            addAllTracks();
            Collections.sort(tracks);
            trackAdapter.addData(tracks);
        } else {
            requestReadExternalStoragePermission();
        }
    }

    private void addAllTracks() {
        tracks.addAll(getTracksFromRawFolder());
        tracks.addAll(getTracksFromDevice());
    }

    private List<Track> getTracksFromRawFolder() {
        List<Track> tracksInRaw = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            int rawId = getResources().getIdentifier(field.getName(), "raw", context.getPackageName());
            if (rawId != 0) {
                TypedValue value = new TypedValue();
                getResources().getValue(rawId, value, true);
                String[] s = value.string.toString().split("/");
                String filename = s[s.length - 1];
                if (filename.endsWith(SupportedAudioFormat.WAVE.getFileExtension())
                        || filename.endsWith(SupportedAudioFormat.MP3.getFileExtension())) {
                    Log.i("filename", filename);
                    filename = filename.split("\\.")[0];
                    tracksInRaw.add(getTrack(rawId, filename));
                }
            }
        }
        return tracksInRaw;
    }

    private Track getTrack(int resId, String filename) {
        String title, artist, album, duration, mimeType;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String uri = "android.resource://" + context.getPackageName()
                + File.separator + resId;
        try {
            mmr.setDataSource(context, Uri.parse(uri));
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        title = title == null ? filename : title;
        artist = artist == null ? "<unknown>" : artist;
        album = album == null ? "<unknown>" : album;
        SupportedAudioFormat audioFormat = SupportedAudioFormat.getSupportedAudioFormat(mimeType);
        return new Track(title, artist, album, duration, uri, "", audioFormat);
    }

    private List<Track> getTracksFromDevice() {
        List<Track> tracksOnDevice = new ArrayList<>();
        Cursor musicCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int nameColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int mimeTypeColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
            do {
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                String duration = musicCursor.getString(durationColumn);
                String file = "file:///" + musicCursor.getString(nameColumn);
                String mimeType = musicCursor.getString(mimeTypeColumn);
                SupportedAudioFormat audioFormat = SupportedAudioFormat.getSupportedAudioFormat(mimeType);
                if (audioFormat != SupportedAudioFormat.UNKNOWN)
                    tracksOnDevice.add(new Track(title, artist, album, duration, file, "", audioFormat));
            }
            while (musicCursor.moveToNext());
        }
        if (musicCursor != null) musicCursor.close();
        return tracksOnDevice;
    }

    private void requestReadExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(rootView.findViewById(R.id.media_list),
                    "Read permission to external storage is required in order to access your audio files.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public MediaListType getMediaListType() {
        return mediaListType;
    }

    public void setMediaListType(MediaListType mediaListType) {
        this.mediaListType = mediaListType;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            url = "https://api.spotify.com/v1/search?q=" +
                    URLEncoder.encode(query, "UTF-8") +
                    "&type=track";
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UTF-8 encoding from search query failed: " + e.getMessage());
        }
        new GetTracksFromSpotify().execute();

        // Remove old entries
        tracks.clear();
        trackAdapter.clearData();
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetTracksFromSpotify extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject root = (JSONObject) jsonObj.get("tracks");

                    // Getting JSON Array node
                    JSONArray items = root.getJSONArray("items");

                    // looping through All Items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        // Album node is JSON Object
                        JSONObject album = (JSONObject) item.get("album");
                        // Getting Artists and Images JSON Array node
                        JSONArray artists = item.getJSONArray("artists");
                        JSONArray images = album.getJSONArray("images");
                        // Getting first Image node as JSON Object
                        JSONObject image = images.getJSONObject(0);

                        String title = item.getString("name");
                        String albumName = album.getString("name");
                        String preview_url = item.getString("preview_url");
                        String imageUrl = image.getString("url");

                        // looping through All Artists
                        List<String> artistNames = new ArrayList<>();
                        for (int j = 0; j < artists.length(); j++) {
                            JSONObject artist = artists.getJSONObject(j);
                            artistNames.add(artist.getString("name"));
                        }
                        Joiner joiner = Joiner.on(", ");
                        String artistName = joiner.join(artistNames);

                        // tmp Track for single Track
                        Track track = new Track(title, artistName, albumName, "", preview_url,
                                imageUrl, SupportedAudioFormat.MP3);

                        // adding track to track list
                        tracks.add(track);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ApplicationContext.getAppContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ApplicationContext.getAppContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            // Updating parsed JSON data into ListView
            trackAdapter.addData(tracks);
        }

    }
}
