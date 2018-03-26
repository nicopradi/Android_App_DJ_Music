package ch.epfl.sweng.djmusicapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import ch.epfl.sweng.djmusicapp.MyConstants;

/**
 * Provides several utility methods
 * 
 * @author csbenz
 * 
 */
public class MyUtils {

    /**
     * Get Default SharedPreferences of the app
     */
    public static SharedPreferences getSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get the Google ID of the user
     * 
     * @param context
     * @return the user's google ID, or "-1" if it is not set
     */
    public static String getUserId(Context context) {
        return getSharedPrefs(context).getString(
                MyConstants.SHARED_PREFS_USER_GOOGLE_ID, "-1");
    }

    /**
     * Get the Google username
     * 
     * @param context
     * @return the user's name from the Google authentication
     */
    public static String getUserName(Context context) {
        return getSharedPrefs(context).getString(
                MyConstants.SHARED_PREFS_USER_NAME, MyConstants.FAKE_USER_NAME);
    }

    /**
     * Hide the software keyboard immediately
     */
    public static void hideSoftKeyboardImmediatly(Activity activity) {
        try {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSoftKeyBoard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
