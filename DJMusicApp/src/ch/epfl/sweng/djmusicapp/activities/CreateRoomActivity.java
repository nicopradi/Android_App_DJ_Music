package ch.epfl.sweng.djmusicapp.activities;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import ch.epfl.sweng.djmusicapp.MyConstants;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.data.ServerContacterFactory;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.AddRoomCallback;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.SubscribeRoomCallback;
import ch.epfl.sweng.djmusicapp.utils.MyUtils;

/**
 * 
 * @author Tristan Marchal, SebastienAndreina
 * 
 */
/*
 * Activity where the user can provide with a description of a room to create
 * its own
 */
public class CreateRoomActivity extends Activity implements AddRoomCallback {

    private EditText mPasswordField;
    private EditText mPasswordConfirmField;
    private Activity mActivity = this;
    private Button mButton;
    private EditText mEditText;
    private Spinner mSpinnerGenre;
    private Spinner mSpinnerStatus;
    private LinearLayout mLayout;
    private LinearLayout mLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        mEditText = (EditText) findViewById(R.id.roomNameCreateRoomActivity);
        mSpinnerGenre = (Spinner) findViewById(R.id.spinnerGenreCreateRoomActivity);
        mSpinnerStatus = (Spinner) findViewById(R.id.spinnerStatusCreateRoomActivity);
        mButton = (Button) findViewById(R.id.confirmRoomNameButtonCreateRoomActivity);
        mLayout = (LinearLayout) findViewById(R.id.linearLayout2CreateRoomActivity);
        mLayout2 = (LinearLayout) findViewById(R.id.linearLayout3CreateRoomActivity);
        displayPasswordField();
        mPasswordField.setVisibility(View.GONE);
        mPasswordConfirmField.setVisibility(View.GONE);

        mSpinnerStatus.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                @SuppressWarnings("unchecked")
                ArrayAdapter<String> mAdapter = (ArrayAdapter<String>) mSpinnerStatus
                        .getAdapter();
                if (position == mAdapter.getPosition(Room.PRIVATE_STRING)) {
                    mPasswordField.setVisibility(View.VISIBLE);
                    mPasswordConfirmField.setVisibility(View.VISIBLE);
                    mButton.setEnabled(false);
                    Toast.makeText(mActivity, "Type room's password",
                            Toast.LENGTH_SHORT).show();
                } else if (position == mAdapter.getPosition(Room.PUBLIC_STRING)) {
                    mPasswordField.setVisibility(View.GONE);
                    mPasswordConfirmField.setVisibility(View.GONE);
                    mButton.setEnabled(true);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /* Switch to the RoomActivity activity */
    public void onClickCreateButtonFromCreateRoomActivity(View view) {
        String name = mEditText.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter a room name", Toast.LENGTH_LONG).show();
        } else if (!mPasswordField.getText().toString()
                .equals(mPasswordConfirmField.getText().toString())) {
            Toast.makeText(this, "Passwords are not the same",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Creating the room", Toast.LENGTH_LONG).show();

            String mSpinnerGenreSelected = mSpinnerGenre.getSelectedItem()
                    .toString();
            int status = 1;
            if (mSpinnerStatus.getSelectedItem().toString()
                    .equals(Room.PUBLIC_STRING)) {
                status = 0;
            }
            String userId = MyUtils.getUserId(this);
            Log.i("name", name);
            Log.i("genre", mSpinnerGenreSelected);
            Log.i("status", String.valueOf(status));
            Log.i("user id", userId);
            Log.i("status", String.valueOf(status));
            ServerContacterFactory.getServerContacter().addRoom(userId, name,
                    mSpinnerGenreSelected, status,
                    mPasswordField.getText().toString(), this);
            mButton.setEnabled(false);
        }
    }

    /*
     * Override the behavior of the back button from the CreateRoomActivity to
     * return to the ChooseRoomActivity activity
     */
    @Override
    public void onBackPressed() {
        Intent switchToChooseRoomActivityIntent = new Intent(this,
                ChooseRoomActivity.class);

        startActivity(switchToChooseRoomActivityIntent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.AddRoomCallback
     * #onFail(java.lang.String)
     */
    @Override
    public void onFail(String errorMessage) {
        mButton.setEnabled(true);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.AddRoomCallback
     * #onRoomAdded(java.lang.String)
     */
    @Override
    public void onRoomAdded(final String roomId) {

        ServerContacterFactory.getServerContacter().subscribeRoom(roomId,
                MyUtils.getUserId(this), mPasswordField.getText().toString(),
                new SubscribeRoomCallback() {

                    @Override
                    public void onSubscribedRoom(boolean isAdmin) {

                        Intent switchToRoomActivityIntent = new Intent(
                                CreateRoomActivity.this, RoomActivity.class);
                        switchToRoomActivityIntent.putExtra(
                                MyConstants.BUNDLE_ROOM_ID, roomId);
                        switchToRoomActivityIntent.putExtra("isAdmin", true);
                        startActivity(switchToRoomActivityIntent);

                    }

                    @Override
                    public void onFail(String errorMessage) {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private void displayPasswordField() {
        mButton.setEnabled(false);
        mPasswordField = new EditText(mActivity);
        mPasswordConfirmField = new EditText(mActivity);

        setPasswordField(mPasswordField, "Password");
        setPasswordField(mPasswordConfirmField, "Repeat password");

        mPasswordField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mButton.setEnabled(false);
                } else if ((s.length() != 0)
                        && (!mButton.isEnabled())
                        && (!mPasswordConfirmField.getText().toString()
                                .matches(""))) {
                    mButton.setEnabled(true);
                }
            }
        });

        mPasswordConfirmField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mButton.setEnabled(false);
                } else if ((s.length() != 0) && (!mButton.isEnabled())
                        && (!mPasswordField.getText().toString().matches(""))) {
                    mButton.setEnabled(true);
                }
            }
        });

        mLayout.addView(mPasswordField);
        mLayout2.addView(mPasswordConfirmField);
    }

    private void setPasswordField(EditText passwordField, String hint) {
        passwordField.setGravity(Gravity.CENTER);
        passwordField.setHint(hint);
        passwordField.setSingleLine();
        passwordField.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams mPasswordLayoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        passwordField.setLayoutParams(mPasswordLayoutParams);
    }
}
