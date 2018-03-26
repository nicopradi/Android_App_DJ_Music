package ch.epfl.sweng.djmusicapp.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ch.epfl.sweng.djmusicapp.data.ServerContacterFactory;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.VoteCurrentTrackCallback;
import ch.epfl.sweng.djmusicapp.helpers.DefaultNotificationSystem;

/**
 * @author raducotofrei
 * 
 * Receiver 
 *
 */
public class VoteListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		DefaultNotificationSystem notifications = DefaultNotificationSystem.getInstance(null);
		String userId = notifications.getUserId();
		String roomId = notifications.getRoomId();
		
		if (action != null) {
			if (action.equals(DefaultNotificationSystem.THUMB_UP_ACTION)) {
				ServerContacterFactory.getServerContacter().voteCurrentTrack(
						userId, roomId, 1, new VoteCurrentTrackCallback() {

							@Override
							public void onVotedCurrentTrackCallback() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFail(String errorMessage) {
								// TODO Auto-generated method stub

							}
						});
				
				notifications.setThumbUp(true);
				notifications.setThumbDown(false);
			} else if(action.equals(DefaultNotificationSystem.THUMB_DOWN_ACTION)) {
				ServerContacterFactory.getServerContacter().voteCurrentTrack(
						userId, roomId, -1, new VoteCurrentTrackCallback() {

							@Override
							public void onVotedCurrentTrackCallback() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFail(String errorMessage) {
								// TODO Auto-generated method stub

							}
						});
				
				notifications.setThumbUp(false);
				notifications.setThumbDown(true);
			} else if(action.equals(DefaultNotificationSystem.CANCEL_THUMB_UP_ACTION)) {
				ServerContacterFactory.getServerContacter().voteCurrentTrack(
						userId, roomId, -1, new VoteCurrentTrackCallback() {

							@Override
							public void onVotedCurrentTrackCallback() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFail(String errorMessage) {
								// TODO Auto-generated method stub

							}
						});
				
				notifications.setThumbUp(false);
				notifications.setThumbDown(false);
			} else if(action.equals(DefaultNotificationSystem.CANCEL_THUMB_DOWN_ACTION)) {
				ServerContacterFactory.getServerContacter().voteCurrentTrack(
						userId, roomId, 1, new VoteCurrentTrackCallback() {

							@Override
							public void onVotedCurrentTrackCallback() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFail(String errorMessage) {
								// TODO Auto-generated method stub

							}
						});
				
				notifications.setThumbUp(false);
				notifications.setThumbDown(false);
			} else if (action.equals(DefaultNotificationSystem.RESET)){
				notifications.setThumbUp(false);
				notifications.setThumbDown(false);
			}
			else {
				throw new IllegalArgumentException(action + " is not a valid action for voteListener");
			}
			
			notifications.show();
			
		} else {
			throw new IllegalArgumentException("action is null");
		}
	}

}
