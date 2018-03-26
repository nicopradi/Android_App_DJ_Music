package ch.epfl.sweng.djmusicapp;

import java.util.List;

import ch.epfl.sweng.djmusicapp.activities.RoomActivity;
import ch.epfl.sweng.djmusicapp.activities.SearchMusicActivity;


public interface SearchCallback {
	
	public void onFinish(List<Track> result, SearchMusicActivity activity);
	
	public void onFinish(List<Track> result, RoomActivity activity);
	
}
