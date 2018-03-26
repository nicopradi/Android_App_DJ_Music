package ch.epfl.sweng.djmusicapp.activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.Track;
import ch.epfl.sweng.djmusicapp.Room.GENRES;
import ch.epfl.sweng.djmusicapp.adapters.RoomAdapter;
import ch.epfl.sweng.djmusicapp.data.ServerContacterFactory;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.GetRoomsCallback;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.SubscribeRoomCallback;
import ch.epfl.sweng.djmusicapp.helpers.DefaultNotificationSystem;
import ch.epfl.sweng.djmusicapp.helpers.SortRooms;
import ch.epfl.sweng.djmusicapp.listeners.SwitchAudioVolumeListener;
import ch.epfl.sweng.djmusicapp.listeners.VoteListener;
import ch.epfl.sweng.djmusicapp.utils.MyUtils;

/**
 * Activity displaying the different rooms created by users
 * 
 * Clicking on a room button to join will redirect the user to the Room Activity
 * 
 * @author Tristan Marchal, SebastienAndreina, csbenz
 * 
 */
public class ChooseRoomActivity extends Activity implements GetRoomsCallback,
        OnClickListener, SubscribeRoomCallback {

    private ChooseRoomActivity mActivity = this;
    public static final String ALERT_DIALOG_TITLE = "PASSWORD";
    public static final String ALERT_DIALOG_MESSAGE = "Enter room's password";
    public static final String ALERT_DIALOG_CANCEL = "Cancel";
    public static final String ALERT_DIALOG_CONFIRM = "Ok";
    public static final String SORT_BY_NUMBER_USERS = "#Users";
    public static final String SORT_BY_NAME = "Alphabetically";
    public static final String GENRE_POP = "Pop";
    public static final String GENRE_ROCK = "Rock";
    public static final String GENRE_CLASSICAL = "Classical";
    public static final String GENRE_METAL = "Metal";
    public static final String GENRE_HOUSE = "House";
    public static final String GENRE_TRANSE = "Transe";
    public static final String GENRE_TECHNO = "Techno";
    public static final String GENRE_ELECTRO = "Electro";
    public static final String GENRE_FOLK = "Folk";
    public static final String GENRE_JAZZ = "Jazz";
    public static final String GENRE_COUNTRY = "Country";
    public static final String GENRE_LATINO = "Latino";
    public static final String GENRE_OTHERS = "Others";
    public static final String GENRE_ANYTHING = "Anything";

    private ArrayList<Room> mRooms = new ArrayList<Room>();
    private Set<String> mGenreSelected = new HashSet<String>();
    private ServerContacterInterface mContacter;
    private RecyclerView mRecyclerView;
    private RoomAdapter mRoomAdapter;
    private String mRoomId;
    private Room mSelectedRoom;
    private CheckBox mPublicCheckBox;
    private CheckBox mPrivateCheckBox;
    private Spinner mSpinnerSort;
    private ProgressBar mLoadingSpinner;
    private boolean alreadyClicked = false;

    private CheckBox mCheckBoxPop;
    private CheckBox mCheckBoxRock;
    private CheckBox mCheckBoxClassical;
    private CheckBox mCheckBoxMetal;
    private CheckBox mCheckBoxHouse;
    private CheckBox mCheckBoxTranse;
    private CheckBox mCheckBoxTechno;
    private CheckBox mCheckBoxElectro;
    private CheckBox mCheckBoxFolk;
    private CheckBox mCheckBoxJazz;
    private CheckBox mCheckBoxCountry;
    private CheckBox mCheckBoxLatino;
    private CheckBox mCheckBoxOthers;
    private CheckBox mCheckBoxAnything;
    private CheckBox mCheckBoxAll;

    private LinearLayout mLayout3;
    private LinearLayout mLayout4;
    private LinearLayout mLayout5;
    private LinearLayout mLayout6;
    private LinearLayout mLayout7;
    private LinearLayout mLayout8;

    /* Retrieve rooms from the server and dynamically create a button for each */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_room);

        mRecyclerView = (RecyclerView) findViewById(R.id.listViewChooseRoomActivity);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mLoadingSpinner = (ProgressBar) findViewById(R.id.spinner_progress_bar);

        mRoomAdapter = new RoomAdapter(mRooms);
        mRoomAdapter.setOnClickItemListener(this);
        mRecyclerView.setAdapter(mRoomAdapter);
        mPublicCheckBox = (CheckBox) findViewById(R.id.displayPublicCheckBoxChooseRoomActivity);
        mPrivateCheckBox = (CheckBox) findViewById(R.id.displayPrivateCheckBoxChooseRoomActivity);
        setPublicCheckBox();
        setPrivateCheckBox();
        mSpinnerSort = (Spinner) findViewById(R.id.spinnerSortChooseRoomActivity);
        mSpinnerSort.setSelection(0);

        mSpinnerSort.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                @SuppressWarnings("unchecked")
                ArrayAdapter<String> mAdapter = (ArrayAdapter<String>) mSpinnerSort
                        .getAdapter();
                if ((position == mAdapter.getPosition(SORT_BY_NUMBER_USERS))
                        || (position == mAdapter.getPosition(SORT_BY_NAME))) {
                    fetchRooms();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCheckBoxPop = (CheckBox) findViewById(R.id.checkBoxPopChooseRoomActivity);
        mCheckBoxRock = (CheckBox) findViewById(R.id.checkBoxRockChooseRoomActivity);
        mCheckBoxClassical = (CheckBox) findViewById(R.id.checkBoxClassicalChooseRoomActivity);
        mCheckBoxMetal = (CheckBox) findViewById(R.id.checkBoxMetalChooseRoomActivity);
        mCheckBoxHouse = (CheckBox) findViewById(R.id.checkBoxHouseChooseRoomActivity);
        mCheckBoxTranse = (CheckBox) findViewById(R.id.checkBoxTranseChooseRoomActivity);
        mCheckBoxTechno = (CheckBox) findViewById(R.id.checkBoxTechnoChooseRoomActivity);
        mCheckBoxElectro = (CheckBox) findViewById(R.id.checkBoxElectroChooseRoomActivity);
        mCheckBoxFolk = (CheckBox) findViewById(R.id.checkBoxFolkChooseRoomActivity);
        mCheckBoxJazz = (CheckBox) findViewById(R.id.checkBoxJazzChooseRoomActivity);
        mCheckBoxCountry = (CheckBox) findViewById(R.id.checkBoxCountryChooseRoomActivity);
        mCheckBoxLatino = (CheckBox) findViewById(R.id.checkBoxLatinoChooseRoomActivity);
        mCheckBoxOthers = (CheckBox) findViewById(R.id.checkBoxOthersChooseRoomActivity);
        mCheckBoxAnything = (CheckBox) findViewById(R.id.checkBoxAnythingChooseRoomActivity);
        mCheckBoxAll = (CheckBox) findViewById(R.id.checkBoxAllChooseRoomActivity);

        mLayout3 = (LinearLayout) findViewById(R.id.linearLayout3ChooseRoomActivity);
        mLayout4 = (LinearLayout) findViewById(R.id.linearLayout4ChooseRoomActivity);
        mLayout5 = (LinearLayout) findViewById(R.id.linearLayout5ChooseRoomActivity);
        mLayout6 = (LinearLayout) findViewById(R.id.linearLayout6ChooseRoomActivity);
        mLayout7 = (LinearLayout) findViewById(R.id.linearLayout7ChooseRoomActivity);
        mLayout8 = (LinearLayout) findViewById(R.id.linearLayout8ChooseRoomActivity);

        addAll();
        hideGenre();

        setGenreCheckBoxListener(mCheckBoxPop, GENRE_POP);
        setGenreCheckBoxListener(mCheckBoxRock, GENRE_ROCK);
        setGenreCheckBoxListener(mCheckBoxClassical, GENRE_CLASSICAL);
        setGenreCheckBoxListener(mCheckBoxMetal, GENRE_METAL);
        setGenreCheckBoxListener(mCheckBoxHouse, GENRE_HOUSE);
        setGenreCheckBoxListener(mCheckBoxTranse, GENRE_TRANSE);
        setGenreCheckBoxListener(mCheckBoxTechno, GENRE_TECHNO);
        setGenreCheckBoxListener(mCheckBoxElectro, GENRE_ELECTRO);
        setGenreCheckBoxListener(mCheckBoxFolk, GENRE_FOLK);
        setGenreCheckBoxListener(mCheckBoxJazz, GENRE_JAZZ);
        setGenreCheckBoxListener(mCheckBoxCountry, GENRE_COUNTRY);
        setGenreCheckBoxListener(mCheckBoxLatino, GENRE_LATINO);
        setGenreCheckBoxListener(mCheckBoxOthers, GENRE_OTHERS);
        setGenreCheckBoxListener(mCheckBoxAnything, GENRE_ANYTHING);
        setAllCheckBox();

        // Fetch server for info
        mContacter = ServerContacterFactory.getServerContacter();
        fetchRooms();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
        case R.id.action_refresh_choose_room:
            fetchRooms();
            Toast.makeText(this, "Rooms refreshed !", Toast.LENGTH_SHORT)
                    .show();
            return true;
        case R.id.action_search_choose_room:
            Intent switchToSearchRoomActivityIntent = new Intent(this,
                    SearchRoomActivity.class);
            startActivity(switchToSearchRoomActivityIntent);
            return true;
        case R.id.action_add_choose_room:
            Intent switchToCreateRoomActivityIntent = new Intent(this,
                    CreateRoomActivity.class);
            startActivity(switchToCreateRoomActivityIntent);
            return true;
        case R.id.action_options_choose_room:
            displayGenre();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Override the behavior of the back button from the ChooseRoomActivity to
     * return to the MainActivity activity
     */
    @Override
    public void onBackPressed() {
        Intent switchToMainActivityIntent = new Intent(this, MainActivity.class);

        startActivity(switchToMainActivityIntent);
    }

    @Override
    public void onClick(View v) {
        if (!alreadyClicked) {
            alreadyClicked = true;
            // Called when click on an item of the list
            int itemPosition = mRecyclerView.getChildPosition(v);
            mSelectedRoom = mRooms.get(itemPosition);
            mRoomId = mSelectedRoom.getId();

            if (mSelectedRoom.isPublic()) {
                mContacter.subscribeRoom(mRoomId, MyUtils.getUserId(this), "",
                        this);
            } else {
                alreadyClicked = false;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                alertDialog.setTitle(ALERT_DIALOG_TITLE);

                alertDialog.setMessage(ALERT_DIALOG_MESSAGE);

                final EditText mPasswordField = new EditText(this);
                LinearLayout.LayoutParams mParameters = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                mPasswordField.setLayoutParams(mParameters);
                alertDialog.setView(mPasswordField);
                mPasswordField.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                alertDialog.setPositiveButton(ALERT_DIALOG_CONFIRM,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String mTypedPassword = mPasswordField
                                        .getText().toString();
                                mContacter.subscribeRoom(mRoomId, MyUtils
                                        .getUserId(ChooseRoomActivity.this),
                                        mTypedPassword, mActivity);
                            }
                        });

                alertDialog.setNegativeButton(ALERT_DIALOG_CANCEL,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface.GetRoomsCallback#onGotRooms
     * (java.util.List)
     */
    @Override
    public void onGotRooms(List<Room> rooms) {
        hideLoadingSpinner();
        mRooms.clear();
        for (Room room : rooms) {
            if (mGenreSelected.contains(room.getGenre())) {
                mRooms.add(room);
            }
        }

        switch (mSpinnerSort.getSelectedItemPosition()) {
        case 0:
            SortRooms.sortByUsers(mRooms);
            break;
        case 1:
            SortRooms.sortByName(mRooms);
            break;
        default:
            break;
        }

        mRoomAdapter.notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.ServerContacterInterface.GetRoomsCallback#onFail
     * (java.lang.String)
     */
    @Override
    public void onFail(String errorMessage) {
        this.alreadyClicked = false;
        // TODO remove Toast for release
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        hideLoadingSpinner();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.SubscribeRoomCallback
     * #onSubscribedRoom()
     */
    @Override
    public void onSubscribedRoom(boolean isAdmin) {
        this.alreadyClicked = false;

        // radu - notification
        Track currentTrack = mSelectedRoom.getCurrentTrack();
        DefaultNotificationSystem notifications = DefaultNotificationSystem
                .getInstance(this).setRoom(mSelectedRoom)
                .setTrack(currentTrack).setTargetClass(RoomActivity.class)
                .setUserId(MyUtils.getUserId(this)).setIsAdmin(isAdmin);

        notifications
                .subscribeReceiverToPauseButton(new SwitchAudioVolumeListener());
        notifications.subscribeReceiverToVoteButton(new VoteListener());

        notifications.show();
        // end

        Intent switchToRoomActivityIntent = new Intent(this, RoomActivity.class);

        switchToRoomActivityIntent.putExtra("ID", mRoomId);
        switchToRoomActivityIntent.putExtra("isAdmin", isAdmin);
        startActivity(switchToRoomActivityIntent);
    }

    private void setPublicCheckBox() {
        mPublicCheckBox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        if (isChecked) {
                            if (mPrivateCheckBox.isChecked()) {
                                mContacter.getRooms(
                                        Room.STATUS.BOTH.getStatusCode(),
                                        mActivity);
                                showLoadingSpinner();
                            } else {
                                mContacter.getRooms(
                                        Room.STATUS.PUBLIC.getStatusCode(),
                                        mActivity);
                                showLoadingSpinner();
                            }
                        } else {
                            if (mPrivateCheckBox.isChecked()) {
                                mContacter.getRooms(
                                        Room.STATUS.PRIVATE.getStatusCode(),
                                        mActivity);
                                showLoadingSpinner();
                            } else {
                                mRooms.clear();
                                mRoomAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void setPrivateCheckBox() {
        mPrivateCheckBox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        if (isChecked) {
                            if (mPublicCheckBox.isChecked()) {
                                mContacter.getRooms(
                                        Room.STATUS.BOTH.getStatusCode(),
                                        mActivity);
                                showLoadingSpinner();
                            } else {
                                mContacter.getRooms(
                                        Room.STATUS.PRIVATE.getStatusCode(),
                                        mActivity);
                                showLoadingSpinner();
                            }
                        } else {
                            if (mPublicCheckBox.isChecked()) {
                                mContacter.getRooms(
                                        Room.STATUS.PUBLIC.getStatusCode(),
                                        mActivity);
                                showLoadingSpinner();
                            } else {
                                mRooms.clear();
                                mRoomAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void setAllCheckBox() {
        mCheckBoxAll
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        if (isChecked) {
                            mCheckBoxPop.setChecked(false);
                            mCheckBoxRock.setChecked(false);
                            mCheckBoxClassical.setChecked(false);
                            mCheckBoxMetal.setChecked(false);
                            mCheckBoxHouse.setChecked(false);
                            mCheckBoxTranse.setChecked(false);
                            mCheckBoxTechno.setChecked(false);
                            mCheckBoxElectro.setChecked(false);
                            mCheckBoxFolk.setChecked(false);
                            mCheckBoxJazz.setChecked(false);
                            mCheckBoxCountry.setChecked(false);
                            mCheckBoxLatino.setChecked(false);
                            mCheckBoxOthers.setChecked(false);
                            mCheckBoxAnything.setChecked(false);
                            addAll();
                            fetchRooms();
                        } else if (!(mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked()
                                || mCheckBoxPop.isChecked() || mCheckBoxPop
                                .isChecked())) {
                            removeAll();
                            fetchRooms();
                        }
                    }
                });
    }

    private void fetchRooms() {
        if (mPublicCheckBox.isChecked() && mPrivateCheckBox.isChecked()) {
            showLoadingSpinner();
            mContacter.getRooms(Room.STATUS.BOTH.getStatusCode(), this);
        } else if (mPublicCheckBox.isChecked()
                && (!mPrivateCheckBox.isChecked())) {
            showLoadingSpinner();
            mContacter.getRooms(Room.STATUS.PUBLIC.getStatusCode(), this);
        } else if ((!mPublicCheckBox.isChecked())
                && mPrivateCheckBox.isChecked()) {
            showLoadingSpinner();
            mContacter.getRooms(Room.STATUS.PRIVATE.getStatusCode(), this);
        } else {
            mRooms.clear();
            mRoomAdapter.notifyDataSetChanged();
        }
    }

    private void hideGenre() {
        mLayout3.setVisibility(View.GONE);
        mLayout4.setVisibility(View.GONE);
        mLayout5.setVisibility(View.GONE);
        mLayout6.setVisibility(View.GONE);
        mLayout7.setVisibility(View.GONE);
        mLayout8.setVisibility(View.GONE);
    }

    private void displayGenre() {
        mLayout3.setVisibility(View.VISIBLE);
        mLayout4.setVisibility(View.VISIBLE);
        mLayout5.setVisibility(View.VISIBLE);
        mLayout6.setVisibility(View.VISIBLE);
        mLayout7.setVisibility(View.VISIBLE);
        mLayout8.setVisibility(View.VISIBLE);
    }

    public void onClickCloseButton(View view) {
        hideGenre();
    }

    private void addAll() {
        for (GENRES genre : GENRES.values()) {
            mGenreSelected.add(genre.toString());
        }
    }

    private void removeAll() {
        for (GENRES genre : GENRES.values()) {
            mGenreSelected.remove(genre.toString());
        }
    }

    private void setGenreCheckBoxListener(CheckBox checkBox, final String genre) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked && mCheckBoxAll.isChecked()) {
                    mCheckBoxAll.setChecked(false);
                    removeAll();
                    mGenreSelected.add(genre);
                    fetchRooms();
                } else if (isChecked && (!mCheckBoxAll.isChecked())) {
                    mGenreSelected.add(genre);
                    fetchRooms();
                } else if (!isChecked) {
                    mGenreSelected.remove(genre);
                    fetchRooms();
                }
            }
        });
    }

    private void showLoadingSpinner() {
        mLoadingSpinner.setVisibility(View.VISIBLE);
    }

    private void hideLoadingSpinner() {
        mLoadingSpinner.setVisibility(View.GONE);
    }
}
