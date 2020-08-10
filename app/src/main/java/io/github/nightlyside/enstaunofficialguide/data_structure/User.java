package io.github.nightlyside.enstaunofficialguide.data_structure;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class User {

    public int id;
    public String username;
    public String display_name;
    public String password;
    public String role;
    public String jwt_token;
    public boolean isConnected;

    public User(int id, String username, String display_name, String password, String role) {
        this.id = id;
        this.username = username;
        this.display_name = display_name;
        this.password = password;
        this.role = role;
    }

    public void setToken(String jwt_token) {
        this.jwt_token = jwt_token;
        isConnected = true;
    }

    public void disconnect(Context context) {
        id = -1;
        username = null;
        password = null;
        role = null;
        jwt_token = null;
        isConnected = false;

        // Writing to the shared preferences
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String userjson = gson.toJson(this);
        editor.putString("userData", userjson);
        editor.apply();
    }

    static public void saveUserFromJWTToken(final Context context, final String jwt_token, final NetworkResponseListener<Boolean> listener) {
        String query_string = "jwt-decode.php?token="+jwt_token;
        NetworkManager.getInstance().makeJSONRequest(query_string, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONObject response = new JSONObject(result);
                JSONObject userData = response.getJSONObject("data");

                // Creating the user
                User u = new User(userData.getInt("id"),
                             userData.getString("username"),
                             userData.getString("display_name"),
                             userData.getString("password"),
                             userData.getString("role"));
                u.setToken(jwt_token);
                u.isConnected = true;

                // Writing to the shared preferences
                SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_key_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Gson gson = new Gson();
                String userjson = gson.toJson(u);
                editor.putString("userData", userjson);
                editor.apply();

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
}
