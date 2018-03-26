package ch.epfl.sweng.djmusicapp;

import android.content.BroadcastReceiver;

/**
 * @author raducotofrei
 * 
 * A notification system
 * 
 */
public interface NotificationSystem {
	
	/**
	 * Subscribe the receiver who will deal with the mute/unmute button
	 * 
	 * @param receiver the personalized receiver
	 */
	public void subscribeReceiverToPauseButton(BroadcastReceiver receiver);
	
	/**
	 * Subscribe the receiver who will deal with the vote buttons
	 * 
	 * @param receiver the personalized receiver
	 */
	public void subscribeReceiverToVoteButton(BroadcastReceiver receiver);
	
	public NotificationSystem setRoom(Room room);
	
	public NotificationSystem setTrack(Track track);
	
	public NotificationSystem setTargetClass(Class <?> targetClass);
	
	public NotificationSystem setThumbUp(boolean value);
	
	public NotificationSystem setThumbDown(boolean value);
	
	
	/**
	 * Show a message as a notification
	 *
	 */
	void show();
	
	
}
