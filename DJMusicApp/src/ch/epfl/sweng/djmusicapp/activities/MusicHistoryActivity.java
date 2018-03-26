package ch.epfl.sweng.djmusicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ch.epfl.sweng.djmusicapp.R;

/**
 * 
 * @author Tristan Marchal
 * 
 */
/*
 * Activity used to consult the history of the musics played in this room
 */
public class MusicHistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_history);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.music_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Override the behavior of the back button from the MusicHistoryActivity to
     * return to the RoomActivity activity
     */
    @Override
    public void onBackPressed() {
        Intent switchToRoomActivityIntent = new Intent(this, RoomActivity.class);

        startActivity(switchToRoomActivityIntent);
    }
}
