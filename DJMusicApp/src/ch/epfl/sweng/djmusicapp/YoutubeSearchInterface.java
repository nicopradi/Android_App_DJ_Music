package ch.epfl.sweng.djmusicapp;

import ch.epfl.sweng.djmusicapp.activities.RoomActivity;
import ch.epfl.sweng.djmusicapp.activities.SearchMusicActivity;



public interface YoutubeSearchInterface {
	
	public void search(String query,SearchCallback cb, RoomActivity activity);
	
	public void search(String query,SearchCallback cb, SearchMusicActivity activity);
	
	
}
