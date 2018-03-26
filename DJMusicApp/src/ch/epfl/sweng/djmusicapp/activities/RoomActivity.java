package ch.epfl.sweng.djmusicapp.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.RoomThread;
import ch.epfl.sweng.djmusicapp.SearchCallback;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.YoutubeSearch;
import ch.epfl.sweng.djmusicapp.YoutubeSearchCallback;
import ch.epfl.sweng.djmusicapp.adapters.TrackAdapter;
import ch.epfl.sweng.djmusicapp.data.ServerContacterFactory;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.AddTracksCallback;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.GetCurrentAndNextTracksCallback;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.GetRoomCallBack;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.SkipTrackCallback;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.VoteCurrentTrackCallback;
import ch.epfl.sweng.djmusicapp.helpers.DefaultNotificationSystem;
import ch.epfl.sweng.djmusicapp.helpers.TrackDataSource;
import ch.epfl.sweng.djmusicapp.listeners.SwitchAudioVolumeListener;
import ch.epfl.sweng.djmusicapp.listeners.VoteListener;
import ch.epfl.sweng.djmusicapp.ui.CustomTextView;
import ch.epfl.sweng.djmusicapp.utils.MyUtils;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

/**
 * Activity displaying the video, the chat, the vote/grab buttons and the giving
 * users the ability to join the queue
 * 
 * @author Tristan Marchal, SebastienAndreina, csbenz
 * 
 * 
 */
