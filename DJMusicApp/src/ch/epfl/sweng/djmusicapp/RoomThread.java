/**
 * 
 */
package ch.epfl.sweng.djmusicapp;

import android.os.Handler;
import android.util.Log;
import ch.epfl.sweng.djmusicapp.activities.RoomActivity;
import ch.epfl.sweng.djmusicapp.data.ServerContacterFactory;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.GetCurrentAndNextTracksCallback;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.SyncWithServerCallback;

/**
 * @author SebastienAndreina
 * 
 */
public class RoomThread extends Thread implements
        GetCurrentAndNextTracksCallback, SyncWithServerCallback {
    private static final int INTERVAL = 5000;
    private Handler mScheduler;
    private RoomActivity mActivity;
    private boolean sync = false;
    private boolean run = true;
    private Track nextSong;

    public RoomThread(RoomActivity ac) {
        this.mActivity = ac;
        mScheduler = new Handler();
    }

    @Override
    public void run() {
        if (run) {
            if (!sync) {
                ServerContacterFactory.getServerContacter()
                        .syncWithServer(this);
            } else if (mActivity.youtubePlayerIsInstentiate()) {
                fetchTrack();
            } else {
                mScheduler.postDelayed(this, INTERVAL);
            }
        }

    }

    private void fetchTrack() {
        Log.e("Thread", "fetching track");
        ServerContacterFactory.getServerContacter().getCurrentAndNextTracks(
                RoomActivity.getRoomID(), false, this);
    }

    @Override
    public void onFail(String errorMessage) {
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
            if (!mActivity.youtubePlayerIsInstentiate()) {
                Log.e("thread", "Youtube player is not playing");
                mScheduler.postDelayed(this, INTERVAL);
            } else {
                if (mActivity.getMusic() == null
                        || !(mActivity.getMusic().getUrl().equals(currentTrack
                                .getUrl()))) {
                    mActivity.setMusic(currentTrack);
                    mActivity.setTime(currentTrackPos);
                    mActivity.playMusic();
                    Log.e("roomThread", "different track, changing");
                    mScheduler.postDelayed(this, INTERVAL);
                } else if (mActivity.getMusic().getUrl() == currentTrack
                        .getUrl() && mActivity.getTimeRemaining() < INTERVAL) {
                    Log.e("roomThread", "same track, interval <10000");
                    nextSong = nextTrack;
                    mScheduler.postDelayed(new setNextSong(),
                            mActivity.getTimeRemaining());
                    mScheduler.postDelayed(this, INTERVAL * 2);

                } else {

                    Log.e("roomThread", "same track, nothing to do: playing "
                            + mActivity.getMusic().getTitle() + " and fetch "
                            + currentTrack.getTitle());
                    mScheduler.postDelayed(this, INTERVAL);
                }
            }
        } else {

            mScheduler.postDelayed(this, INTERVAL);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.SyncWithServerCallback
     * #onSyncedWithServer(int)
     */
    @Override
    public void onSyncedWithServer(int secondsFromSync) {
        sync = true;
        if (secondsFromSync <= 1) {
            secondsFromSync = 5-1;
        } else {
            secondsFromSync--;
        }
        mScheduler.postDelayed(this, secondsFromSync * 1000);

    }

    public void setActivity(RoomActivity ra) {
        this.mActivity = ra;
    }

    public void stopThis() {
        run = false;
    }

    private class setNextSong implements Runnable {
        @Override
        public void run() {
            if (nextSong != null) {
                mActivity.setTime(0);
                mActivity.setMusic(nextSong);
                mActivity.playMusic();
            }
        }
    }
}