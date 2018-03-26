package ch.epfl.sweng.djmusicapp.data;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.User;

/**
 * Offers a very simple dummy ServerContacter for testing purpose.
 * 
 * @author csbenz
 * @deprecated not maintained anymore with new API commands
 */
public abstract class DummyServerContacter implements ServerContacterInterface {

    private static DummyServerContacter instance;

    private static int idCount = 1000;
    private static List<Room> rooms = new ArrayList<Room>();
    // private static String subscribedRoomId = null;
    // private static boolean alreadyCreated = false;
    private static Track initialTrack;

    /**
     * Constructor
     */
    private DummyServerContacter() {
        setUp();
    }

    public static ServerContacterInterface getInstance() {
        if (instance == null) {
            // instance = new DummyServerContacter();
        }

        return instance;
    }

    /**
     * Initial setup for the class. Only called once.
     */
    private void setUp() {
        initialTrack = new Track("www.myserver.com", "initial track",
                "initial track artist", "initial track album name", "a genre",
                24000);

        List<User> usersList1 = new ArrayList<User>();
        usersList1.add(new User("0987", "Kate"));
        usersList1.add(new User("142857", "Alex"));
        usersList1.add(new User("1664", "Virgile"));

        List<User> usersList2 = new ArrayList<User>();
        usersList2.add(new User("8393292", "Seb"));
        usersList2.add(new User("929209439", "Nico"));
        usersList2.add(new User("0383282", "Jerome"));
        usersList2.add(new User("1326076", "Radu"));
        usersList2.add(new User("99383989", "Tristan"));
        usersList2.add(new User("459503", "Keyan"));
        usersList2.add(new User("1918376", "Christo"));

        Track track = new Track("GRxofEmo3HA", "Piano Concerto in E major",
                "Dr Kaboom", "Albumz", "Classical", 18000000);

        Track track2 = new Track("oZ1CE1qAjA8", "Jazzy airs", "Clayton Wardon",
                "A foul's soul", "Jazz", 4050000);

        Track track3 = new Track("iNk9_bUKYx0", "Black dawn",
                "Time Destructor", "My life", "Metal", 123000);

        rooms.add(new Room("1234", "Best Room", "Classical", usersList1, true,
                track, 1258000));

        rooms.add(new Room("9871236565", "Jazz and soul Room", "Jazz",
                usersList2, true, track2, 40500));

        rooms.add(new Room("859595", "Metal Kill", "Metal", usersList2, true,
                track3, 13000));

        rooms.add(new Room("12344", "For Old Guys", "Classical", usersList1,
                true, track, 1258000));

        rooms.add(new Room("98712365655", "The Fun Room", "Pop", usersList2,
                true, track2, 40500));

        rooms.add(new Room("8595955", "All Chill Music", "Jazz", usersList2,
                true, track3, 13000));
    }

    /*
     * 
     * Just calls the callback. Doesn't do anything on the "server" side.
     * 
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#login(ch.epfl.sweng
     * .djmusicapp.User,
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface.LoginCallback)
     */
    @Override
    public void login(User user, LoginCallback loginCallback) {

        loginCallback.onLogin();
    }

    /*
     * Just calls the callback. Doesn't do anything on the "server" side.
     * 
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#logout(java.lang.String
     * , ch.epfl.sweng.djmusicapp.ServerContacterInterface.LogoutCallback)
     */
    @Override
    public void logout(String userId, LogoutCallback logoutCallback) {

        logoutCallback.onLogout();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#getRooms(ch.epfl.sweng
     * .djmusicapp.ServerContacterInterface.GetRoomsCallback)
     */
    @Override
    public void getRooms(int roomStatus, GetRoomsCallback getRoomsCallback) {
        getRoomsCallback.onGotRooms(rooms);

    }

    @Override
    public void getRoom(String roomId, GetRoomCallBack getRoomCallback) {

        for (Room room : rooms) {
            if (room.getId().equals(roomId)) {
                getRoomCallback.onGotRoom(room);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#createRoom(java.lang
     * .String, java.lang.String,
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface.CreateRoomCallback)
     */
    @Override
    public void addRoom(String userId, String roomName, String roomGenre,
            int roomStatus, String password, AddRoomCallback createRoomCallback) {

        String id = String.valueOf(++idCount);
        rooms.add(new Room(id, roomName, roomGenre, new ArrayList<User>(),
                true, initialTrack, 0));

        createRoomCallback.onRoomAdded(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#subscribeRoom(long,
     * java.lang.String,
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface.SubscribeRoomCallback)
     */
    @Override
    public void subscribeRoom(String roomId, String userId, String password,
            SubscribeRoomCallback subscribeRoomCallback) {

        // subscribedRoomId = roomId;

        subscribeRoomCallback.onSubscribedRoom(false);

    }

    /*
     * Just calls the callback. Doesn't do anything on the "server" side.
     * 
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#unsubscribeRoom(ch.
     * epfl.sweng.djmusicapp.ServerContacterInterface.UnSubscribeRoomCallback)
     */
    @Override
    public void unsubscribeRoom(String roomId, String userId,
            UnSubscribeRoomCallback unSubscribeRoomCallback) {

        // subscribedRoomId = null;

        unSubscribeRoomCallback.onUnSubscribedRoom();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#addTracks(java.lang
     * .String, long, java.util.List,
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface.AddTracksCallback)
     */
    @Override
    public void addTracks(String userId, String roomId, List<Track> tracks,
            AddTracksCallback addTracksCallback) {
        // TODO Auto-generated method stub

    }

    /*
     * Returns a list of the Users in the Room (through the callback passed as
     * an argument). (non-Javadoc)
     * 
     * @see ch.epfl.sweng.djmusicapp.ServerContacterInterface#getUsers(long,
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface.GetUsersCallback)
     */
    @Override
    public void getUsers(String roomId, GetUsersCallback getUsersCallback) {
        for (Room room : rooms) {
            if (room.getId().equals(roomId)) {
                getUsersCallback.onGotUsers(room.getUsers());
            }
        }
    }

    @Override
    public void updateRoom(String userId, String roomId, String roomName,
            String roomGenre, UpdateRoomCallback updateRoomCallback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteRoom(String userId, String roomId,
            DeleteRoomCallback deleteRoomCallback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getCurrentAndNextTracks(String roomId, boolean syncMode,
            GetCurrentAndNextTracksCallback getCurrentAndNextTracksCallback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void voteCurrentTrack(String userId, String roomId, int vote,
            VoteCurrentTrackCallback voteCurrentTrackCallback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void skipTrack(String userId, String roomId,
            SkipTrackCallback skipTrackCallback) {
        // TODO Auto-generated method stub

    }
}
