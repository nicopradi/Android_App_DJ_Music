/**
 * @author csbenz
 */
package ch.epfl.sweng.djmusicapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a track with its URL and other metadata, that can then be played.
 * 
 * @author csbenz
 */
public class Track {

    public static final String TRACK_ID_KEY = "track_id";
    public static final String TRACK_URL_KEY = "track_url";
    public static final String TRACK_TITLE_KEY = "track_title";
    public static final String TRACK_ARTIST_KEY = "track_artist";
    public static final String TRACK_ALBUM_KEY = "track_album";
    public static final String TRACK_GENRE_KEY = "track_genre";
    public static final String TRACK_LENGTH_KEY = "track_length";

    private long mId = 0; // TODO not in constructor
    private String mUrl;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private String mGenre;
    private long mLength;

    // TODO add thumbnail

    /**
     * @param url
     *            url of the track
     * @param title
     *            title of the track
     * @param artist
     *            artist of the track
     * @param album
     *            album of the track
     * @param genre
     *            genre of the track
     * @param length
     *            length of the track in milliseconds
     * 
     */
    public Track(String url, String title, String artist, String album,
            String genre, long length) {

        if (url == null || title == null || artist == null || album == null
                || genre == null) {

            throw new NullPointerException();
        }

        if (length < 0) {
            throw new IllegalArgumentException();
        }

        this.mUrl = url;
        this.mTitle = title;
        this.mArtist = artist;
        this.mAlbum = album;
        this.mGenre = genre;
        this.mLength = length;
    }

    /**
     * @return the id
     */
    public long getId() {
        return mId;
    }
    
    public void setId(long id){
    	this.mId = id;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return mArtist;
    }

    /**
     * @return the album
     */
    public String getAlbum() {
        return mAlbum;
    }

    /**
     * @return the genre
     */
    public String getGenre() {
        return mGenre;
    }

    /**
     * @return the length
     */
    public long getLength() {
        return mLength;
    }

    public static List<Track> createListOfTracksFromJSONArray(
            JSONArray jsonArray) throws JSONException {

        List<Track> tracks = new ArrayList<Track>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject trackJSON = (JSONObject) jsonArray.get(i);
            Track track = fromJSON(trackJSON);
            tracks.add(track);
        }

        return tracks;
    }

    public static Track fromJSON(JSONObject trackJson) throws JSONException {
        String url = null;
        String title = null;
        String artist = null;
        String album = null;
        String genre = null;
        long length = 0;

        url = trackJson.getString(TRACK_URL_KEY);
        title = trackJson.getString(TRACK_TITLE_KEY);
        artist = trackJson.getString(TRACK_ARTIST_KEY);
        album = trackJson.getString(TRACK_ALBUM_KEY);
        genre = trackJson.getString(TRACK_GENRE_KEY);
        length = trackJson.getInt(TRACK_LENGTH_KEY)*1000;

        return new Track(url, title, artist, album, genre, length);
    }

    public JSONObject toJSON() throws JSONException {

        if (mUrl == null || mTitle == null || mArtist == null || mAlbum == null
                || mGenre == null) {
            throw new NullPointerException();
        }

        JSONObject trackJson = new JSONObject();

        trackJson.put(TRACK_ID_KEY, mId);
        trackJson.put(TRACK_URL_KEY, mUrl);
        trackJson.put(TRACK_TITLE_KEY, mTitle);
        trackJson.put(TRACK_ARTIST_KEY, mArtist);
        trackJson.put(TRACK_ALBUM_KEY, mAlbum);
        trackJson.put(TRACK_GENRE_KEY, mGenre);
        trackJson.put(TRACK_LENGTH_KEY, mLength);

        return trackJson;
    }
    
    public String toString() {
    	return getTitle();
    }

}
