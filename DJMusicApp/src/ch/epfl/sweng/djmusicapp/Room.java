package ch.epfl.sweng.djmusicapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author csbenz
 * 
 */
public class Room {

    public enum GENRES {
        POP("Pop"), ROCK("Rock"), CLASSICAL("Classical"), METAL("Metal"), HOUSE(
                "House"), TRANSE("Transe"), TECHNO("Techno"), ELECTRO("Electro"), FOLK(
                "Folk"), JAZZ("Jazz"), COUNTRY("Country"), LATINO("Latino"), OTHERS(
                "Others"), ANYTHING("Anything");

        private String stringValue;

        private GENRES(String stringValue) {
            this.stringValue = stringValue;
        }

        public String toString() {
            return stringValue;
        }
    }

    public enum STATUS {
        PUBLIC(0), PRIVATE(1), BOTH(2);

        private int statusCode;

        STATUS(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    public static final String ROOM_ID_KEY = "room_id";
    public static final String ROOM_NAME_KEY = "room_name";
    public static final String ROOM_GENRE_KEY = "room_genre";
    public static final String ROOM_USERS_KEY = "room_users";
    public static final String ROOM_CURRENT_TRACK_AVAILABLE_KEY = "room_current_track_available";
    public static final String ROOM_CURRENT_TRACK_KEY = "room_current_track";
    public static final String ROOM_CURRENT_TRACK_POS_KEY = "room_current_track_pos";
    public static final String ROOM_STATUS_KEY = "room_status";
    public static final String ROOM_PASSWORD_KEY = "password";

    public static final String PUBLIC_STRING = "Public";
    public static final String PRIVATE_STRING = "Private";

    private String mId;
    private String mName;
    private String mGenre;
    private List<User> mUsers;
    private boolean mCurrentTrackIsAvailable;
    private Track mCurrentTrack;
    private int mCurrentTrackPos;
    private boolean mIsPublic = true;

    /**
     * Complete constructor
     * 
     * @param mId
     * @param name
     * @param genre
     * @param users
     * @param currentTrack
     * @param currentTrackPos
     */
    public Room(String id, String name, String genre, List<User> users,
            boolean isPublic, Track currentTrack, int currentTrackPos) {

        if (id == null || name == null || genre == null || users == null
                || currentTrack == null) {
            throw new NullPointerException();
        }

        if (currentTrackPos < 0 || currentTrackPos > currentTrack.getLength()) {
            throw new IllegalArgumentException();
        }

        this.mId = id;
        this.mName = name;
        this.mGenre = genre;

        this.mUsers = new ArrayList<User>();
        for (int i = 0; i < users.size(); i++) {
            this.mUsers.add(users.get(i));
        }

        this.mIsPublic = isPublic;
        this.mCurrentTrackIsAvailable = true;
        this.mCurrentTrack = currentTrack;
        this.mCurrentTrackPos = currentTrackPos;
    }

    /**
     * Constructor without the currentTrack and currentTrackPos fields.
     * 
     * @param id
     * @param name
     * @param genre
     * @param users
     */
    public Room(String id, String name, String genre, List<User> users,
            boolean isPublic) {

        if (id == null || name == null || genre == null || users == null) {
            throw new NullPointerException();
        }

        this.mId = id;
        this.mName = name;
        this.mGenre = genre;

        this.mUsers = new ArrayList<User>();
        for (int i = 0; i < users.size(); i++) {
            this.mUsers.add(users.get(i));
        }

        this.mCurrentTrackIsAvailable = false;
        this.mIsPublic = isPublic;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getGenre() {
        return mGenre;
    }

    public List<User> getUsers() {
        return new ArrayList<User>(mUsers);
    }

    public boolean isCurrentTrackAvailable() {
        return mCurrentTrackIsAvailable;
    }

    public Track getCurrentTrack() {
        return mCurrentTrack;
    }

    public int getCurrentTrackPos() {
        return mCurrentTrackPos;
    }

    public GENRES[] getMusicGenres() {
        return GENRES.values();
    }

    /**
     * 
     * @return true if the room is public, false if the room is private
     */
    public boolean isPublic() {
        return mIsPublic;
    }

    public String getStatusToString() {
        if (this.isPublic()) {
            return PUBLIC_STRING;
        } else {
            return PRIVATE_STRING;
        }
    }

    public void setPrivate() {
        this.mIsPublic = false;
    }

    /**
     * Get a list of rooms from a JSONArray.
     * 
     * @param jsonArray
     *            list of rooms parsed as JSON
     * @return list of Room(s)
     */
    public static List<Room> createListOfRoomsFromJSONArray(JSONArray jsonArray) {
        if (jsonArray == null) {
            throw new NullPointerException();
        }

        List<Room> rooms = new ArrayList<Room>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject roomJSON = (JSONObject) jsonArray.get(i);
                Room room = fromJson(roomJSON);
                rooms.add(room);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public static Room fromJson(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            throw new NullPointerException();
        }

        String id = jsonObject.getString(ROOM_ID_KEY);
        String name = jsonObject.getString(ROOM_NAME_KEY);
        String genre = jsonObject.getString(ROOM_GENRE_KEY);

        JSONArray usersJSONArray = jsonObject.getJSONArray(ROOM_USERS_KEY);
        List<User> users = User.createListOfUsersFromJSONArray(usersJSONArray);

        int status = jsonObject.getInt(ROOM_STATUS_KEY);
        boolean isPublic = status == 0;

        boolean currentTrackIsAvailable = jsonObject
                .getBoolean(ROOM_CURRENT_TRACK_AVAILABLE_KEY);

        if (currentTrackIsAvailable) {
            JSONObject currentTrackJSONObject = jsonObject
                    .getJSONObject(ROOM_CURRENT_TRACK_KEY);

            Track currentTrack = Track.fromJSON(currentTrackJSONObject);
            int currentTrackPos = 1000 * jsonObject
                    .getInt(ROOM_CURRENT_TRACK_POS_KEY);

            return new Room(id, name, genre, users, isPublic, currentTrack,
                    currentTrackPos);
        } else {
            return new Room(id, name, genre, users, isPublic);
        }
    }

    public JSONObject toJSON() throws JSONException {
        if (mId == null || mName == null || mGenre == null || mUsers == null
                || mCurrentTrack == null) {
            throw new NullPointerException();
        }

        JSONObject roomJson = new JSONObject();

        roomJson.put(ROOM_ID_KEY, mId);
        roomJson.put(ROOM_NAME_KEY, mName);
        roomJson.put(ROOM_GENRE_KEY, mGenre);

        JSONArray usersArray = new JSONArray();
        for (User user : mUsers) {
            JSONObject userJson = user.toJSON();
            usersArray.put(userJson);
        }

        roomJson.put(ROOM_USERS_KEY, usersArray);

        int status = mIsPublic ? 0 : 1;
        roomJson.put(ROOM_STATUS_KEY, status);

        roomJson.put(ROOM_CURRENT_TRACK_AVAILABLE_KEY, mCurrentTrackIsAvailable);

        if (mCurrentTrackIsAvailable) {
            roomJson.put(ROOM_CURRENT_TRACK_KEY, mCurrentTrack.toJSON());
            roomJson.put(ROOM_CURRENT_TRACK_POS_KEY, mCurrentTrackPos);
        }

        return roomJson;
    }

}