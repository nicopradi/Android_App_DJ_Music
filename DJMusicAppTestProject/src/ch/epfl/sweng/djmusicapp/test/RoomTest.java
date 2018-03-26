package ch.epfl.sweng.djmusicapp.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.User;

public class RoomTest extends TestCase {

    private List<User> userList;
    private Track track;
    private JSONObject roomJson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        userList = new ArrayList<User>();
        userList.add(new User("3456", "Pradi"));
        userList.add(new User("1234", "Radu"));
        userList.add(new User("99991", "Jerome"));

        track = new Track("http://test.ourserver.com",
                "Piano Concerto #4793 in A Major", "Beethoven", "BestAlbum",
                "Classical", 4000);

        JSONArray usersJsonArray = new JSONArray();
        for (User user : userList) {
            usersJsonArray.put(user.toJSON());
        }

        Log.i("usersJsonArray", usersJsonArray.toString());

        roomJson = new JSONObject();
        roomJson.put(Room.ROOM_ID_KEY, "46009");
        roomJson.put(Room.ROOM_NAME_KEY, "Best Classical Romantic Music");
        roomJson.put(Room.ROOM_GENRE_KEY, "Classical");
        roomJson.put(Room.ROOM_USERS_KEY, usersJsonArray);
        roomJson.put(Room.ROOM_STATUS_KEY, 0);
        roomJson.put(Room.ROOM_CURRENT_TRACK_AVAILABLE_KEY, true);
        roomJson.put(Room.ROOM_CURRENT_TRACK_KEY, track.toJSON());
        roomJson.put(Room.ROOM_CURRENT_TRACK_POS_KEY, 20);

    }

    public void testRoomIDAccess() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        assertEquals("46009", room.getId());
    }

    public void testRoomNameAccess() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        assertEquals("Best Classical Romantic Music", room.getName());
    }

    public void testRoomGenreAccess() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        assertEquals("Classical", room.getGenre());
    }

    public void testRoomUserListAccess() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        assertEquals(userList, room.getUsers());
    }

    public void testRoomIsPublicAccess() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        assertEquals(true, room.isPublic());
    }

    public void testRoomCurrentTrackAccess() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        assertEquals(track, room.getCurrentTrack());
    }

    public void testRoomCurrentTrackPosAccess() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        assertEquals(2000, room.getCurrentTrackPos());
    }

    public void testCurrentTrackPosOutOfRange() {
        // track's length is 4000
        try {
            int currentTrackPos = 8000;
            new Room("46009", "Best Classical Romantic Music", "Classical",
                    userList, true, track, currentTrackPos);
            fail("currentTrackPos greater than actual track length");
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testCurrentTrackPosNegative() {
        try {
            int currentTrackPos = -5000;
            new Room("46009", "Best Classical Romantic Music", "Classical",
                    userList, true, track, currentTrackPos);
            fail("currentTrackPos is negative");
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testUserListArgumentIsImmutable() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        userList.add(new User("98987", "raiju"));

        assertFalse(userList.equals(room.getUsers()));
    }

    public void testUserListmemberIsImmutable() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        try {
            room.getUsers().add(new User("98987", "raiju"));
        } catch (UnsupportedOperationException e) {
            // Success
        }
    }

    public void testRoomToJson() {
        Room room = new Room("46009", "Best Classical Romantic Music",
                "Classical", userList, true, track, 2000);

        try {
            JSONObject roomJson = room.toJSON();
            roomJson.get(Room.ROOM_ID_KEY);
        } catch (JSONException e) {
            fail();
        }
    }

    public void testRoomFromJson() {
        try {
            Room room = Room.fromJson(roomJson);

            assertEquals("46009", room.getId());
            assertEquals("Best Classical Romantic Music", room.getName());
            assertEquals("Classical", room.getGenre());

            assertEquals("3456", room.getUsers().get(0).getId());
            assertEquals("Pradi", room.getUsers().get(0).getUsername());
            assertEquals("1234", room.getUsers().get(1).getId());
            assertEquals("Radu", room.getUsers().get(1).getUsername());
            assertEquals("99991", room.getUsers().get(2).getId());
            assertEquals("Jerome", room.getUsers().get(2).getUsername());

            assertEquals("http://test.ourserver.com", room.getCurrentTrack()
                    .getUrl());
            assertEquals("Piano Concerto #4793 in A Major", room
                    .getCurrentTrack().getTitle());
            assertEquals("Beethoven", room.getCurrentTrack().getArtist());
            assertEquals("BestAlbum", room.getCurrentTrack().getAlbum());
            assertEquals("Classical", room.getCurrentTrack().getGenre());
            assertEquals(4000 * 1000, room.getCurrentTrack().getLength());

            assertEquals(20 * 1000, room.getCurrentTrackPos());

        } catch (JSONException e) {
            fail();
        }
    }
}
