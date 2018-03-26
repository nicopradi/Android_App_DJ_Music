package ch.epfl.sweng.djmusicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.helpers.TrackDataSource;

/**
 * 
 * @author kezan
 * 
 */
public class DisplayMusicActivity extends Activity {

    private TrackDataSource mTrackDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * SearchCallback test = new YoutubeSearchCallback(); List<Track>
         * someTracksForTesting = new ArrayList(); Track track1 = new
         * Track("title1","title1", "", "","",(long)12); Track track2 = new
         * Track("title2","title2", "", "","",(long)12); Track track3 = new
         * Track("title3","title3", "", "","",(long)12);
         * someTracksForTesting.add(track1); someTracksForTesting.add(track2);
         * someTracksForTesting.add(track3); test.onFinish(someTracksForTesting,
         * this);
         */

        mTrackDataSource = new TrackDataSource(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    private OnClickListener btnPlayClicked = new OnClickListener() {

        public void onClick(View v) {
            Track tag = (Track) v.getTag();
            mTrackDataSource.open();
            mTrackDataSource.addSong(tag);
            mTrackDataSource.close();

            Toast.makeText(getApplicationContext(),
                    "You added " + tag.getTitle() + " to your playlist",
                    Toast.LENGTH_SHORT).show();

        }

    };

    public OnClickListener getPlayListener() {
        return btnPlayClicked;
    }

    private OnClickListener btnMoreClicked = new OnClickListener() {

        public void onClick(View v) {
            Track tag = (Track) v.getTag();
            startProperties(tag);
        }

    };

    public OnClickListener getMoreListener() {
        return btnMoreClicked;
    }

    public void startProperties(Track track) {
        Intent propertiesActivityIntent = new Intent(this,
                ChangeTrackPropertiesActivity.class);
        startActivity(propertiesActivityIntent);
    }

    @Override
    public void onBackPressed() {
        Intent switchToSearchMusicActivityIntent = new Intent(this,
                SearchMusicActivity.class);

        startActivity(switchToSearchMusicActivityIntent);
    }
}
