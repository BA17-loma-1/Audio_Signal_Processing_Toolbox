package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;

public class TrackAdapter extends BaseAdapter {

    static class ViewHolderItem {
        TextView title;
        TextView artist;
    }

    private ArrayList<Track> tracks;
    private LayoutInflater inflater;

    public TrackAdapter(ArrayList<Track> tracks) {
        this.tracks = tracks;
        inflater = LayoutInflater.from(ApplicationContext.getAppContext());
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Android ViewHolder Pattern for smoother scrolling
        ViewHolderItem viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.media_list_item, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.artist = (TextView) convertView.findViewById(R.id.track_artist);
            viewHolder.title = (TextView) convertView.findViewById(R.id.track_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        Track track = tracks.get(position);
        if (track != null) {
            viewHolder.artist.setText(track.getArtist());
            viewHolder.title.setText(track.getTitle());
        }
        return convertView;
    }

}
