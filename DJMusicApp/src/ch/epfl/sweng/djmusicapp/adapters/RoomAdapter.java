package ch.epfl.sweng.djmusicapp.adapters;

import java.util.List;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.adapters.RoomAdapter.RoomViewHolder;
import ch.epfl.sweng.djmusicapp.ui.CustomTextView;

/**
 * Adapter for RecyclerView
 * 
 * @author csbenz
 * 
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomViewHolder> {

    private OnClickListener mOnClickItemListener;
    private List<Room> mRoomList;

    public RoomAdapter(List<Room> roomList) {
        this.mRoomList = roomList;
    }

    public void setOnClickItemListener(OnClickListener listener) {
        mOnClickItemListener = listener;
    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }

    @Override
    public void onBindViewHolder(RoomViewHolder roomViewHolder, int position) {
        Room room = mRoomList.get(position);

        roomViewHolder.vName.setText(room.getName());
        roomViewHolder.vGenre.setText(room.getGenre());

        String usersNumberString = null;
        int numberUsers = room.getUsers().size();
        if (numberUsers == 1) {
            usersNumberString = numberUsers + " user in this room";
        } else {
            usersNumberString = numberUsers + " users in this room";
        }

        roomViewHolder.vUserNumber.setText(usersNumberString);

        if (room.isCurrentTrackAvailable()) {
            roomViewHolder.vCurrentTrack.setText("Playing now: "
                    + room.getCurrentTrack().getTitle());
        } else {
            roomViewHolder.vCurrentTrack.setText("Playing now: None");
        }

        if (!room.isPublic()) {
            roomViewHolder.vAccessState.setText("Private");
            roomViewHolder.vAccessState.setTextColor(Color.RED);
            roomViewHolder.vAccessState.setVisibility(View.VISIBLE);
        } else {
            roomViewHolder.vAccessState.setVisibility(View.GONE);
        }
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.room_cardview_layout, viewGroup, false);

        itemView.setOnClickListener(mOnClickItemListener);

        return new RoomViewHolder(itemView);
    }

    /**
     * @author csbenz
     * 
     */
    public static class RoomViewHolder extends ViewHolder {

        protected CustomTextView vName;
        protected CustomTextView vGenre;
        protected CustomTextView vUserNumber;
        protected CustomTextView vCurrentTrack;
        protected CustomTextView vAccessState;

        public RoomViewHolder(View v) {
            super(v);

            vName = (CustomTextView) v.findViewById(R.id.roomName);
            vGenre = (CustomTextView) v.findViewById(R.id.roomGenre);
            vUserNumber = (CustomTextView) v.findViewById(R.id.roomUserNumber);
            vCurrentTrack = (CustomTextView) v
                    .findViewById(R.id.roomCurrentTrack);
            vAccessState = (CustomTextView) v
                    .findViewById(R.id.roomAccessState);

            vCurrentTrack.setTypeFaceRobotoRegular(v.getContext());
        }
    }
}
