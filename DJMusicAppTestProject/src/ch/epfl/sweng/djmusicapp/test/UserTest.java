package ch.epfl.sweng.djmusicapp.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.djmusicapp.User;

public class UserTest extends TestCase {

    public void testIdAccess() {
        User user = new User("12345", "Nico");

        assertEquals("12345", user.getId());
    }

    public void testUsernameAccess() {
        User user = new User("12345", "Nico");

        assertEquals("Nico", user.getUsername());
    }

    public void testUserToJson() {
        User user = new User("12345", "Nico");

        try {
            JSONObject userJson = user.toJSON();

            assertEquals("12345", userJson.getString(User.USER_ID_KEY));
            assertEquals("Nico", userJson.getString(User.USER_NAME_KEY));

        } catch (JSONException e) {
            fail("failed parsing User to json");
        }
    }

    public void testUserFromJson() {
        try {
            JSONObject userJson = new JSONObject();
            userJson.put(User.USER_ID_KEY, "12345");
            userJson.put(User.USER_NAME_KEY, "Nico");
            // TODO question : put above in setup() ? How to be sure it's
            // correct ?

            User user = User.fromJson(userJson);

            assertEquals("12345", user.getId());
            assertEquals("Nico", user.getUsername());

        } catch (JSONException e) {
            fail();
        }
    }

    public void testCreateListOfUsersFromJsonArray() {
        try {
            List<User> userList = new ArrayList<User>();
            userList.add(new User("12345", "Nico"));
            userList.add(new User("67890", "Pradi"));
            userList.add(new User("99998", "Jean-Michel"));

            JSONArray userJsonArray = new JSONArray();
            for (User user : userList) {
                userJsonArray.put(user.toJSON());
            }
            // TODO question : put above in setup() ?

            List<User> users = User
                    .createListOfUsersFromJSONArray(userJsonArray);

            assertEquals("12345", users.get(0).getId());
            assertEquals("Nico", users.get(0).getUsername());

            assertEquals("67890", users.get(1).getId());
            assertEquals("Pradi", users.get(1).getUsername());

            assertEquals("99998", users.get(2).getId());
            assertEquals("Jean-Michel", users.get(2).getUsername());

        } catch (JSONException e) {
            fail();
        }
    }
}
