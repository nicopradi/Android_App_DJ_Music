package ch.epfl.sweng.djmusicapp.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import ch.epfl.sweng.djmusicapp.NotificationSystem;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.activities.RoomActivity;

/**
 * @author raducotofrei
 * 
 *         An implementation of a simple notification system
 * 
 */
public class DefaultNotificationSystem implements NotificationSystem {

    public static final String RESET = "android.intent.djmusic.action.RESET";
    public static final String MUTE_ACTION = "android.intent.djmusic.action.MUTE_ACTION";
    public static final String UNMUTE_ACTION = "android.intent.djmusic.action.UNMUTE_ACTION";
    public static final String THUMB_UP_ACTION = "android.intent.djmusic.action.THUMB_UP_ACTION";
    public static final String THUMB_DOWN_ACTION = "android.intent.djmusic.action.THUMB_DOWN_ACTION";
    public static final String CANCEL_THUMB_UP_ACTION = "android.intent.djmusic.action.CANCEL_THUMB_UP_ACTION";
    public static final String CANCEL_THUMB_DOWN_ACTION = "android.intent.djmusic.action.CANCEL_THUMB_DOWN_ACTION";

    private Context context;

    private Room mRoom;
    private Track mTrack;
    private Class<?> targetClass;
    private boolean thumbUp = false;
    private boolean thumbDown = false;

    private String userId;
    private boolean isAdmin;

    private IntentFilter savIntentFilter = new IntentFilter();
    private IntentFilter voteIntentFilter = new IntentFilter();

    private int notificationID = 1;
    private boolean resetVote = false;
    protected static DefaultNotificationSystem notificationSystem = null;

    private DefaultNotificationSystem(Context context) {
        if (context != null) {
            this.context = context;
        } else {
            System.out.println("context was null :(");
            this.context = new RoomActivity().getBaseContext();
        }

        // complete Intent Filters
        savIntentFilter.addAction(MUTE_ACTION);
        savIntentFilter.addAction(UNMUTE_ACTION);
        voteIntentFilter.addAction(RESET);
        voteIntentFilter.addAction(CANCEL_THUMB_DOWN_ACTION);
        voteIntentFilter.addAction(CANCEL_THUMB_UP_ACTION);
        voteIntentFilter.addAction(THUMB_DOWN_ACTION);
        voteIntentFilter.addAction(THUMB_UP_ACTION);
    }

    public void subscribeReceiverToPauseButton(BroadcastReceiver receiver) {
        context.registerReceiver(receiver, savIntentFilter);
    }

    public void subscribeReceiverToVoteButton(BroadcastReceiver receiver) {
        context.registerReceiver(receiver, voteIntentFilter);
    }

    public DefaultNotificationSystem setContext(Context context) {
        this.context = context;
        return this;
    }

    public DefaultNotificationSystem setRoom(Room room) {
        if (room != null) {
            this.mRoom = room;
        }
        return this;
    }

    public DefaultNotificationSystem setTrack(Track track) {
        this.mTrack = track;
        return this;
    }

