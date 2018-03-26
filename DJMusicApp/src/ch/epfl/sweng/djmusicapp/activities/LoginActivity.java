package ch.epfl.sweng.djmusicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;
import ch.epfl.sweng.djmusicapp.MyConstants;
import ch.epfl.sweng.djmusicapp.R;
import ch.epfl.sweng.djmusicapp.User;
import ch.epfl.sweng.djmusicapp.data.ServerContacterFactory;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.LoginCallback;
import ch.epfl.sweng.djmusicapp.utils.MyUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * 
 * @author csbenz
 * 
 */
public class LoginActivity extends Activity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private SignInButton btnSignIn;
    private ProgressBar mLoadingSpinner;

    private boolean revokeAccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        mLoadingSpinner = (ProgressBar) findViewById(R.id.spinner_progress_bar);

        btnSignIn.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            revokeAccess = getIntent().getExtras().getBoolean(
                    MyConstants.BUNDLE_REVOKE_ACCESS);
        }

        if (revokeAccess) {
            showLoadingSpinner();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_sign_in:
            signInWithGplus();
            break;
        }
    }

    private void revokeAccess() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e("Christo", "User access revoked!");
                            Toast.makeText(LoginActivity.this,
                                    "Google+ sign-in access was revoked",
                                    Toast.LENGTH_SHORT).show();

                            revokeAccess = false;

                            SharedPreferences.Editor e = MyUtils
                                    .getSharedPrefs(LoginActivity.this).edit();
                            e.putString(MyConstants.SHARED_PREFS_USER_NAME,
                                    "Not signed in");
                            e.putString(
                                    MyConstants.SHARED_PREFS_USER_GOOGLE_ID,
                                    "Not signed in ID");
                            e.commit();

                            onBackPressed();
                        }
                    });
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        hideLoadingSpinner();
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
            Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;

        if (!revokeAccess) {
            saveProfileInfo();

            ServerContacterFactory.getServerContacter()
                    .login(new User(MyUtils.getUserId(this),
                            MyUtils.getUserName(this)), new LoginCallback() {

                        @Override
                        public void onLogin() {
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFail(String errorMessage) {
                            Toast.makeText(LoginActivity.this, errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            revokeAccess();
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnecting()) {
            showLoadingSpinner();

            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            Log.i("Christo", "1");
            try {
                Log.i("Christo", "2");
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                Log.i("Christo", "3");
            } catch (SendIntentException e) {
                Log.i("Christo", "4");
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void saveProfileInfo() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String id = currentPerson.getId();

                Log.i("localUser", "Should be created now");

                SharedPreferences.Editor e = MyUtils.getSharedPrefs(this)
                        .edit();
                e.putString(MyConstants.SHARED_PREFS_USER_NAME, personName);
                e.putString(MyConstants.SHARED_PREFS_USER_GOOGLE_ID, id);
                e.commit();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent closeAppIntent = new Intent(Intent.ACTION_MAIN);
        closeAppIntent.addCategory(Intent.CATEGORY_HOME);
        closeAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(closeAppIntent);
    }

    private void showLoadingSpinner() {
        mLoadingSpinner.setVisibility(View.VISIBLE);
    }

    private void hideLoadingSpinner() {
        mLoadingSpinner.setVisibility(View.GONE);
    }

}