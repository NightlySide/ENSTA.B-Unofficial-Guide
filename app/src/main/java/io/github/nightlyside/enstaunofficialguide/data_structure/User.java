package io.github.nightlyside.enstaunofficialguide.data_structure;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Objects;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class User {

    public int id;
    public String username;
    public String display_name;
    public String role;
    public String password;
    public String jwt_token;
    public boolean isConnected;
    public HashSet<Integer> assosJoined;

    public User(int id, String username, String password, String display_name, String role, HashSet<Integer> assosJoined) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.display_name = display_name;
        this.role = role;
        this.assosJoined = assosJoined;
    }

    public void setToken(String jwt_token) {
        this.jwt_token = jwt_token;
        isConnected = true;
    }

    public void disconnect(Context context) {
        id = -1;
        username = null;
        role = null;
        jwt_token = null;
        isConnected = false;

        saveUserToSharedPreferences(context);
    }

    static public void updateUserFromCurrentJWTToken(final Context context, final String jwt_token, final NetworkResponseListener<Boolean> listener) {
        String query_string = "update-jwt.php?token="+jwt_token;
        NetworkManager.getInstance().makeJSONRequest(query_string, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONObject response = new JSONObject(result);
                if (response.getString("message").equals("Sucessful update.")) {
                    User.saveUserFromJWTToken(context, response.getString("jwt"), listener);
                }
            }
        });
    }

    static public void saveUserFromJWTToken(final Context context, final String jwt_token, final NetworkResponseListener<Boolean> listener) {
        String query_string = "jwt-decode.php?token="+jwt_token;
        NetworkManager.getInstance().makeJSONRequest(query_string, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONObject response = new JSONObject(result);
                JSONObject userData = response.getJSONObject("data");

                // Creating the club list
                HashSet<Integer> assosList = new HashSet<>();
                if (!userData.getString("assos_joined").equals("")) {
                    String[] id_list = userData.getString("assos_joined").split(",");
                    for (String s : id_list) {
                        assosList.add(Integer.valueOf(s.trim()));
                    }
                }

                // Creating the user
                User u = new User(userData.getInt("id"),
                             userData.getString("username"),
                             userData.getString("password"),
                             userData.getString("display_name"),
                             userData.getString("role"),
                             assosList);
                u.setToken(jwt_token);
                u.isConnected = true;
                u.saveUserToSharedPreferences(context);

                listener.getResult(true);
            }
        });
    }

    static public User getUserFromSharedPreferences(Context context) {
        SharedPreferences mPref = context.getSharedPreferences(context.getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userjson = mPref.getString("userData", "");
        if (userjson.equals(""))
            return null;
        // Si c'est pas null on retourne la classe
        return gson.fromJson(userjson, User.class);
    }

    public void saveUserToSharedPreferences(Context context) {
        // Writing to the shared preferences
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String userjson = gson.toJson(this);
        editor.putString("userData", userjson);
        editor.apply();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                username.equals(user.username) &&
                display_name.equals(user.display_name) &&
                role.equals(user.role) &&
                password.equals(user.password) &&
                assosJoined.equals(user.assosJoined);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, display_name, role, password, assosJoined);
    }
}
