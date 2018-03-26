package ch.epfl.sweng.djmusicapp.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import ch.epfl.sweng.djmusicapp.helpers.DefaultNotificationSystem;

/**
 * @author raducotofrei
 * 
 * Receiver which can mute/unmute music volume depending on user's actions
 *
 */
public class SwitchAudioVolumeListener extends BroadcastReceiver {
	private AudioManager audioManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		String action = intent.getAction();
		if (action != null) {
			if (action.equals(DefaultNotificationSystem.MUTE_ACTION)) {
				muteAudio();
			} else if(action.equals(DefaultNotificationSystem.UNMUTE_ACTION)){
				unmuteAudio();
			} else {
				throw new IllegalArgumentException(action + " is not a valid action for switchAudioListener");
			}
			
			DefaultNotificationSystem.getInstance(null).show();
		} else {
			throw new IllegalArgumentException("action is null");
		}
	}

	private void muteAudio() {
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
	}

	private void unmuteAudio() {
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

}