public class RoomActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener, GetRoomCallBack,
        AddTracksCallback, GetCurrentAndNextTracksCallback {

    private Activity mActivity = this;
    public final static String API_KEY = "AIzaSyBVaUWIoMw7Nd7g8swIJlvgod1Tm8l1c-4";
    private static String mRoomID = "-1";
    private static RoomThread mThread;
    private static boolean isInstentiate = false;
    private static String PAUSE_TEXT = "Pause";
    private static String PLAY_TEXT = "Play";

    private YouTubePlayer mYouTubePlayer;
    private YouTubePlayerFragment mYouTubePlayerFragment;

    private TrackDataSource mTrackDataSource;
    private Track mCurrentTrack = null;
    private String mCurrentlyPlaying = null;
    private String mUserID = "-1";
    private int mTime = 0;
    private Room mRoom;

    private RecyclerView mYoutubeRecyclerView;
    private RecyclerView mRecyclerView;
    private TrackAdapter mTrackAdapter;
    private List<Track> mTrackList = new ArrayList<Track>();
    private List<Track> allTracks = new ArrayList<Track>();
    private LinearLayout buttonsLayout;
    private EditText searchField;
    private Button backButton;
    private Button showMyMusicsButton;
    private Button searchOnYoutubeButton;
    private CustomTextView mNameTrack;
    private Button mPausePlayButton;
    private Button mUpVoteButton;
    private Button mDownVoteButton;
    private boolean alreadyVoted = false;

    private Button adminButton;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null && getIntent().getExtras().containsKey("ID")
                && getIntent().getExtras().containsKey("isAdmin")) {
            mRoomID = getIntent().getExtras().getString("ID");
            this.isAdmin = getIntent().getExtras().getBoolean("isAdmin");
        } else if (mRoomID == null) {
            throw new IllegalArgumentException("No ID in the extra");
        }

        mTrackDataSource = new TrackDataSource(RoomActivity.this);
        mTrackDataSource.open();
        allTracks = mTrackDataSource.getAllSongs();
        mTrackDataSource.close();

        setContentView(R.layout.activity_room);
        this.mUserID = MyUtils.getUserId(this);

        mNameTrack = (CustomTextView) findViewById(R.id.nameOfTrackRoomActivity);
        mNameTrack.setTypeFaceRobotoRegular(this);
        mPausePlayButton = (Button) findViewById(R.id.pausePlayButtonRoomActivity);
        mPausePlayButton.setText(PAUSE_TEXT);

        adminButton = (Button) findViewById(R.id.skipButtonRoomActivity);
        if (!isAdmin) {
            adminButton.setVisibility(View.GONE);
        }

        mUpVoteButton = (Button) findViewById(R.id.upvoteButtonRoomActivity);
        mUpVoteButton.setEnabled(true);
        mDownVoteButton = (Button) findViewById(R.id.downvoteButtonRoomActivity);
        mDownVoteButton.setEnabled(true);
        mTrackDataSource = new TrackDataSource(this);

        setContent();

        initialise();

        // notifications
        DefaultNotificationSystem notifications = DefaultNotificationSystem
                .getInstance(this).setRoom(mRoom).setTrack(mCurrentTrack)
                .setTargetClass(RoomActivity.class)
                .setUserId(MyUtils.getUserId(this)).setIsAdmin(isAdmin);

        notifications
                .subscribeReceiverToPauseButton(new SwitchAudioVolumeListener());
        notifications.subscribeReceiverToVoteButton(new VoteListener());

        notifications.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
        case R.id.action_refresh_room:
            resume();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialise() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewRoomActivity);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mYoutubeRecyclerView = (RecyclerView) findViewById(R.id.recyclerYoutubeViewRoomActivity);
        LinearLayoutManager l2m = new LinearLayoutManager(this);
        l2m.setOrientation(LinearLayoutManager.VERTICAL);
        mYoutubeRecyclerView.setLayoutManager(l2m);

        mTrackAdapter = new TrackAdapter(mTrackList);
        backButton = (Button) findViewById(R.id.backButtonRoomActivity);
        showMyMusicsButton = (Button) findViewById(R.id.seeMyMusicsRoomActivity);
        searchOnYoutubeButton = (Button) findViewById(R.id.searchOnYoutubeButtonRoomActivity);
        searchField = (EditText) findViewById(R.id.searchMusicFieldRoomActivity);
        buttonsLayout = (LinearLayout) findViewById(R.id.buttonsLayoutRoomActivity);
        mTrackAdapter.setOnClickItemListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                new Builder(RoomActivity.this)
                        .setTitle("Choose")
                        .setMessage(
                                "Are you sure you want to add this track to the room playlist?")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // User clicked OK in dialog to delete
                                        // track
                                        int itemPosition = mRecyclerView
                                                .getChildPosition(v);
                                        Track trackToAdd = mTrackList
                                                .get(itemPosition);
                                        List<Track> tracks = (new ArrayList<Track>());
                                        tracks.add(trackToAdd);

                                        ServerContacterFactory
                                                .getServerContacter()
                                                .addTracks(
                                                        RoomActivity.this.mUserID,
                                                        RoomActivity.mRoomID,
                                                        tracks,
                                                        RoomActivity.this);

                                        // mTrackList.remove(itemPosition);
                                        // mTrackAdapter.notifyDataSetChanged();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // Do nothing
                                    }
                                }).show();

            }
        });
        this.back(null);
        mRecyclerView.setAdapter(mTrackAdapter);
    }

    /**
     * 
     */
    private void setContent() {

        ServerContacterFactory.getServerContacter().getRoom(mRoomID, this);

    }

    @Override
    public void onPause() {
        super.onPause();

        release();
    }

    @Override
    public void onResume() {
        super.onResume();
        setContent();
        initialise();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    @Override
    public void onStop() {
        super.onStop();
        release();
    }

    private void release() {
        isInstentiate = false;

        if (mYouTubePlayer != null) {
            this.mYouTubePlayer.release();
            this.mYouTubePlayer = null;
        }

    }

    /* Upvote the song and notify the user */
    public void onClickUpvoteButtonFromRoomActivity(View view) {
        mUpVoteButton.setEnabled(false);
        mDownVoteButton.setEnabled(true);
        int vote = 1;
        if (alreadyVoted) {
            vote = 2;
        }
        ServerContacterFactory.getServerContacter().voteCurrentTrack(mUserID,
                mRoomID, vote, new VoteCurrentTrackCallback() {

                    @Override
                    public void onVotedCurrentTrackCallback() {
                        Toast.makeText(mActivity, "You upvoted that song !",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String errorMessage) {

                    }
                });
        alreadyVoted = true;

    }

    /* Downvote the song and notify the user */
    public void onClickDownvoteButtonFromRoomActivity(View view) {
        mUpVoteButton.setEnabled(true);
        mDownVoteButton.setEnabled(false);
        int vote = -1;
        if (alreadyVoted) {
            vote = -2;
        }
        ServerContacterFactory.getServerContacter().voteCurrentTrack(mUserID,
                mRoomID, vote, new VoteCurrentTrackCallback() {

                    @Override
                    public void onVotedCurrentTrackCallback() {
                        Toast.makeText(mActivity, "You downvoted that song !",
                                Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFail(String errorMessage) {

                    }
                });
        alreadyVoted = true;
    }

    /* Grab the song and notify the user */
    public void onClickGrabButtonFromRoomActivity(View view) {
        if (mCurrentTrack != null) {
            Toast.makeText(this, "You grabed that song !", Toast.LENGTH_SHORT)
                    .show();
            
            mTrackDataSource.open();
            mTrackDataSource.addSong(mCurrentTrack);
            if (!allTracks.contains(mCurrentTrack)) {
                allTracks.add(mCurrentTrack);
            }
            mTrackDataSource.close();
        } else {
            Toast.makeText(this, "no song to grab", Toast.LENGTH_SHORT).show();
        }
    }

    /* Switch to the MusicHistoryActivity activity */
    public void onClickHistoryButtonFromRoomActivity(View view) {
        Intent switchToMusicHistoryActivityIntent = new Intent(this,
                MusicHistoryActivity.class);

        startActivity(switchToMusicHistoryActivityIntent);
    }

    @Override
    public void onBackPressed() {
        Intent switchToChooseRoomActivityIntent = new Intent(this,
                ChooseRoomActivity.class);

        startActivity(switchToChooseRoomActivityIntent);
    }

    /**
     * Play the CurrentlyPlaying music in the player
     */
    public void playMusic() {
        mPausePlayButton.setText(PAUSE_TEXT);
        mUpVoteButton.setEnabled(true);
        mDownVoteButton.setEnabled(true);
        alreadyVoted = false;
        if (mYouTubePlayer == null) {
            initialise();
        } else {
            if (mCurrentlyPlaying != null) {
                refreshName();
                refreshNotification();
                mYouTubePlayer.loadVideo(mCurrentlyPlaying, mTime);

            } else {
                Toast.makeText(this, "No track to play", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void refreshName() {
        if (mNameTrack == null) {
            mNameTrack = (CustomTextView) findViewById(R.id.nameOfTrackMusicActivity);
        }
        mNameTrack.setText(mCurrentTrack.getTitle());
    }

    public void refreshNotification() {
        DefaultNotificationSystem.getInstance(this).resetVotes()
                .setTrack(mCurrentTrack).setRoom(mRoom).show();
    }

    public void onClickPausePlay(View view) {
        if (mYouTubePlayer != null) {
            String mCurrentText = mPausePlayButton.getText().toString();
            if (mCurrentText.equals(PAUSE_TEXT)) {
                mYouTubePlayer.pause();
                mPausePlayButton.setText(PLAY_TEXT);
            } else if (mCurrentText.equals(PLAY_TEXT)) {
                ServerContacterFactory.getServerContacter()
                        .getCurrentAndNextTracks(mRoomID, false, this);
                mPausePlayButton.setText(PAUSE_TEXT);
            }
        } else {
            Toast.makeText(this, "player not initialized", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void resume() {
        if (mYouTubePlayer != null) {
            mPausePlayButton.setText(PAUSE_TEXT);
            mUpVoteButton.setEnabled(true);
            mDownVoteButton.setEnabled(true);
            if (mThread != null) {
                mThread.stopThis();
            }
            mThread = new RoomThread(this);
            mThread.run();
            ServerContacterFactory.getServerContacter()
                    .getCurrentAndNextTracks(mRoomID, false, this);

        } else {
            setContent();
        }

    }

    // ///////////////////////////////////////////
    // //////////Getters/Setters//////////////////
    // ///////////////////////////////////////////

    public boolean youtubePlayerIsInstentiate() {
        return isInstentiate;
    }

    public YouTubePlayer getYoutubePlayer() {
        return this.mYouTubePlayer;
    }

    /**
     * @param timeInMillis
     */
    public void setTime(int timeInMillis) {
        if (timeInMillis < 0) {
            throw new IllegalArgumentException("Time cannot be negative");
        }
        this.mTime = timeInMillis;
    }

    public void setMusic(Track track) {
        if (track == null) {
            throw new IllegalArgumentException();
        }
        this.mCurrentTrack = track;
        this.mCurrentlyPlaying = track.getUrl();

    }

    public Track getMusic() {
        return this.mCurrentTrack;
    }

    public static String getRoomID() {
        return mRoomID;
    }

    public int getTimeRemaining() {
        return this.mYouTubePlayer.getDurationMillis()
                - this.mYouTubePlayer.getCurrentTimeMillis();
    }

    // ///////////////////////////////////////////
    // ////////////CALLBACKS//////////////////////
    // ///////////////////////////////////////////

    // first callBack After onCreate();
    @Override
    public void onGotRoom(Room room) {

        this.mRoom = room;
        if (room.getCurrentTrack() != null) {
            this.mCurrentlyPlaying = mRoom.getCurrentTrack().getUrl();
            mCurrentTrack = mRoom.getCurrentTrack();
        }
        this.setTitle(mRoom.getName());

        this.mTime = mRoom.getCurrentTrackPos();
        mYouTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager()
                .findFragmentById(R.id.youtube_fragment);
        mCurrentTrack = mRoom.getCurrentTrack();
        mYouTubePlayerFragment.initialize(API_KEY, this);

    }

    // second callBack after onCreate(); called in onGotRoom
    @Override
    public void onInitializationSuccess(
            com.google.android.youtube.player.YouTubePlayer.Provider provider,
            YouTubePlayer player, boolean arg2) {
        isInstentiate = true;
        mYouTubePlayer = player;
        mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

        if (mThread != null) {
            mThread.stopThis();
        }

        Log.e("Thread", "Creating new Thread");
        mThread = new RoomThread(this);
        mThread.run();

        playMusic();

    }

    // ServerContacter callback failure
    @Override
    public void onFail(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // YouTube callBack failure
    @Override
    public void onInitializationFailure(
            com.google.android.youtube.player.YouTubePlayer.Provider provider,
            YouTubeInitializationResult result) {

        Toast.makeText(
                this,
                "YouTubePlayer.onInitializationFailure(): " + result.toString(),
                Toast.LENGTH_LONG).show();
    }

    // ///////////////////////////////////////////
    // ////////////QUEUE//////////////////////////
    // ///////////////////////////////////////////

    public void joinQueue(View view) {
        buttonsLayout.setVisibility(View.GONE);
        searchField.setVisibility(View.VISIBLE);
        searchOnYoutubeButton.setVisibility(View.VISIBLE);
        showMyMusicsButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
    }

    public void back(View view) {
        mRecyclerView.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        mYoutubeRecyclerView.setVisibility(View.GONE);
        searchField.setVisibility(View.GONE);
        searchOnYoutubeButton.setVisibility(View.GONE);
        showMyMusicsButton.setVisibility(View.GONE);
        buttonsLayout.setVisibility(View.VISIBLE);
    }

    public void showMyMusics(View v) {
        String mSearchInput = searchField.getText().toString();
        mYoutubeRecyclerView.setVisibility(View.GONE);
        searchField.setVisibility(View.GONE);
        searchOnYoutubeButton.setVisibility(View.GONE);
        showMyMusicsButton.setVisibility(View.GONE);

        mTrackList.clear();
        if (mSearchInput == null || mSearchInput == "") {
            mTrackList.addAll(allTracks);
        }

        for (Track track : allTracks) {

            if (track.getTitle().toLowerCase()
                    .contains(mSearchInput.toLowerCase())) {
                mTrackList.add(track);
            }
        }
        mTrackAdapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void searchOnYoutube(View view) {
        mRecyclerView.setVisibility(View.GONE);
        searchOnYoutubeButton.setVisibility(View.GONE);
        searchField.setVisibility(View.GONE);
        showMyMusicsButton.setVisibility(View.GONE);
        mYoutubeRecyclerView.setVisibility(View.VISIBLE);

        String query = searchField.getText().toString();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();

        StrictMode.setThreadPolicy(policy);
        Log.e("before cb", "1");
        SearchCallback cb = new YoutubeSearchCallback();
        Log.e("before yts", "2");

        YoutubeSearch yts = new YoutubeSearch();

        Log.e("before search", "3");
        yts.search(query, cb, this);

    }

    public RecyclerView getRecyclerView() {
        return this.mYoutubeRecyclerView;
    }

    public String getUserId() {
        return this.mUserID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.AddTracksCallback
     * #onAddedTracks()
     */
    @Override
    public void onAddedTracks() {
        mTrackAdapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        mYoutubeRecyclerView.setVisibility(View.GONE);
        searchField.setVisibility(View.GONE);
        searchOnYoutubeButton.setVisibility(View.GONE);
        showMyMusicsButton.setVisibility(View.GONE);
        buttonsLayout.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Track added to playlist", Toast.LENGTH_SHORT)
                .show();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.
     * GetCurrentAndNextTracksCallback
     * #onGotCurrentAndNextTracks(ch.epfl.sweng.djmusicapp.Track, int,
     * ch.epfl.sweng.djmusicapp.Track)
     */
    @Override
    public void onGotCurrentAndNextTracks(Track currentTrack,
            int currentTrackPos, Track nextTrack) {
        if (currentTrack != null) {
            this.mCurrentlyPlaying = currentTrack.getUrl();
            this.mCurrentTrack = currentTrack;
            this.mTime = (int) currentTrackPos;
            refreshName();
            mYouTubePlayer.loadVideo(mCurrentlyPlaying, mTime);
            mYouTubePlayer.play();
        }

    }

    public void onClickSkipButton(View view) {
        if (mCurrentTrack != null) {

            ServerContacterFactory.getServerContacter().skipTrack(
                    MyUtils.getUserId(this), getRoomID(),
                    new SkipTrackCallback() {

                        @Override
                        public void onSkippedTrack() {

                        }

                        @Override
                        public void onFail(String errorMessage) {
                            Toast.makeText(RoomActivity.this, errorMessage,
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

}