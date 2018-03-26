package ch.epfl.sweng.djmusicapp.data;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.User;
import ch.epfl.sweng.djmusicapp.data.ContactServerAysncTask.HttpCallback;

/**
 * This class contains static methods to send requests to the server. Each
 * method asks for a callback function that will be called when the request
 * response is received.
 * 
 * @author csbenz, Tristan Marchal, SebastienAndreina
 */
public class ServerContacter implements ServerContacterInterface {

    private static final String SERVER_URL = "http://djmusic.rightlight.fr/";

    // List of commands (URLs) that the client can send to the server.
    private static final String COMMAND_LOGIN = "login";
    private static final String COMMAND_LOGOUT = "logout";
    private static final String COMMAND_GET_ROOMS = "getRooms";
    private static final String COMMAND_GET_ROOM = "getRoom";
    private static final String COMMAND_ADD_ROOM = "addRoom";
    private static final String COMMAND_UPDATE_ROOM = "updateRoom";
    private static final String COMMAND_DELETE_ROOM = "deleteRoom";
    private static final String COMMAND_SUBSCRIBE_ROOM = "subscribeRoom";
    private static final String COMMAND_UNSUBSCRIBE_ROOM = "unSubscribeRoom";
    private static final String COMMAND_GET_USERS = "getUsers";
    private static final String COMMAND_GET_CURRENT_AND_NEXT_TRACKS = "getCurrentAndNextTracks";
    private static final String COMMAND_ADD_TRACKS = "addTracks";
    private static final String COMMAND_VOTE_CURRENT_TRACK = "voteCurrentTrack";
    private static final String COMMAND_SKIP_TRACK = "skipTrack";
    private static final String COMMAND_SYNC_WITH_SERVER = "syncWithServer";

    // JSON keys to retrieve or put objects in JSONObjects
    private static final String CONFIRMATION_CODE_KEY = "confirmation";
    private static final String ROOMS_JSONARRAY_KEY = "rooms";
    private static final String ROOM_KEY = "room";
    private static final String TRACKS_JSONARRAY_KEY = "tracks";
    private static final String USERS_JSONARRAY_KEY = "users";
    private static final String NOTE_KEY = "note";
    private static final String SECONDS_FROM_SYNC_KEY = "seconds_from_sync";

    private static final String CONFIRMATION_CODE_OK = "OK";
    private static final String CONFIRMATION_CODE_WARNING = "WARNING";
    private static final String ERROR_MESSAGE_KEY = "error_msg";

    private static ServerContacter mInstance;

    private ServerContacter() {
        // Empty
    }

