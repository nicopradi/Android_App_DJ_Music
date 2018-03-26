package ch.epfl.sweng.djmusicapp.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.adapters.TrackAdapter;
import ch.epfl.sweng.djmusicapp.helpers.TrackDataSource;

/**
 * Activity where users can have a look at their musics or search for new ones
 * via Youtube
 * 
 * @author Tristan Marchal, nicopradi, csbenz
 * 
 */
public class MusicActivity extends Activity {

    private TrackDataSource mTrackDataSource;
    private List<Track> mTrackList;
    private RecyclerView mRecyclerView;
    private TrackAdapter mTrackAdapter;

    /*
     * On create, retrieves the music previously saved and display them in the
     * LinearLayout of the ScrollView
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mTrackDataSource = new TrackDataSource(this);

        mTrackDataSource.open();
        mTrackList = mTrackDataSource.getAllSongs();
        mTrackDataSource.close();

        mRecyclerView = (RecyclerView) findViewById(R.id.listView1);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mTrackAdapter = new TrackAdapter(mTrackList);
        mTrackAdapter.setOnClickItemListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int itemPosition = mRecyclerView.getChildPosition(v);
                Track track = mTrackList.get(itemPosition);

                mTrackAdapter.notifyDataSetChanged();

                Intent playMusicIntent = new Intent(MusicActivity.this, PlayYourTrackActivity.class);
                playMusicIntent.putExtra("track", track.getTitle());
                startActivity(playMusicIntent);

            }
        });
        mTrackAdapter.setOnLongClickItemListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(final View v) {
                new Builder(MusicActivity.this).setTitle("Delete?")
                        .setMessage("Are you sure you want to delete this track?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked OK in dialog to delete
                                // track
                                int itemPosition = mRecyclerView.getChildPosition(v);
                                Track trackToDelete = mTrackList.get(itemPosition);

                                deleteTrack(trackToDelete);

                                mTrackList.remove(itemPosition);
                                mTrackAdapter.notifyDataSetChanged();

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        }).show();

                return true;
            }
        });
        mRecyclerView.setAdapter(mTrackAdapter);
    }

    private void deleteTrack(Track track) {
        mTrackDataSource.open();
        mTrackDataSource.deleteSong(track);
        mTrackDataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.action_search_musics:
            Intent switchToSearchMusicActivityIntent = new Intent(this, SearchMusicActivity.class);

            startActivity(switchToSearchMusicActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Override the behavior of the back button from the MusicActivity to return
     * to the MainActivity activity
     */
    @Override
    public void onBackPressed() {
        Intent switchToMainActivityIntent = new Intent(this, MainActivity.class);

        startActivity(switchToMainActivityIntent);
    }
}