    public DefaultNotificationSystem setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        return this;
    }

    public DefaultNotificationSystem setThumbUp(boolean value) {
        this.thumbUp = value;
        return this;
    }

    public DefaultNotificationSystem setThumbDown(boolean value) {
        this.thumbDown = value;
        return this;
    }

    public DefaultNotificationSystem setUserId(String value) {
        this.userId = value;
        return this;
    }

    public DefaultNotificationSystem setIsAdmin(boolean value) {
        this.isAdmin = value;
        return this;
    }

    public String getUserId() {
        return this.userId;
    }
    
    public String getRoomId(){
    	if(mRoom != null){
    		return mRoom.getId();
    	} else {
    		throw new NullPointerException();
    	}
    }

    public DefaultNotificationSystem resetVotes() {
        this.resetVote = true;
        return this;
    }

    /*
     * See
     * http://developer.android.com/guide/topics/ui/notifiers/notifications.html
     */
    @Override
    public void show() {
		if (this.mRoom != null && this.targetClass != null && this.context != null) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.drawable.dj_music_icon)
                    .setContentTitle(mRoom.getName());
			if(mTrack != null)
				mBuilder.setContentText("current : " + mTrack.getTitle());
			else
				mBuilder.setContentText("no current play ");
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, targetClass);
            if (targetClass.equals(RoomActivity.class)) {
                resultIntent.putExtra("ID", mRoom.getId());
                resultIntent.putExtra("isAdmin", isAdmin);
            }

            // The stack builder object will contain an artificial back stack
            // for
            // the started Activity.
            // This ensures that navigating backward from the Activity leads out
            // of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(targetClass);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            // Switch Mute/UnMute
            /*
             * Intent switchAudioVolumeIntent = new Intent(); PendingIntent
             * pendingswitchAudioVolumeIntent;
             * 
             * if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0)
             * { // We add a mute button
             * switchAudioVolumeIntent.setAction(MUTE_ACTION);
             * pendingswitchAudioVolumeIntent = PendingIntent.getBroadcast(
             * context, 0, switchAudioVolumeIntent, 0);
             * mBuilder.addAction(R.drawable.volume_icon, "",
             * pendingswitchAudioVolumeIntent); } else { // We add a unmute
             * button switchAudioVolumeIntent.setAction(UNMUTE_ACTION);
             * pendingswitchAudioVolumeIntent = PendingIntent.getBroadcast(
             * context, 0, switchAudioVolumeIntent, 0);
             * mBuilder.addAction(R.drawable.mute_icon, "",
             * pendingswitchAudioVolumeIntent); }
             */

            // Vote
            Intent VoteUpIntent = new Intent();
            PendingIntent pendingVoteUpIntent;
            Intent VoteDownIntent = new Intent();
            PendingIntent pendingVoteDownIntent;

            if (resetVote) {
                resetVote = false;

                VoteUpIntent.setAction(RESET);
                pendingVoteUpIntent = PendingIntent.getBroadcast(context, 0,
                        VoteUpIntent, 0);
                mBuilder.addAction(R.drawable.thumbs_up_icon, "",
                        pendingVoteUpIntent);

                VoteDownIntent.setAction(RESET);
                pendingVoteDownIntent = PendingIntent.getBroadcast(context, 2,
                        VoteDownIntent, 0);
                mBuilder.addAction(R.drawable.thumbs_down_icon, "",
                        pendingVoteDownIntent);

            } else {

                if (thumbUp) {
                    VoteUpIntent.setAction(CANCEL_THUMB_UP_ACTION);
                    pendingVoteUpIntent = PendingIntent.getBroadcast(context,
                            0, VoteUpIntent, 0);
                    mBuilder.addAction(R.drawable.thumbs_up_selected_icon, "",
                            pendingVoteUpIntent);
                } else {
                    VoteUpIntent.setAction(THUMB_UP_ACTION);
                    pendingVoteUpIntent = PendingIntent.getBroadcast(context,
                            0, VoteUpIntent, 0);
                    mBuilder.addAction(R.drawable.thumbs_up_icon, "",
                            pendingVoteUpIntent);
                }

                if (thumbDown) {
                    VoteDownIntent.setAction(CANCEL_THUMB_DOWN_ACTION);
                    pendingVoteDownIntent = PendingIntent.getBroadcast(context,
                            2, VoteDownIntent, 0);
                    mBuilder.addAction(R.drawable.thumbs_down_selected_icon,
                            "", pendingVoteDownIntent);
                } else {
                    VoteDownIntent.setAction(THUMB_DOWN_ACTION);
                    pendingVoteDownIntent = PendingIntent.getBroadcast(context,
                            2, VoteDownIntent, 0);
                    mBuilder.addAction(R.drawable.thumbs_down_icon, "",
                            pendingVoteDownIntent);
                }
            }

            mNotificationManager.notify(notificationID, mBuilder.build());
        }
    }

    public static DefaultNotificationSystem getInstance(Context context) {
        if (notificationSystem == null) {
            notificationSystem = new DefaultNotificationSystem(context);
        }
        return notificationSystem;
    }

}
