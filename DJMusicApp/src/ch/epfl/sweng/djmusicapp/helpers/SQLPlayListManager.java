package ch.epfl.sweng.djmusicapp.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLPlayListManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UsersPlaylists";

    public static final String TABLE_PLAYLIST = "UserPlaylist";

    public static final String KEY_ID = "id";
    public static final String KEY_URL = "url";
    public static final String KEY_NAME = "name";
    public static final String KEY_ARTISTS = "artists";
    public static final String KEY_ALBUM = "album";
    public static final String KEY_GENRE = "genre";
    public static final String KEY_LENGTH = "length";

    

    public SQLPlayListManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + "(" 
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "url TEXT, "
                + "name TEXT, " + "artists TEXT, " + "album TEXT, "
                + "genre TEXT, " + "length INTEGER );";
        db.execSQL(CREATE_PLAYLIST_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
//        onCreate(db);

    }
}
