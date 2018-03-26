package ch.epfl.sweng.djmusicapp.data;

import java.util.List;

import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.User;

/**
 * @author csbenz
 * 
 */
public interface ServerContacterInterface {

    /**
     * Login to the service. A user must be logged in before contacting the
     * server for anything else.
     * 
     * @param user
     *            a User object uniquely defining the user
     * @param loginCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void login(final User user, final LoginCallback loginCallback);

    /**
     * Logout from the service. Must be called when the user has finished with
     * the app.
     * 
     * @param userId
     *            the id uniquely defining the user
     * @param logoutCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void logout(final String userId, final LogoutCallback logoutCallback);

    /**
     * Get a list of the available rooms.
     * 
     * @param roomStatus
     *            status of the room: 0 if public, 1 if private
     * @param getRoomsCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void getRooms(final int roomStatus,
            final GetRoomsCallback getRoomsCallback);

    /**
     * Get a room from its id.
     * 
     * @param roomId
     *            id of the room
     * @param getRoomCallBack
     *            callback function called when the request response for this
     *            command is received
     */
    public void getRoom(final String roomId,
            final GetRoomCallBack getRoomCallBack);

    /**
     * Create a room with a user-chosen name and music genre.
     * 
     * @param userId
     *            id of the user adding the room
     * @param roomName
     *            desired name of the room
     * @param roomGenre
     *            music genre of the room
     * @param roomStatus
     *            status of the room: 0 if public, 1 if private
     * @param password
     *            password of the room, only needed if the room is private
     * @param createRoomCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void addRoom(final String userId, final String roomName,
            final String roomGenre, final int roomStatus,
            final String password, final AddRoomCallback createRoomCallback);

    /**
     * Update a room.
     * 
     * @param userId
     *            id of the user updating the room
     * @param roomId
     *            id of the room to update
     * @param roomName
     *            new name of the room
     * @param roomGenre
     *            new genre of the room
     * @param updateRoomCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void updateRoom(final String userId, final String roomId,
            final String roomName, final String roomGenre,
            final UpdateRoomCallback updateRoomCallback);

    /**
     * Delete a room
     * 
     * @param userId
     *            id of the user deleting the room
     * @param roomId
     *            id of the room to delete
     * @param deleteRoomCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void deleteRoom(final String userId, final String roomId,
            final DeleteRoomCallback deleteRoomCallback);

    /**
     * Subscribe to a given room.
     * 
     * @param roomId
     *            id of the room to subscribe to
     * @param userId
     *            id of the user subscribing to the room
     * @param password
     *            password of the room, if the room is private
     * @param subscribeRoomCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void subscribeRoom(final String roomId, final String userId,
            final String password,
            final SubscribeRoomCallback subscribeRoomCallback);

    /**
     * Unsubscribe from a room.
     * 
     * @param unSubscribeRoomCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void unsubscribeRoom(final String roomId, final String userId,
            final UnSubscribeRoomCallback unSubscribeRoomCallback);

    /**
     * Get the list of Users of the given room.
     * 
     * @param roomId
     *            id of the room
     * @param getUsersCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void getUsers(final String roomId,
            final GetUsersCallback getUsersCallback);

    /**
     * Get the current and next tracks of a room
     * 
     * @param roomId
     *            id of the room from which to get the tracks
     * @param getCurrentAndNextTracksCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void getCurrentAndNextTracks(
            final String roomId,
            final boolean syncMode,
            final GetCurrentAndNextTracksCallback getCurrentAndNextTracksCallback);

    /**
     * Add a list of tracks to a given room.
     * 
     * @param userId
     *            id of the user that is adding the tracks
     * @param roomId
     *            id of the room
     * @param tracks
     *            list of Tracks to add
     * @param addTracksCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void addTracks(final String userId, final String roomId,
            final List<Track> tracks, final AddTracksCallback addTracksCallback);

    /**
     * Vote for the current track of a room
     * 
     * @param userId
     *            id of the user that is voting
     * @param roomId
     *            id of the room in which to vote for the current track
     * @param vote
     *            a value in the set {-1, 0, 1}, representing the vote value for
     *            the current track
     * @param voteCurrentTrackCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void voteCurrentTrack(final String userId, final String roomId,
            final int vote,
            final VoteCurrentTrackCallback voteCurrentTrackCallback);

    /**
     * Skip the current track (must be an admin of the room)
     * 
     * @param userId
     *            id of the user skipping the track
     * @param roomId
     *            id of the room in which to skip the current track
     * @param skipTrackCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void skipTrack(final String userId, final String roomId,
            final SkipTrackCallback skipTrackCallback);

    /**
     * Synchronize with the server
     * 
     * @param syncWithServerCallback
     *            callback function called when the request response for this
     *            command is received
     */
    public void syncWithServer(
            final SyncWithServerCallback syncWithServerCallback);

    /**
     * Callback interfaces used in above static methods.
     * 
     * @author csbenz
     * 
     */
    public interface LoginCallback {
        public void onLogin();

        public void onFail(String errorMessage);
    }

    public interface LogoutCallback {
        public void onLogout();

        public void onFail(String errorMessage);
    }

    public interface GetRoomsCallback {
        public void onGotRooms(List<Room> rooms);

        public void onFail(String errorMessage);
    }

    public interface GetRoomCallBack {
        public void onGotRoom(Room room);

        public void onFail(String errorMessage);
    }

    public interface AddRoomCallback {
        public void onRoomAdded(String roomId);

        public void onFail(String errorMessage);
    }

    public interface UpdateRoomCallback {
        public void onRoomUpdated();

        public void onFail(String errorMessage);
    }

    public interface DeleteRoomCallback {
        public void onRoomDeleted();

        public void onFail(String errorMessage);
    }

    public interface SubscribeRoomCallback {
        public void onSubscribedRoom(boolean isAdmin);

        public void onFail(String errorMessage);
    }

    public interface UnSubscribeRoomCallback {
        public void onUnSubscribedRoom();

        public void onFail(String errorMessage);
    }

    public interface GetUsersCallback {
        public void onGotUsers(List<User> users);

        public void onFail(String errorMessage);
    }

    public interface GetCurrentAndNextTracksCallback {
        public void onGotCurrentAndNextTracks(Track currentTrack,
                int currentTrackPos, Track nextTrack);

        public void onFail(String errorMEssage);
    }

    public interface AddTracksCallback {
        public void onAddedTracks();

        public void onFail(String errorMessage);
    }

    public interface VoteCurrentTrackCallback {
        public void onVotedCurrentTrackCallback();

        public void onFail(String errorMessage);
    }

    public interface SkipTrackCallback {
        public void onSkippedTrack();

        public void onFail(String errorMessage);
    }

    public interface SyncWithServerCallback {
        public void onSyncedWithServer(int secondsFromSync);

        public void onFail(String errorMessage);
    }

}
