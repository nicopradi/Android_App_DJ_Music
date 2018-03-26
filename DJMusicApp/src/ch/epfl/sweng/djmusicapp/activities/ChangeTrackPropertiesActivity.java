package ch.epfl.sweng.djmusicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.helpers.TrackDataSource;

public class ChangeTrackPropertiesActivity extends Activity {

    private TrackDataSource mTrackDataSource;
    private Spinner mSpinnerGenre;
    private String url;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private long length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_track_properties);

        mSpinnerGenre = (Spinner) findViewById(R.id.spinnerGenreChangeTrackPropertiesActivity);
        Intent changeTrackIntent = getIntent();
        Bundle properties = changeTrackIntent.getExtras();
        url = properties.getString("URL");
        title = properties.getString("TITLE");
        artist = properties.getString("ARTIST");
        album = properties.getString("ALBUM");
        genre = properties.getString("GENRE");
        length = properties.getLong("LENGTH");
        mTrackDataSource = new TrackDataSource(this);
        EditText titleText = (EditText) findViewById(R.id.edit_title1);
        titleText.setHint(title);
        EditText artistText = (EditText) findViewById(R.id.edit_artist1);
        artistText.setHint(artist);
        EditText albumText = (EditText) findViewById(R.id.edit_album1);
        albumText.setHint(album);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.change_track_properties, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickAddModifiedTrack(View v) {
        EditText titleText = (EditText) findViewById(R.id.edit_title1);
        String newTitle = titleText.getText().toString();
        EditText artistText = (EditText) findViewById(R.id.edit_artist1);
        String newArtist = artistText.getText().toString();
        EditText albumText = (EditText) findViewById(R.id.edit_album1);
        String newAlbum = albumText.getText().toString();

        if (!newTitle.equals("")) {
            title = newTitle;
        }
        if (!newArtist.equals("")) {
            artist = newArtist;
        }
        if (!newAlbum.equals("")) {
            album = newAlbum;
        }

        genre = mSpinnerGenre.getSelectedItem().toString();

        Track modifiedTrack = new Track(url, title, artist, album, genre,
                length);

        // add to the playlist ?
        mTrackDataSource.open();
        mTrackDataSource.addSong(modifiedTrack);
        mTrackDataSource.close();

        Toast.makeText(getApplicationContext(),
                "You added " + modifiedTrack.getTitle() + " to your playlist",
                Toast.LENGTH_SHORT).show();

        Intent switchToMusicActivityIntent = new Intent(this,
                MusicActivity.class);
        startActivity(switchToMusicActivityIntent);

    }

    @Override
    public void onBackPressed() {
        Intent switchToMusicActivityIntent = new Intent(this,
                MusicActivity.class);
        startActivity(switchToMusicActivityIntent);
    }

}