    public static ServerContacterInterface getInstance() {
        if (mInstance == null) {
            mInstance = new ServerContacter();
        }

        return mInstance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#login(ch.epfl.sweng
     * .djmusicapp.User,
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface.LoginCallback)
     */
    @Override
    public void login(final User user, final LoginCallback loginCallback) {

        if (user == null || loginCallback == null) {
            throw new NullPointerException();
        }

        try {
            JSONObject userJson = user.toJSON();

            sendRequestHelper(COMMAND_LOGIN, userJson,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            loginCallback.onLogin();
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            loginCallback.onFail(errorMessage);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            loginCallback.onFail("error parsing user to JSON");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#logout(java.lang.String
     * , ch.epfl.sweng.djmusicapp.ServerContacterInterface.LogoutCallback)
     */
    @Override
    public void logout(final String userId, final LogoutCallback logoutCallback) {

        if (userId == null || logoutCallback == null) {
            throw new NullPointerException("null argument");
        }

        try {
            JSONObject userIdJson = new JSONObject();
            userIdJson.put(User.USER_ID_KEY, userId);

            sendRequestHelper(COMMAND_LOGOUT, userIdJson,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            logoutCallback.onLogout();
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            logoutCallback.onFail(errorMessage);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            logoutCallback.onFail("error parsing userId to JSON");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#getRooms(ch.epfl.sweng
     * .djmusicapp.ServerContacterInterface.GetRoomsCallback)
     */
    @Override
    public void getRooms(final int roomStatus,
            final GetRoomsCallback getRoomsCallback) {

        if (getRoomsCallback == null) {
            throw new NullPointerException("getRoomsCallback is null");
        }

        // if (roomStatus < 0 || roomStatus > 2) {
        // throw new IllegalArgumentException();
        // }

        try {
            JSONObject jsonRequestParams = new JSONObject();
            jsonRequestParams.put(Room.ROOM_STATUS_KEY, roomStatus);

            sendRequestHelper(COMMAND_GET_ROOMS, jsonRequestParams,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            try {
                                JSONArray roomsJSONArray = jsonResponse
                                        .getJSONArray(ROOMS_JSONARRAY_KEY);

                                List<Room> rooms = Room
                                        .createListOfRoomsFromJSONArray(roomsJSONArray);

                                getRoomsCallback.onGotRooms(rooms);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                executeOnFail("Failed to parse JSON response");
                            }
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            getRoomsCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            getRoomsCallback.onFail("Failed to parse roomStatus to JSON");
        }
    }

    @Override
    public void getRoom(final String roomId,
            final GetRoomCallBack getRoomCallBack) {

        if (roomId == null || getRoomCallBack == null) {
            throw new NullPointerException("null argument");
        }

        try {
            JSONObject jsonRequestParams = new JSONObject();
            jsonRequestParams.put(Room.ROOM_ID_KEY, roomId);

            sendRequestHelper(COMMAND_GET_ROOM, jsonRequestParams,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            try {
                                JSONObject roomJson = jsonResponse
                                        .getJSONObject(ROOM_KEY);
                                Room room = Room.fromJson(roomJson);
                                getRoomCallBack.onGotRoom(room);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                executeOnFail("Failed to parse JSON response");
                            }
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            getRoomCallBack.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            getRoomCallBack.onFail("Failed to parse roomId to JSON");
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
    public void addRoom(String userId, final String roomName,
            final String roomGenre, final int roomStatus,
            final String password, final AddRoomCallback createRoomCallback) {

        if (userId == null || roomName == null || password == null
                || roomGenre == null) {
            throw new NullPointerException("null argument");
        }

        if (roomStatus != 0 && roomStatus != 1) {
            throw new IllegalArgumentException();
        }

        try {

            JSONObject jsonRequestParams = new JSONObject();
            jsonRequestParams.put(User.USER_ID_KEY, userId);
            jsonRequestParams.put(Room.ROOM_NAME_KEY, roomName);
            jsonRequestParams.put(Room.ROOM_GENRE_KEY, roomGenre);
            jsonRequestParams.put(Room.ROOM_STATUS_KEY, roomStatus);
            jsonRequestParams.put("password", password);

            sendRequestHelper(COMMAND_ADD_ROOM, jsonRequestParams,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            try {
                                String roomId = jsonResponse
                                        .getString(Room.ROOM_ID_KEY);
                                createRoomCallback.onRoomAdded(roomId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                executeOnFail("Failed to parse JSON response");
                            }
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            createRoomCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            createRoomCallback.onFail("Failed to parse arguments to JSON");
        }
    }

    @Override
    public void updateRoom(final String userId, final String roomId,
            final String roomName, final String roomGenre,
            final UpdateRoomCallback updateRoomCallback) {

        if (userId == null || roomId == null || roomName == null
                || roomGenre == null) {
            throw new NullPointerException("null argument");
        }

        try {
            JSONObject jsonRequestParams = new JSONObject();
            jsonRequestParams.put(User.USER_ID_KEY, userId);
            jsonRequestParams.put(Room.ROOM_ID_KEY, roomId);
            jsonRequestParams.put(Room.ROOM_NAME_KEY, roomName);
            jsonRequestParams.put(Room.ROOM_GENRE_KEY, roomGenre);

            sendRequestHelper(COMMAND_UPDATE_ROOM, jsonRequestParams,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            updateRoomCallback.onRoomUpdated();
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            updateRoomCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            updateRoomCallback.onFail("failed to parse arguments to JSON");
        }
    }

    @Override
    public void deleteRoom(final String userId, final String roomId,
            final DeleteRoomCallback deleteRoomCallback) {

        if (userId == null || roomId == null || deleteRoomCallback == null) {
            throw new NullPointerException("null argument");
        }

        try {
            JSONObject jsonRequestParams = new JSONObject();
            jsonRequestParams.put(User.USER_ID_KEY, userId);
            jsonRequestParams.put(Room.ROOM_ID_KEY, roomId);

            sendRequestHelper(COMMAND_DELETE_ROOM, jsonRequestParams,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            deleteRoomCallback.onRoomDeleted();
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            deleteRoomCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            deleteRoomCallback.onFail("failed to parse arguments to JSON");
        }
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
    public void subscribeRoom(final String roomId, final String userId,
            final String password,
            final SubscribeRoomCallback subscribeRoomCallback) {

        if (roomId == null) {
            throw new NullPointerException("roomId is null");
        }

        if (userId == null) {
            throw new NullPointerException("userId is null");
        }

        if (password == null) {
            throw new NullPointerException("password is null");
        }

        if (subscribeRoomCallback == null) {
            throw new NullPointerException(
                    "subscribeRoomCallback argument is null");
        }

        try {
            JSONObject jsonRequestParams = new JSONObject();
            jsonRequestParams.put(Room.ROOM_ID_KEY, roomId);
            jsonRequestParams.put(User.USER_ID_KEY, userId);
            jsonRequestParams.put(Room.ROOM_PASSWORD_KEY, password);

            sendRequestHelper(COMMAND_SUBSCRIBE_ROOM, jsonRequestParams,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            boolean isAdmin = false;
                            try {
                                isAdmin = jsonResponse.getInt("is_admin") == 1;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            subscribeRoomCallback.onSubscribedRoom(isAdmin);
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            subscribeRoomCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            subscribeRoomCallback.onFail("failed to parse arguments to JSON");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface#unsubscribeRoom(ch.
     * epfl.sweng.djmusicapp.ServerContacterInterface.UnSubscribeRoomCallback)
     */
    @Override
    public void unsubscribeRoom(final String roomId, final String userId,
            final UnSubscribeRoomCallback unSubscribeRoomCallback) {

        if (roomId == null || userId == null || unSubscribeRoomCallback == null) {
            throw new NullPointerException("null argument");
        }

        try {
            JSONObject jsonRequestParams = new JSONObject();
            jsonRequestParams.put(Room.ROOM_ID_KEY, roomId);
            jsonRequestParams.put(User.USER_ID_KEY, userId);

            sendRequestHelper(COMMAND_UNSUBSCRIBE_ROOM, jsonRequestParams,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            unSubscribeRoomCallback.onUnSubscribedRoom();
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            unSubscribeRoomCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            unSubscribeRoomCallback.onFail("failed to parse arguments to JSON");
        }
    }

    @Override
    public void getCurrentAndNextTracks(
            final String roomId,
            final boolean syncMode,
            final GetCurrentAndNextTracksCallback getCurrentAndNextTracksCallback) {

        if (roomId == null || getCurrentAndNextTracksCallback == null) {
            throw new NullPointerException("null argument");
        }

        try {
            JSONObject jsonRequestParams = new JSONObject();
            jsonRequestParams.put(Room.ROOM_ID_KEY, roomId);
            jsonRequestParams.put("sync_mode", Boolean.toString(syncMode));

            sendRequestHelper(COMMAND_GET_CURRENT_AND_NEXT_TRACKS,
                    jsonRequestParams, new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            try {
                                Track currentTrack = null;
                                long currentTrackPos = 0;
                                Track nextTrack = null;

                                boolean currentTrackAvailable = jsonResponse
                                        .getBoolean("current_track_available");

                                if (currentTrackAvailable) {
                                    currentTrack = Track.fromJSON(jsonResponse
                                            .getJSONObject("current_track"));
                                    currentTrackPos = jsonResponse
                                            .getLong("current_track_pos");
                                }

                                boolean nextTrackAvailable = jsonResponse
                                        .getBoolean("next_track_available");

                                if (nextTrackAvailable) {
                                    nextTrack = Track.fromJSON(jsonResponse
                                            .getJSONObject("next_track"));
                                }

                                getCurrentAndNextTracksCallback
                                        .onGotCurrentAndNextTracks(
                                                currentTrack,
                                                secToMillis(currentTrackPos),
                                                nextTrack);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                executeOnFail("Failed to parse JSON response");
                            }
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            getCurrentAndNextTracksCallback
                                    .onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            getCurrentAndNextTracksCallback
                    .onFail("failed to parse arguments to JSON");
        }
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
    public void addTracks(final String userId, final String roomId,
            final List<Track> tracks, final AddTracksCallback addTracksCallback) {

        if (userId == null || roomId == null || tracks == null
                || addTracksCallback == null) {
            throw new NullPointerException("null argument");
        }

        try {
            JSONObject jsonRequestParams = new JSONObject();
            jsonRequestParams.put(User.USER_ID_KEY, userId);
            jsonRequestParams.put(Room.ROOM_ID_KEY, Integer.parseInt(roomId));

            JSONArray tracksJsonArray = new JSONArray();
            for (Track track : tracks) {
                tracksJsonArray.put(track.toJSON());
            }
            jsonRequestParams.put(TRACKS_JSONARRAY_KEY, tracksJsonArray);

            sendRequestHelper(COMMAND_ADD_TRACKS, jsonRequestParams,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            addTracksCallback.onAddedTracks();
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            addTracksCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            addTracksCallback.onFail("failed to parse arguments to JSON");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.sweng.djmusicapp.ServerContacterInterface#getUsers(long,
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface.GetUsersCallback)
     */
    @Override
    public void getUsers(final String roomId,
            final GetUsersCallback getUsersCallback) {

        if (roomId == null || getUsersCallback == null) {
            throw new NullPointerException("null argument");
        }

        try {
            JSONObject jsonRequestObject = new JSONObject();
            jsonRequestObject.put(Room.ROOM_ID_KEY, roomId);

            sendRequestHelper(COMMAND_GET_USERS, jsonRequestObject,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            try {
                                JSONArray usersJSONArray = jsonResponse
                                        .getJSONArray(USERS_JSONARRAY_KEY);

                                List<User> users = User
                                        .createListOfUsersFromJSONArray(usersJSONArray);

                                getUsersCallback.onGotUsers(users);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                executeOnFail("Failed to parse JSON response");
                            }
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            getUsersCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            getUsersCallback.onFail("failed to parse arguments to JSON");
        }
    }

    @Override
    public void voteCurrentTrack(final String userId, final String roomId,
            int vote, final VoteCurrentTrackCallback voteCurrentTrackCallback) {

        if (userId == null || roomId == null
                || voteCurrentTrackCallback == null) {
            throw new NullPointerException("null argument");
        }

        if (vote < -2 || vote > 2) {
            throw new IllegalArgumentException(
                    "vote must be a value int set {-2, -1, 0, 1, 2}");
        }

        try {
            JSONObject jsonRequestObject = new JSONObject();
            jsonRequestObject.put(User.USER_ID_KEY, userId);
            jsonRequestObject.put(Room.ROOM_ID_KEY, roomId);
            jsonRequestObject.put(NOTE_KEY, vote);

            sendRequestHelper(COMMAND_VOTE_CURRENT_TRACK, jsonRequestObject,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            voteCurrentTrackCallback
                                    .onVotedCurrentTrackCallback();
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            voteCurrentTrackCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            voteCurrentTrackCallback
                    .onFail("failed to parse arguments to JSON");
        }
    }

    @Override
    public void skipTrack(final String userId, final String roomId,
            final SkipTrackCallback skipTrackCallback) {

        if (userId == null || roomId == null || skipTrackCallback == null) {
            throw new NullPointerException("null argument");
        }

        try {
            JSONObject jsonRequestObject = new JSONObject();
            jsonRequestObject.put(User.USER_ID_KEY, userId);
            jsonRequestObject.put(Room.ROOM_ID_KEY, roomId);

            sendRequestHelper(COMMAND_SKIP_TRACK, jsonRequestObject,
                    new SendRequestHelperCallback() {

                        @Override
                        public void executeOnSuccess(JSONObject jsonResponse) {
                            skipTrackCallback.onSkippedTrack();
                        }

                        @Override
                        public void executeOnFail(String errorMessage) {
                            skipTrackCallback.onFail(errorMessage);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            skipTrackCallback.onFail("failed to parse arguments to JSON");
        }
    }

    @Override
    public void syncWithServer(
            final SyncWithServerCallback syncWithServerCallback) {

        sendRequestHelper(COMMAND_SYNC_WITH_SERVER, null,
                new SendRequestHelperCallback() {

                    @Override
                    public void executeOnSuccess(JSONObject jsonResponse) {
                        try {
                            int secondsFromSync = jsonResponse
                                    .getInt(SECONDS_FROM_SYNC_KEY);

                            syncWithServerCallback
                                    .onSyncedWithServer(secondsFromSync);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            executeOnFail("failed to parse JSON response");
                        }
                    }

                    @Override
                    public void executeOnFail(String errorMessage) {
                        syncWithServerCallback.onFail(errorMessage);
                    }
                });
    }

    private void sendRequestHelper(final String command,
            final JSONObject jsonRequestParams,
            final SendRequestHelperCallback executeOnSuccessCallback) {

        sendRequest(command, jsonRequestParams, new HttpCallback() {

            @Override
            public void onHttpRequestReponse(JSONObject jsonResponse) {
                try {
                    String confirmationCode = jsonResponse
                            .getString(CONFIRMATION_CODE_KEY);

                    if (confirmationCode.equals(CONFIRMATION_CODE_OK)) {
                        executeOnSuccessCallback.executeOnSuccess(jsonResponse);

                    } else if (confirmationCode
                            .equals(CONFIRMATION_CODE_WARNING)) {
                        executeOnSuccessCallback.executeOnSuccess(jsonResponse);

                    } else {
                        executeOnSuccessCallback
                                .executeOnFail(jsonResponse.getString(ERROR_MESSAGE_KEY));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    executeOnSuccessCallback
                            .executeOnFail("failed to parse response from JSON");
                }
            }

            @Override
            public void onHttpRequestFail(String errorMessage) {
                executeOnSuccessCallback.executeOnFail(errorMessage);
            }
        });
    }

    public interface SendRequestHelperCallback {
        public void executeOnSuccess(JSONObject jsonResponse);

        public void executeOnFail(String errorMessage);
    }

    /**
     * Note : second parameter jsonObject can be null. Creates a new
     * ContactServerAysncTask for each request.
     * 
     * @param command
     *            command (= end of url) name of the request
     * @param jsonObject
     *            optional jsonObject with parameters for command
     * @param callback
     *            callback function called when request response is received
     */
    private void sendRequest(final String command, final JSONObject jsonObject,
            final HttpCallback callback) {

        String requestJson = "";
        if (jsonObject != null) {
            requestJson = jsonObject.toString();
        }

        ContactServerAysncTask asyncTask = new ContactServerAysncTask(callback);

        String url = SERVER_URL + command + ".php";
        asyncTask.execute(url, requestJson);
    }

    /**
     * Convert a time value from seconds to milliseconds
     */
    private int secToMillis(long seconds) {
        return (int) (seconds * 1000);
    }

}