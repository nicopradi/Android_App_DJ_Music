package ch.epfl.sweng.djmusicapp;

public class EntryRoomPL {

    private int userId;
    private Track track = null;

    public EntryRoomPL(int joinUser, Track song) {
        if (joinUser < 0 || song == null) {
            throw new IllegalArgumentException("A song should not be null and an user ID shoud not be negative");
        }
        userId = joinUser;
        track = song;
    }

    public EntryRoomPL(int joinUser) {
        if (joinUser < 0) {
            throw new IllegalArgumentException("A user ID should not be negative");
        }
        userId = joinUser;
    }

    public EntryRoomPL() {

    }

    public int getUserId() {
        return userId;
    }

    public Track getTrack() {
        return track;
    }

    public void setUserID(int refreshUserId) {
        userId = refreshUserId;
    }

    public void setTrack(Track refreshTrack) {
        track = refreshTrack;
    }

}