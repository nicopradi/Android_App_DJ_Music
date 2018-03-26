package ch.epfl.sweng.djmusicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.SearchCallback;
import ch.epfl.sweng.djmusicapp.YoutubeSearch;
import ch.epfl.sweng.djmusicapp.YoutubeSearchCallback;
import ch.epfl.sweng.djmusicapp.helpers.TrackDataSource;

/**
 * 
 * @author Tristan Marchal, Keyan Moine
 * 
 * 
 */
/*
 * Activity where a user can search for a music on Youtube and add it to its
 * playlist
 */

public class SearchMusicActivity extends Activity {

    private SearchMusicActivity mActivity = this;
    private RecyclerView mRecyclerView;
    private TrackDataSource mTrackDataSource;
    private EditText mTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);

        mTrackDataSource = new TrackDataSource(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView3);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mTextField = (EditText) findViewById(R.id.searchMusicFieldSearchMusicActivity);
    }

    public void onClickSearchButton(View view) {
        String query = mTextField.getText().toString();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();

        StrictMode.setThreadPolicy(policy);
        SearchCallback cb = new YoutubeSearchCallback();
        YoutubeSearch yts = new YoutubeSearch();
        yts.search(query, cb, mActivity);
    }

    /*
     * Override the behavior of the back button from the SearchMusicActivity to
     * return to the MusicActivity activity
     */
    @Override
    public void onBackPressed() {
        Intent switchToMusicActivityIntent = new Intent(this,
                MusicActivity.class);

        startActivity(switchToMusicActivityIntent);
    }

    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    public TrackDataSource getTrackDataSource() {
        return mTrackDataSource;
    }

}