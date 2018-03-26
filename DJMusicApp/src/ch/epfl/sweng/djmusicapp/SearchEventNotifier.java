package ch.epfl.sweng.djmusicapp;

import java.util.List;

import ch.epfl.sweng.djmusicapp.activities.RoomActivity;
import ch.epfl.sweng.djmusicapp.activities.SearchMusicActivity;



public class SearchEventNotifier {
	private SearchCallback cb;
	private boolean somethingHappened;
	public SearchEventNotifier (SearchCallback call){
		cb = call;
		somethingHappened = false;
	}
	
	public void doWork(List<Track> result, SearchMusicActivity activity){
		if(somethingHappened){
			cb.onFinish(result,activity);
		}
	}
	
	public void doWork(List<Track> result, RoomActivity activity){
		if(somethingHappened){
			cb.onFinish(result,activity);
		}
	}
	
	public void setSomethingHappened(Boolean smtH){
		somethingHappened = smtH;
	}
}
