package ch.epfl.sweng.djmusicapp;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Toast;
import ch.epfl.sweng.djmusicapp.activities.ChangeTrackPropertiesActivity;
import ch.epfl.sweng.djmusicapp.activities.RoomActivity;
import ch.epfl.sweng.djmusicapp.activities.SearchMusicActivity;
import ch.epfl.sweng.djmusicapp.adapters.TrackAdapter;
import ch.epfl.sweng.djmusicapp.data.ServerContacterFactory;

//test
public class YoutubeSearchCallback implements SearchCallback{

	private SearchEventNotifier en;
	public YoutubeSearchCallback(){
		en = new SearchEventNotifier(this);
	}
	
	public void onFinish(List<Track> result, SearchMusicActivity activity){
		
		final TrackAdapter mTrackAdapter = new TrackAdapter(result);
		final SearchMusicActivity mActivity = activity;
		final List<Track> mResult = result;
		
		mTrackAdapter.setOnClickItemListener(new OnClickListener(){
			
			 @Override
	         public void onClick(View v) {
	            int itemPosition = mActivity.getRecyclerView().getChildPosition(v);
	            Track track = mResult.get(itemPosition);
	            
	            //add track to playlist ??
	            mActivity.getTrackDataSource().open();
	            mActivity.getTrackDataSource().addSong(track);
	            mActivity.getTrackDataSource().close();
	            
	            Toast.makeText(mActivity.getApplicationContext(), "You added " + track.getTitle() + " to your playlist",
	                    Toast.LENGTH_SHORT).show();
	            
	            mTrackAdapter.notifyDataSetChanged();
	            
	            

			 }
			
		});
		
		mTrackAdapter.setOnLongClickItemListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(final View v) {
                new Builder(mActivity)
                        .setTitle("Modify?")
                        .setMessage(
                                "Are you sure you want to modify the properties of this track?")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // User clicked OK in dialog to delete
                                        // track
                                        int itemPosition = mActivity.getRecyclerView()
                                                .getChildPosition(v);
                                        Track trackToModify = mResult
                                                .get(itemPosition);

                                        Intent changeTrackIntent = new Intent(mActivity,
                                        		ChangeTrackPropertiesActivity.class);
                                        Bundle propertiesOfTrack = new Bundle();
                                        
                                        propertiesOfTrack.putString("URL",trackToModify.getUrl());
                                        propertiesOfTrack.putString("TITLE",trackToModify.getTitle());
                                        propertiesOfTrack.putString("ARTIST",trackToModify.getArtist());
                                        propertiesOfTrack.putString("ALBUM",trackToModify.getAlbum());
                                        propertiesOfTrack.putString("GENRE",trackToModify.getGenre());
                                        propertiesOfTrack.putLong("LENGTH",trackToModify.getLength());
                                        
                                        changeTrackIntent.putExtras(propertiesOfTrack);
                                        
                                        mActivity.startActivity(changeTrackIntent);
                                        
                                        mTrackAdapter.notifyDataSetChanged();

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

                return true;
            }
        });
        mActivity.getRecyclerView().setAdapter(mTrackAdapter);
		
        
     
		
	}
	
	
	public void onFinish(List<Track> result, RoomActivity activity){
		
		
		final TrackAdapter mTrackAdapter = new TrackAdapter(result);
		final RoomActivity mActivity = activity;
		final List<Track> mResult = result;
		
		mTrackAdapter.setOnClickItemListener(new OnClickListener(){
			
			 @Override
	         public void onClick(View v) {
				 final View mV = v;
				 new Builder(mActivity).setTitle("Choose")
                 .setMessage("Are you sure you want to add this track to the room playlist?")
                 .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         // User clicked OK in dialog to delete
                         // track
                         int itemPosition = mActivity.getRecyclerView().getChildPosition(mV);
                         Track trackToAdd = mResult.get(itemPosition);
                         List<Track> tracks = (new ArrayList<Track>());
                         tracks.add(trackToAdd);

                         ServerContacterFactory.getServerContacter().addTracks(mActivity.getUserId(),
                                 RoomActivity.getRoomID(), tracks, mActivity);
                         
                         
                         Toast.makeText(mActivity.getApplicationContext(), "You added " + trackToAdd.getTitle() + " to the queue",
         	                    Toast.LENGTH_SHORT).show();
                         // mTrackList.remove(itemPosition);
                         // mTrackAdapter.notifyDataSetChanged();

                     }
                 }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         // Do nothing
                     }
                 }).show();
	            
	            

			 }
			
		});
		
		 mActivity.getRecyclerView().setAdapter(mTrackAdapter);
		
	}
	
	
	public SearchEventNotifier getEn(){
		return en;
	}
	
}
