package ch.epfl.sweng.djmusicapp.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ch.epfl.sweng.djmusicapp.EntryRoomPL;
import ch.epfl.sweng.djmusicapp.Track;

/**
 * @author Nicolas
 *
 */
public class SQLRoomPlaylistManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RoomPlaylists";

    private static final String TABLE_PLAYLIST = "RoomPlaylist";

    private static final String KEY_ROWID = "rowId";
    private static final String KEY_USERID = "userId";
    private static final String KEY_URL = "url";
    private static final String KEY_NAME = "name";
    private static final String KEY_ARTISTS = "artists";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_LENGTH = "length";

    private static final String[] COLUMNS = { KEY_ROWID, KEY_USERID, KEY_URL,
            KEY_NAME, KEY_ARTISTS, KEY_ALBUM, KEY_GENRE, KEY_LENGTH };

    private static int row_Id = 0;

    public SQLRoomPlaylistManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE playlist ( "
                + "rowId INTEGER, " + "userId INTEGER, " + "url TEXT, "
                + "name TEXT, " + "artists TEXT, " + "album TEXT, "
                + "genre TEXT, " + "length INTEGER )";

        db.execSQL(CREATE_PLAYLIST_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXIST RoomPlaylist");
        this.onCreate(db);

    }

    /**
     * When a user join the QueueRoom with no track selected
     * 
     * @param userID
     */
    public void joinQueue(int userID) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the song is not already add to the userplaylist
        if (getTrackWithUser(userID) != null) {
            throw new IllegalArgumentException(
                    "User already add to the RoomPlaylist");
        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_ROWID, row_Id);
            values.put(KEY_USERID, userID);
            values.put(KEY_URL, "");
            values.put(KEY_NAME, "");
            values.put(KEY_ARTISTS, "");
            values.put(KEY_ALBUM, "");
            values.put(KEY_GENRE, "");
            values.put(KEY_LENGTH, 0);

            row_Id++;

            db.insert(TABLE_PLAYLIST, null, values);
        }

        db.close();
    }

    /**
     * When a user join the RoomQueue and already select a track
     * 
     * @param userId
     * @param entry
     */
    public void joinQueue(int userID, Track entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the song is not already add to the userplaylist
        if (getTrackWithUser(userID) != null) {
            throw new IllegalArgumentException(
                    "User already add to the RoomPlaylist");
        }

        else {
            ContentValues values = new ContentValues();

            values.put(KEY_ROWID, row_Id);
            values.put(KEY_USERID, userID);
            values.put(KEY_URL, entry.getUrl());
            values.put(KEY_NAME, entry.getTitle());
            values.put(KEY_ARTISTS, entry.getArtist());
            values.put(KEY_ALBUM, entry.getAlbum());
            values.put(KEY_GENRE, entry.getGenre());
            values.put(KEY_LENGTH, entry.getLength());

            row_Id++;

            db.insert(TABLE_PLAYLIST, null, values);
        }

        db.close();
    }

    /**
     * DO NOT delete any track from the playlist, need to call an other method
     * to do so
     * 
     * @param userID
     * @return
     */
    public EntryRoomPL getTrackWithUser(int userID) {

        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor;

        if(isTableExists(db, TABLE_PLAYLIST)) {
        	cursor = db.query(TABLE_PLAYLIST, COLUMNS, " userId =?",
                        new String[] { String.valueOf(userID) }, null, null,
                        null, null);
        }
        else {
        	return null;
        }

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        EntryRoomPL roomEntry = new EntryRoomPL();
        Track entry = new Track(cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getString(6),
                Integer.parseInt(cursor.getString(7)));

        roomEntry.setUserID(Integer.parseInt(cursor.getString(1)));
        roomEntry.setTrack(entry);

        return roomEntry;
    }

    /**
     * DO NOT delete any track from the playlist, need to call an other method
     * to do so
     * 
     * @param rowId
     * @return
     */
    public EntryRoomPL getTrackWithRow(int rowId) {

        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor;
        
        if(isTableExists(db, TABLE_PLAYLIST)) {
        	 cursor = db.query(TABLE_PLAYLIST, COLUMNS, " rowId =?",
                new String[] { String.valueOf(rowId) }, null, null, null, null);
        	 
        } 
        else {
        	return null;
        }

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        EntryRoomPL roomEntry = new EntryRoomPL();
        Track entry = new Track(cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getString(6),
                Integer.parseInt(cursor.getString(7)));

        roomEntry.setUserID(Integer.parseInt(cursor.getString(1)));
        roomEntry.setTrack(entry);

        return roomEntry;
    }

    /**
     * Change the user track in the roomPlaylist
     * 
     * @param userId
     * @param entry
     * @return the number of row affected
     */
    public int updateTrack(int userId, Track entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_URL, entry.getUrl());
        values.put(KEY_NAME, entry.getTitle());
        values.put(KEY_ARTISTS, entry.getArtist());
        values.put(KEY_ALBUM, entry.getAlbum());
        values.put(KEY_GENRE, entry.getGenre());
        values.put(KEY_LENGTH, entry.getLength());

        int i = db.update(TABLE_PLAYLIST, values, KEY_USERID + " = ?",
                new String[] { String.valueOf(userId) });

        db.close();

        return i;

    }

    /**
     * Delete track with userID
     * 
     * @param userId
     */
    public void deleteTrack(int userId) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PLAYLIST, KEY_USERID + " = ?",
                new String[] { String.valueOf(userId) });

        db.close();
        updateRowId();

    }

    public void deleteFirstTrack() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PLAYLIST, KEY_ROWID + " = ?",
                new String[] { String.valueOf(0) });

        db.close();
        updateRowId();

    }

    /**
     * Decrease by one all RowId entries
     */
    public void updateRowId() {

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 1; i < row_Id; i++) {

            ContentValues values = new ContentValues();
            values.put("rowId", i--);
            db.update(TABLE_PLAYLIST, values, KEY_ROWID
                    + " = ?", new String[] { String.valueOf(i) });
        }
        row_Id--;

        db.close();
    }
    
    public boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        
        return count > 0;
    }

}
