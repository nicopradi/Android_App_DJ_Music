package ch.epfl.sweng.djmusicapp.helpers;


import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.djmusicapp.Track;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TrackDataSource {

		private SQLiteDatabase database;
		private SQLPlayListManager dbHelper;
		private String[] allColumns = { SQLPlayListManager.KEY_ID, SQLPlayListManager.KEY_URL,
				SQLPlayListManager.KEY_NAME, SQLPlayListManager.KEY_ARTISTS, SQLPlayListManager.KEY_ALBUM,
				SQLPlayListManager.KEY_GENRE, SQLPlayListManager.KEY_LENGTH };
		
		public TrackDataSource (Context context) {
			dbHelper = new SQLPlayListManager(context);
		}
		
		public void open() throws SQLException {
			database = dbHelper.getWritableDatabase();
		}
		
		public void close() {
			dbHelper.close();
		}
		
		public Track addSong(Track track) {
			
			if (!IsSongAlreadyStore(track)) {
			
				ContentValues values = new ContentValues();
				values.put(SQLPlayListManager.KEY_URL, track.getUrl());
				values.put(SQLPlayListManager.KEY_NAME, track.getTitle());
				values.put(SQLPlayListManager.KEY_ARTISTS, track.getArtist());
				values.put(SQLPlayListManager.KEY_ALBUM, track.getAlbum());
				values.put(SQLPlayListManager.KEY_GENRE, track.getGenre());
				values.put(SQLPlayListManager.KEY_LENGTH, track.getLength());
				
				
				long insertId = database.insert(SQLPlayListManager.TABLE_PLAYLIST, null, values);
				Cursor cursor = database.query(SQLPlayListManager.TABLE_PLAYLIST, allColumns,
						SQLPlayListManager.KEY_ID + " = " + insertId, null, null, null, null);
				cursor.moveToFirst();
				Track nTrack = cursorToComment(cursor);
				cursor.close();
				return nTrack;
			} else {
				Log.d("Song already in the playlist", track.getTitle());
				return null;
			}
            	
		}
		
		public Track getTrack(String trackName) {
			
			String[] selectionArgs = new String[1];
			selectionArgs[0] = trackName;
			Cursor cursor = database.query(SQLPlayListManager.TABLE_PLAYLIST, allColumns,
						SQLPlayListManager.KEY_NAME + " = ?", selectionArgs, null, null, null);
				cursor.moveToFirst();
				Track nTrack = cursorToComment(cursor);
				cursor.close();
				return nTrack;
		}
		
		public void deleteSong(Track track) {
			long id = track.getId();
			database.delete(SQLPlayListManager.TABLE_PLAYLIST, SQLPlayListManager.KEY_ID + " = " +id, null);
		}
		
		public List<Track> getAllSongs() {
			List<Track> tracks = new ArrayList<Track>();
			
			Cursor cursor = database.query(SQLPlayListManager.TABLE_PLAYLIST,
					allColumns, null, null, null, null, null);
			
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				Track track = cursorToComment(cursor);
				tracks.add(track);
				cursor.moveToNext();
			}
			
			cursor.close();
			return tracks;		
		}
		
		private boolean IsSongAlreadyStore(Track track) {
	        	       
	        Cursor cursor = database.query(SQLPlayListManager.TABLE_PLAYLIST, allColumns, " name =?",
	                new String[] { String.valueOf(track.getTitle()) }, null, null, null, null);

	        return cursor.moveToFirst();
	        
	    }
		
		private Track cursorToComment(Cursor cursor) {
			
			Track track = new Track(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4),
                    cursor.getString(5), Integer.parseInt(cursor.getString(6)));
			
			track.setId(cursor.getLong(0));
			return track;
			
		}
}	