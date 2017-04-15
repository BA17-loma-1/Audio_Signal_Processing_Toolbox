package ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;

public class TrackAdapter extends BaseAdapter {

    private static class ViewHolderItem {
        TextView title;
        TextView artist;
    }

    private List<Track> tracks;
    private LayoutInflater inflater;

    public TrackAdapter(List<Track> tracks) {
        this.tracks = tracks;
        inflater = LayoutInflater.from(ApplicationContext.getAppContext());
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Track getItem(int position) {
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
