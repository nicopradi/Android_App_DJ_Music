/**
 * 
 */
package ch.epfl.sweng.djmusicapp.listeners;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.activities.RoomActivity;

/**
 * TODO delete this class, useless
 * 
 * @author SebastienAndreina
 * 
 *         ListViewListener: when clicked on a button, get the id of the room
 *         and create a roomActivity intent with extra("ID"=room.getID())
 * 
 */
public class ListViewOnItemClickListener implements
        AdapterView.OnItemClickListener {
    private Activity mParentActivity;
    private List<Room> mRooms;

    /**
     * @param parents
     *            Activity that containt the view: the activity will start the
     *            intent
     * @param rooms
     *            List of all the element in the view.
     */
    public ListViewOnItemClickListener(Activity parents, List<Room> rooms) {
        mParentActivity = parents;
        mRooms = rooms;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {

        Intent switchToRoomActivityIntent = new Intent(mParentActivity,
                RoomActivity.class);

        switchToRoomActivityIntent.putExtra("ID", mRooms.get(position).getId());
        mParentActivity.startActivity(switchToRoomActivityIntent);
    }

}
