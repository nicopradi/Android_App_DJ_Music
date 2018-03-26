package ch.epfl.sweng.djmusicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ch.epfl.sweng.djmusicapp.MyConstants;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.ui.CustomTextView;
import ch.epfl.sweng.djmusicapp.utils.MyUtils;

/**
 * Main screen where user can choose between joining a room, consulting their
 * music & modifying their settings
 * 
 * @author Tristan Marchal, csbenz
 * 
 */
public class MainActivity extends Activity {

    private CustomTextView mUserNameWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserNameWelcome = (CustomTextView) findViewById(R.id.userNameWelcomeTV);
        mUserNameWelcome.setText("Hi, " + MyUtils.getUserName(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
        case R.id.revoke_access:
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(MyConstants.BUNDLE_REVOKE_ACCESS, true);

            startActivity(intent);

            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Switch to the MusicActivity activity */
    public void onClickMyMusicButtonFromMainActivity(View view) {
        Intent switchToMusicActivityIntent = new Intent(this,
                MusicActivity.class);

        startActivity(switchToMusicActivityIntent);
    }

    /* Switch to the RoomActivity activity */
    public void onClickJoinRoomButtonFromMainActivity(View view) {
        Intent switchToChooseRoomActivityIntent = new Intent(this,
                ChooseRoomActivity.class);

        startActivity(switchToChooseRoomActivityIntent);
    }

    /*
     * Override the behavior of the back button from the MainActivity to leave
     * the app, not returning to the login screen
     */
    @Override
    public void onBackPressed() {
        Intent closeAppIntent = new Intent(Intent.ACTION_MAIN);
        closeAppIntent.addCategory(Intent.CATEGORY_HOME);
        closeAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(closeAppIntent);
    }

}
