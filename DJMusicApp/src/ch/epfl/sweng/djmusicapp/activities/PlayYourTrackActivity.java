package ch.epfl.sweng.djmusicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.helpers.TrackDataSource;
import ch.epfl.sweng.djmusicapp.ui.CustomTextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;

/**
 * Play the track select by the user
 * 
 * @author Nicolas, SebastienAndreina
 * 
 */
public class PlayYourTrackActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private YouTubePlayer mYouTubePlayer;
    private YouTubePlayerFragment mYouTubePlayerFragment;
    private String mTrackName;
    private Track mCurrentTrack;
    private TrackDataSource mTrackDataSource;
    private CustomTextView mTracktitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_your_track);

        mTracktitle = (CustomTextView) findViewById(R.id.nameOfTrackMusicActivity);

        mTrackDataSource = new TrackDataSource(PlayYourTrackActivity.this);

        Intent startingIntent = getIntent();
        mTrackName = startingIntent.getStringExtra("track");

        mTracktitle.setText(mTrackName);

        mTrackDataSource.open();
        mCurrentTrack = mTrackDataSource.getTrack(mTrackName);
        mTrackDataSource.close();

        mYouTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager()
                .findFragmentById(R.id.youtube_fragmentPlayYourTrackActivity);

        mYouTubePlayerFragment.initialize(RoomActivity.API_KEY, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_your_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInitializationFailure(Provider arg0,
            YouTubeInitializationResult result) {
        Toast.makeText(
                this,
                "YouTubePlayer.onInitializationFailure(): " + result.toString(),
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onInitializationSuccess(Provider arg0, YouTubePlayer player,
            boolean arg2) {
        Log.e("", "initialize successfull");
        mYouTubePlayer = player;
        mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        mYouTubePlayer.loadVideo(mCurrentTrack.getUrl());

    }
}
