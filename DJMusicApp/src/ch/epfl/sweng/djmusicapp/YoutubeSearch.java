package ch.epfl.sweng.djmusicapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;
import ch.epfl.sweng.djmusicapp.activities.RoomActivity;
import ch.epfl.sweng.djmusicapp.activities.SearchMusicActivity;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

public class YoutubeSearch implements YoutubeSearchInterface {

    private static final long NUMBER_OF_VIDEOS = 20;

    private static YouTube youtube;

    public void search(String query, SearchCallback cb, SearchMusicActivity activity) {


        try {
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("DjTest").build();

            YouTube.Search.List search = youtube.search().list("id,snippet");
            String apiKey = "AIzaSyBwp1bcyV2eHurBw7aoOfVh5Yq8Fygd9Ag";
            search.setKey(apiKey);
            search.setQ(query);

            search.setType("video");

            search.setFields("items(id/kind,id/videoId," + "snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList == null) {
                Log.e("noresult", "1");
            }
            if (searchResultList != null) {

                Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();

                if (!iteratorSearchResults.hasNext()) {
                    Log.e(" There aren't any results for your query.", "1");
                }

                List<Track> results = new ArrayList<Track>();

                while (iteratorSearchResults.hasNext()) {

                    SearchResult singleVideo = iteratorSearchResults.next();
                    ResourceId rID = singleVideo.getId();
                    Log.e(singleVideo.getSnippet().getTitle(), "test video");

                    // Log.e(rID.getKind(),"test");
                    if (rID.getKind().equals("youtube#video")) {
                    	String title;
                    	String artist;
                    	if(singleVideo.getSnippet().getTitle().contains(" - ")){
                    		String[] parts = singleVideo.getSnippet().getTitle().split(" - ");
                    		title = parts[1];
                    		artist = parts[0];
                    	}else{
                    		title = singleVideo.getSnippet().getTitle();
                    		artist = "inconnu";
                    	}
//                        Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                        Log.e("is in the if", "test");
                        // add a video to the list (0 for the length because the server modify it anyway)
                        results.add(new Track(rID.getVideoId().toString(), title, artist,
                                "inconnu", "others", 0));

                    }
                }

                cb.onFinish(results, activity);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
        
        
        public void search(String query, SearchCallback cb, RoomActivity activity) {


            try {
                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("DjTest").build();

                YouTube.Search.List search = youtube.search().list("id,snippet");
                String apiKey = "AIzaSyBwp1bcyV2eHurBw7aoOfVh5Yq8Fygd9Ag";
                search.setKey(apiKey);
                search.setQ(query);

                search.setType("video");

                search.setFields("items(id/kind,id/videoId," + "snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(NUMBER_OF_VIDEOS);

                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();
                if (searchResultList == null) {
                    Log.e("noresult", "1");
                }
                if (searchResultList != null) {

                    Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();

                    if (!iteratorSearchResults.hasNext()) {
                        Log.e(" There aren't any results for your query.", "1");
                    }

                    List<Track> results = new ArrayList<Track>();

                    while (iteratorSearchResults.hasNext()) {

                        SearchResult singleVideo = iteratorSearchResults.next();
                        ResourceId rID = singleVideo.getId();
                        Log.e(singleVideo.getSnippet().getTitle(), "test video");

                        // Log.e(rID.getKind(),"test");
                        if (rID.getKind().equals("youtube#video")) {
                        	String title;
                        	String artist;
                        	if(singleVideo.getSnippet().getTitle().contains(" - ")){
                        		String[] parts = singleVideo.getSnippet().getTitle().split(" - ");
                        		title = parts[1];
                        		artist = parts[0];
                        	}else{
                        		title = singleVideo.getSnippet().getTitle();
                        		artist = "inconnu";
                        	}
//                            Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                            Log.e("is in the if", "test");
                            // add a video to the list (0 for the length because the server modify it anyway)
                            results.add(new Track(rID.getVideoId().toString(), title, artist,
                                    "inconnu", "others", 0));

                        }
                    }

                    cb.onFinish(results, activity);
                }
            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    
}