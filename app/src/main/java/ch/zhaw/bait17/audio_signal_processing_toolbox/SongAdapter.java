package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Song;

public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater inflater;

    public SongAdapter(Context context, ArrayList<Song> songs) {
        this.songs = songs;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.media_list_item, parent, false);
        TextView songView = (TextView) layout.findViewById(R.id.song_title);
        TextView artistView = (TextView) layout.findViewById(R.id.song_artist);
        Song song = songs.get(position);
        songView.setText(song.getTitle());
        artistView.setText(song.getArtist());
        return layout;
    }

}
