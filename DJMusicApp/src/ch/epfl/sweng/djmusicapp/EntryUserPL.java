package ch.epfl.sweng.djmusicapp;

/**
 * Represent a song in the userPlaylist
 * 
 * @author Nicolas
 * 
 */
public class EntryUserPL {

    private int id = -1;
    private String name = null;
    private String artists = null;
    private int length = -1;
    private String dateAdded = null;

    public EntryUserPL() {

    }

    /**
     * @param SongName
     * @param artistsNames
     * @param duration
     *            Have to be in seconds
     * @param date
     *            Have to be like : JJ/MM/YYYY
     */
    public EntryUserPL(String SongName, String artistsNames, int duration, String date) {
        if (SongName == null || artistsNames == null || date == null) {
            throw new IllegalArgumentException("A song can not have null name, artist or date");
        }
        if (duration <0 ) {
            throw new IllegalArgumentException("A song's duraton should not be null");
        }

        name = SongName;
        artists = artistsNames;
        length = duration;
        dateAdded = date;
    }

    public String getName() {
        return name;
    }

    public String getArtists() {
        return artists;
    }

    public int getLength() {
        return length;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int refreshId) {
        id = refreshId;
    }

    public void setLength(int duration) {
        length = duration;
    }

    public void setDateAdded(String nDate) {
        dateAdded = nDate;
    }

    public void setName(String refreshName) {
        name = refreshName;
    }

    public void setArtists(String refreshArtists) {
        artists = refreshArtists;
    }

}