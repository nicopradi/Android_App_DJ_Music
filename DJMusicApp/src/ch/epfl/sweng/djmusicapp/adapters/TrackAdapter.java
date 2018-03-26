package ch.epfl.sweng.djmusicapp.adapters;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.adapters.TrackAdapter.TrackViewHolder;
import ch.epfl.sweng.djmusicapp.ui.CustomTextView;

/**
 * Adapter for RecyclerView
 * 
 * @author csbenz
 * 
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackViewHolder> {

    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;
    private List<Track> mTrackList;

    public TrackAdapter(List<Track> trackList) {
        this.mTrackList = trackList;
    }

    public void setOnClickItemListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnLongClickItemListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    @Override
    public void onBindViewHolder(TrackViewHolder trackViewHolder, int position) {
        Track track = mTrackList.get(position);

        trackViewHolder.vTitle.setText(track.getTitle());
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.track_cardview_layout, viewGroup, false);

        itemView.setOnClickListener(mOnClickListener);
        itemView.setOnLongClickListener(mOnLongClickListener);

        return new TrackViewHolder(itemView);
    }

    /**
     * @author csbenz
     * 
     */
    public static class TrackViewHolder extends ViewHolder {
        protected CustomTextView vTitle;

        public TrackViewHolder(View v) {
            super(v);

            vTitle = (CustomTextView) v.findViewById(R.id.trackTitle);
        }
    }

}
