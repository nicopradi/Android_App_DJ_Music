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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.adapters.RoomAdapter;
import ch.epfl.sweng.djmusicapp.data.ServerContacterFactory;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.GetRoomsCallback;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.SubscribeRoomCallback;
import ch.epfl.sweng.djmusicapp.helpers.SortRooms;
import ch.epfl.sweng.djmusicapp.utils.MyUtils;

/**
 * Activity where user can type keyword in order to find a certain room/certain
 * kind of music e.g., keywords searched in description of the room or name of
 * the host
 * 
 * @author Tristan Marchal, SebastienAndreina, csbenz
 * 
 */
public class SearchRoomActivity extends Activity implements GetRoomsCallback, OnClickListener, SubscribeRoomCallback {

    private SearchRoomActivity mActivity = this;
    private RecyclerView mRecyclerView;
    private List<Room> mMatchingRooms = new ArrayList<Room>();
    private Set<String> mGenreSelected = new HashSet<String>();
    private RoomAdapter mRoomAdapter;
    private String mSearchInput;
    private ServerContacterInterface mContacter;
    private Room mSelectedRoom;
    private String mRoomId;
    private CheckBox mPublicCheckBox;
    private CheckBox mPrivateCheckBox;
    private Spinner mSpinnerSort;
    private ProgressBar mLoadingSpinner;
    private TextView mSearchField;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);

        mRecyclerView = (RecyclerView) findViewById(R.id.listViewSearchRoomActivity);
        mContacter = ServerContacterFactory.getServerContacter();
        mLoadingSpinner = (ProgressBar) findViewById(R.id.spinner_progress_bar);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mRoomAdapter = new RoomAdapter(mMatchingRooms);
        mRoomAdapter.setOnClickItemListener(this);
        mRecyclerView.setAdapter(mRoomAdapter);

        mPublicCheckBox = (CheckBox) findViewById(R.id.displayPublicCheckBoxSearchRoomActivity);
        mPrivateCheckBox = (CheckBox) findViewById(R.id.displayPrivateCheckBoxSearchRoomActivity);
        setPublicCheckBox();
        setPrivateCheckBox();
        mSpinnerSort = (Spinner) findViewById(R.id.spinnerSortSearchRoomActivity);
        mSpinnerSort.setSelection(0);

        mSpinnerSort.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                ArrayAdapter<String> mAdapter = (ArrayAdapter<String>) mSpinnerSort.getAdapter();
                if ((position == mAdapter.getPosition(ChooseRoomActivity.SORT_BY_NUMBER_USERS))
                        || (position == mAdapter.getPosition(ChooseRoomActivity.SORT_BY_NAME))) {
                    fetchRooms();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSearchField = (TextView) findViewById(R.id.searchRoomFieldSearchRoomActivity);
        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchInput = mSearchField.getText().toString().toLowerCase();
                fetchRooms();
            }
        });

        mCheckBoxPop = (CheckBox) findViewById(R.id.checkBoxPopSearchRoomActivity);
        mCheckBoxRock = (CheckBox) findViewById(R.id.checkBoxRockSearchRoomActivity);
        mCheckBoxClassical = (CheckBox) findViewById(R.id.checkBoxClassicalSearchRoomActivity);
        mCheckBoxMetal = (CheckBox) findViewById(R.id.checkBoxMetalSearchRoomActivity);
        mCheckBoxHouse = (CheckBox) findViewById(R.id.checkBoxHouseSearchRoomActivity);
        mCheckBoxTranse = (CheckBox) findViewById(R.id.checkBoxTranseSearchRoomActivity);
        mCheckBoxTechno = (CheckBox) findViewById(R.id.checkBoxTechnoSearchRoomActivity);
        mCheckBoxElectro = (CheckBox) findViewById(R.id.checkBoxElectroSearchRoomActivity);
        mCheckBoxFolk = (CheckBox) findViewById(R.id.checkBoxFolkSearchRoomActivity);
        mCheckBoxJazz = (CheckBox) findViewById(R.id.checkBoxJazzSearchRoomActivity);
        mCheckBoxCountry = (CheckBox) findViewById(R.id.checkBoxCountrySearchRoomActivity);
        mCheckBoxLatino = (CheckBox) findViewById(R.id.checkBoxLatinoSearchRoomActivity);
        mCheckBoxOthers = (CheckBox) findViewById(R.id.checkBoxOthersSearchRoomActivity);
        mCheckBoxAnything = (CheckBox) findViewById(R.id.checkBoxAnythingSearchRoomActivity);
        mCheckBoxAll = (CheckBox) findViewById(R.id.checkBoxAllSearchRoomActivity);

        mLayout3 = (LinearLayout) findViewById(R.id.linearLayout3SearchRoomActivity);
        mLayout4 = (LinearLayout) findViewById(R.id.linearLayout4SearchRoomActivity);
        mLayout5 = (LinearLayout) findViewById(R.id.linearLayout5SearchRoomActivity);
        mLayout6 = (LinearLayout) findViewById(R.id.linearLayout6SearchRoomActivity);
        mLayout7 = (LinearLayout) findViewById(R.id.linearLayout7SearchRoomActivity);
        mLayout8 = (LinearLayout) findViewById(R.id.linearLayout8SearchRoomActivity);

        addAll();
        hideGenre();

        setGenreCheckBoxListener(mCheckBoxPop, ChooseRoomActivity.GENRE_POP);
        setGenreCheckBoxListener(mCheckBoxRock, ChooseRoomActivity.GENRE_ROCK);
        setGenreCheckBoxListener(mCheckBoxClassical, ChooseRoomActivity.GENRE_CLASSICAL);
        setGenreCheckBoxListener(mCheckBoxMetal, ChooseRoomActivity.GENRE_METAL);
        setGenreCheckBoxListener(mCheckBoxHouse, ChooseRoomActivity.GENRE_HOUSE);
        setGenreCheckBoxListener(mCheckBoxTranse, ChooseRoomActivity.GENRE_TRANSE);
        setGenreCheckBoxListener(mCheckBoxTechno, ChooseRoomActivity.GENRE_TECHNO);
        setGenreCheckBoxListener(mCheckBoxElectro, ChooseRoomActivity.GENRE_ELECTRO);
        setGenreCheckBoxListener(mCheckBoxFolk, ChooseRoomActivity.GENRE_FOLK);
        setGenreCheckBoxListener(mCheckBoxJazz, ChooseRoomActivity.GENRE_JAZZ);
        setGenreCheckBoxListener(mCheckBoxCountry, ChooseRoomActivity.GENRE_COUNTRY);
        setGenreCheckBoxListener(mCheckBoxLatino, ChooseRoomActivity.GENRE_LATINO);
        setGenreCheckBoxListener(mCheckBoxOthers, ChooseRoomActivity.GENRE_OTHERS);
        setGenreCheckBoxListener(mCheckBoxAnything, ChooseRoomActivity.GENRE_ANYTHING);
        setAllCheckBox();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
        case R.id.action_refresh_search_room:
            fetchRooms();
            return true;
        case R.id.action_options_search_room:
            displayGenre();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent switchToChooseRoomActivityIntent = new Intent(this, ChooseRoomActivity.class);

        startActivity(switchToChooseRoomActivityIntent);
    }

    @Override
    public void onGotRooms(List<Room> rooms) {
        hideLoadingSpinner();
        MyUtils.hideSoftKeyboardImmediatly(this);

        mMatchingRooms.clear();
        for (Room room : rooms) {
            if (mSearchInput == null) {
                mSearchInput = "";
            }
            if (room.getName().toLowerCase().contains(mSearchInput)
                    || room.getGenre().toLowerCase().contains(mSearchInput)
                    || room.getStatusToString().toLowerCase().contains(mSearchInput)) {
                if (mGenreSelected.contains(room.getGenre())) {
                    mMatchingRooms.add(room);
                }
            }
        }

        switch (mSpinnerSort.getSelectedItemPosition()) {
        case 0:
            SortRooms.sortByUsers(mMatchingRooms);
            break;
        case 1:
            SortRooms.sortByName(mMatchingRooms);
            break;
        default:
            break;
        }

        mRoomAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFail(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        // Called when click on an item of the list
        int itemPosition = mRecyclerView.getChildPosition(v);
        mSelectedRoom = mMatchingRooms.get(itemPosition);
        mRoomId = mSelectedRoom.getId();

        if (mSelectedRoom.isPublic()) {
            mContacter.subscribeRoom(mRoomId, MyUtils.getUserId(this), "", mActivity);
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle(ChooseRoomActivity.ALERT_DIALOG_TITLE);

            alertDialog.setMessage(ChooseRoomActivity.ALERT_DIALOG_MESSAGE);

            final EditText mPasswordField = new EditText(this);
            LinearLayout.LayoutParams mParameters = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mPasswordField.setLayoutParams(mParameters);
            alertDialog.setView(mPasswordField);
            mPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            alertDialog.setPositiveButton(ChooseRoomActivity.ALERT_DIALOG_CONFIRM,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            String mTypedPassword = mPasswordField.getText().toString();
                            mContacter.subscribeRoom(mRoomId, MyUtils.getUserId(SearchRoomActivity.this),
                                    mTypedPassword, mActivity);
                        }
                    });

            alertDialog.setNegativeButton(ChooseRoomActivity.ALERT_DIALOG_CANCEL,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }
    }

    @Override
    public void onSubscribedRoom(boolean isAdmin) {
        Intent switchToRoomActivityIntent = new Intent(this, RoomActivity.class);

        switchToRoomActivityIntent.putExtra("ID", mRoomId);
        switchToRoomActivityIntent.putExtra("isAdmin", isAdmin);
        startActivity(switchToRoomActivityIntent);

    }

    private void setPublicCheckBox() {
        mPublicCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mPrivateCheckBox.isChecked()) {
                        mContacter.getRooms(Room.STATUS.BOTH.getStatusCode(), mActivity);
                        showLoadingSpinner();
                    } else {
                        mContacter.getRooms(Room.STATUS.PUBLIC.getStatusCode(), mActivity);
                        showLoadingSpinner();
                    }
                } else {
                    if (mPrivateCheckBox.isChecked()) {
                        mContacter.getRooms(Room.STATUS.PRIVATE.getStatusCode(), mActivity);
                        showLoadingSpinner();
                    } else {
                        mMatchingRooms.clear();
                        mRoomAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void setPrivateCheckBox() {
        mPrivateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mPublicCheckBox.isChecked()) {
                        mContacter.getRooms(Room.STATUS.BOTH.getStatusCode(), mActivity);
                        showLoadingSpinner();
                    } else {
                        mContacter.getRooms(Room.STATUS.PRIVATE.getStatusCode(), mActivity);
                        showLoadingSpinner();
                    }
                } else {
                    if (mPublicCheckBox.isChecked()) {
                        mContacter.getRooms(Room.STATUS.PUBLIC.getStatusCode(), mActivity);
                        showLoadingSpinner();
                    } else {
                        mMatchingRooms.clear();
                        mRoomAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void setAllCheckBox() {
        mCheckBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                } else if (!(mCheckBoxPop.isChecked() || mCheckBoxPop.isChecked() || mCheckBoxPop.isChecked()
                        || mCheckBoxPop.isChecked() || mCheckBoxPop.isChecked() || mCheckBoxPop.isChecked()
                        || mCheckBoxPop.isChecked() || mCheckBoxPop.isChecked() || mCheckBoxPop.isChecked()
                        || mCheckBoxPop.isChecked() || mCheckBoxPop.isChecked() || mCheckBoxPop.isChecked()
                        || mCheckBoxPop.isChecked() || mCheckBoxPop.isChecked())) {
                    removeAll();
                    fetchRooms();
                }
            }
        });
    }

    private void fetchRooms() {
        if (mPublicCheckBox.isChecked() && mPrivateCheckBox.isChecked()) {
            mContacter.getRooms(Room.STATUS.BOTH.getStatusCode(), this);
            showLoadingSpinner();
        } else if (mPublicCheckBox.isChecked() && !mPrivateCheckBox.isChecked()) {
            mContacter.getRooms(Room.STATUS.PUBLIC.getStatusCode(), this);
            showLoadingSpinner();
        } else if (!mPublicCheckBox.isChecked() && mPrivateCheckBox.isChecked()) {
            mContacter.getRooms(Room.STATUS.PRIVATE.getStatusCode(), this);
            showLoadingSpinner();
        } else {
            mMatchingRooms.clear();
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
        mGenreSelected.add(ChooseRoomActivity.GENRE_POP);
        mGenreSelected.add(ChooseRoomActivity.GENRE_ROCK);
        mGenreSelected.add(ChooseRoomActivity.GENRE_CLASSICAL);
        mGenreSelected.add(ChooseRoomActivity.GENRE_METAL);
        mGenreSelected.add(ChooseRoomActivity.GENRE_HOUSE);
        mGenreSelected.add(ChooseRoomActivity.GENRE_TRANSE);
        mGenreSelected.add(ChooseRoomActivity.GENRE_TECHNO);
        mGenreSelected.add(ChooseRoomActivity.GENRE_ELECTRO);
        mGenreSelected.add(ChooseRoomActivity.GENRE_FOLK);
        mGenreSelected.add(ChooseRoomActivity.GENRE_JAZZ);
        mGenreSelected.add(ChooseRoomActivity.GENRE_COUNTRY);
        mGenreSelected.add(ChooseRoomActivity.GENRE_LATINO);
        mGenreSelected.add(ChooseRoomActivity.GENRE_OTHERS);
        mGenreSelected.add(ChooseRoomActivity.GENRE_ANYTHING);
    }

    private void removeAll() {
        mGenreSelected.remove(ChooseRoomActivity.GENRE_POP);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_ROCK);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_CLASSICAL);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_METAL);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_HOUSE);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_TRANSE);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_TECHNO);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_ELECTRO);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_FOLK);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_JAZZ);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_COUNTRY);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_LATINO);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_OTHERS);
        mGenreSelected.remove(ChooseRoomActivity.GENRE_ANYTHING);
    }

    private void setGenreCheckBoxListener(CheckBox checkBox, final String genre) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
