/**
 * @author csbenz
 */
package ch.epfl.sweng.djmusicapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author csbenz
 * 
 */
public class User {

    public static final String USER_ID_KEY = "user_id";
    public static final String USER_NAME_KEY = "user_name";

    private String mId;
    private String mUserName;

    public User(String id, String userName) {

        if (id == null || userName == null) {
            throw new NullPointerException("null argument");
        }

        this.mId = id;
        this.mUserName = userName;
    }

    public static List<User> createListOfUsersFromJSONArray(JSONArray jsonArray)
            throws JSONException {

        List<User> users = new ArrayList<User>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject userJSON = (JSONObject) jsonArray.get(i);
            User user = fromJson(userJSON);
            users.add(user);
        }

        return users;
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {

        String id = jsonObject.getString(USER_ID_KEY);
        String userName = jsonObject.getString(USER_NAME_KEY);

        return new User(id, userName);
    }

    /**
     * Create a Json representation of the current User.
     * 
     * @return json representation of the current user
     */
    public JSONObject toJSON() throws JSONException {

        if (mId == null || mUserName == null) {
            throw new NullPointerException();
        }

        JSONObject userJson = new JSONObject();
        userJson.put(USER_ID_KEY, mId);
        userJson.put(USER_NAME_KEY, mUserName);

        return userJson;
    }

    /**
     * @return the id
     */
    public String getId() {
        return mId;
    }

    /**
     * @return the user
     */
    public String getUsername() {
        return mUserName;
    }

}
