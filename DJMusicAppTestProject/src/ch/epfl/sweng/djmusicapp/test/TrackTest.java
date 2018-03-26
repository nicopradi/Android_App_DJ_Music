package ch.epfl.sweng.djmusicapp.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.djmusicapp.Track;

public class TrackTest extends TestCase {

    private JSONObject trackJSON;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        trackJSON = new JSONObject();
        trackJSON.put(Track.TRACK_URL_KEY, "http://test.ourserver.com");
        trackJSON.put(Track.TRACK_TITLE_KEY, "Piano Concerto #4793 in A Major");
        trackJSON.put(Track.TRACK_ARTIST_KEY, "Beethoven");
        trackJSON.put(Track.TRACK_ALBUM_KEY, "BestAlbum");
        trackJSON.put(Track.TRACK_GENRE_KEY, "Classical");
        trackJSON.put(Track.TRACK_LENGTH_KEY, 4000);

    }

    public void testUrlAccess() {
        Track track = new Track("http://test.ourserver.com",
                "Piano Concerto #4793 in A Major", "Beethoven", "BestAlbum",
                "Classical", 4000);

        assertEquals("http://test.ourserver.com", track.getUrl());
    }

    public void testTitleAccess() {
        Track track = new Track("http://test.ourserver.com",
                "Piano Concerto #4793 in A Major", "Beethoven", "BestAlbum",
                "Classical", 4000);

        assertEquals("Piano Concerto #4793 in A Major", track.getTitle());
    }

    public void testArtistAccess() {
        Track track = new Track("http://test.ourserver.com",
                "Piano Concerto #4793 in A Major", "Beethoven", "BestAlbum",
                "Classical", 4000);

        assertEquals("Beethoven", track.getArtist());
    }

    public void testAlbumAccess() {
        Track track = new Track("http://test.ourserver.com",
                "Piano Concerto #4793 in A Major", "Beethoven", "BestAlbum",
                "Classical", 4000);

        assertEquals("BestAlbum", track.getAlbum());
    }

    public void testGenreAccess() {
        Track track = new Track("http://test.ourserver.com",
                "Piano Concerto #4793 in A Major", "Beethoven", "BestAlbum",
                "Classical", 4000);

        assertEquals("Classical", track.getGenre());
    }

    public void testLengthAccess() {
        Track track = new Track("http://test.ourserver.com",
                "Piano Concerto #4793 in A Major", "Beethoven", "BestAlbum",
                "Classical", 4000);

        assertEquals(4000, track.getLength());
    }

    public void testLengthNegative() {
        long length = -1000;

        try {
            new Track("http://test.ourserver.com",
                    "Piano Concerto #4793 in A Major", "Beethoven",
                    "BestAlbum", "Classical", length);
            fail("Track length is negative");
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testTrackToJson() {
        try {
            Track track = new Track("http://test.ourserver.com",
                    "Piano Concerto #4793 in A Major", "Beethoven",
                    "BestAlbum", "Classical", 4000);

            JSONObject trackJson = track.toJSON();

            assertEquals("http://test.ourserver.com",
                    trackJson.getString(Track.TRACK_URL_KEY));
            assertEquals("Piano Concerto #4793 in A Major",
                    trackJson.getString(Track.TRACK_TITLE_KEY));
            assertEquals("Beethoven",
                    trackJson.getString(Track.TRACK_ARTIST_KEY));
            assertEquals("BestAlbum",
                    trackJson.getString(Track.TRACK_ALBUM_KEY));
            assertEquals("Classical",
                    trackJson.getString(Track.TRACK_GENRE_KEY));
            assertEquals(4000, trackJson.getLong(Track.TRACK_LENGTH_KEY));

        } catch (JSONException e) {
            fail();
        }

    }

    public void testTrackFromJson() {

        try {
            Track track = Track.fromJSON(trackJSON);

            assertEquals("http://test.ourserver.com", track.getUrl());
            assertEquals("Piano Concerto #4793 in A Major", track.getTitle());
            assertEquals("Beethoven", track.getArtist());
            assertEquals("BestAlbum", track.getAlbum());
            assertEquals("Classical", track.getGenre());
            assertEquals(4000 * 1000, track.getLength());
        } catch (JSONException e) {
            fail();
        }
    }

    public void testCreateListOfTracksFromJsonArray() {
        try {
            List<Track> trackList = new ArrayList<Track>();
            trackList.add(new Track("GRxofEmo3HA", "Piano Concerto in E major",
                    "Dr Kaboom", "Albumz", "Classical", 1800));
            trackList.add(new Track("oZ1CE1qAjA8", "Jazzy airs",
                    "Clayton Wardon", "A foul's soul", "Jazz", 405));
            trackList.add(new Track("iNk9_bUKYx0", "Black dawn",
                    "Time Destructor", "My life", "Metal", 123));

            JSONArray tracksJsonArray = new JSONArray();
            for (Track track : trackList) {
                tracksJsonArray.put(track.toJSON());
            }
            
            List<Track> tracks = Track.createListOfTracksFromJSONArray(tracksJsonArray);
            
            assertEquals("GRxofEmo3HA", tracks.get(0).getUrl());
            assertEquals("Piano Concerto in E major", tracks.get(0).getTitle());
            assertEquals("Dr Kaboom", tracks.get(0).getArtist());
            assertEquals("Albumz", tracks.get(0).getAlbum());
            assertEquals("Classical", tracks.get(0).getGenre());
            assertEquals(1800 * 1000, tracks.get(0).getLength());
            
            assertEquals("oZ1CE1qAjA8", tracks.get(1).getUrl());
            assertEquals("Jazzy airs", tracks.get(1).getTitle());
            assertEquals("Clayton Wardon", tracks.get(1).getArtist());
            assertEquals("A foul's soul", tracks.get(1).getAlbum());
            assertEquals("Jazz", tracks.get(1).getGenre());
            assertEquals(405 * 1000, tracks.get(1).getLength());
            
            assertEquals("iNk9_bUKYx0", tracks.get(2).getUrl());
            assertEquals("Black dawn", tracks.get(2).getTitle());
            assertEquals("Time Destructor", tracks.get(2).getArtist());
            assertEquals("My life", tracks.get(2).getAlbum());
            assertEquals("Metal", tracks.get(2).getGenre());
            assertEquals(123 * 1000, tracks.get(2).getLength());
            
        } catch (JSONException e) {
            fail();
        }
    }
}
