package io.github.nightlyside.enstaunofficialguide.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.data_structure.AssoEvent;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;
import io.github.nightlyside.enstaunofficialguide.data_structure.User;

public class CacheManager {

    public List<Association> associationList = new ArrayList<>();
    public List<User> userList = new ArrayList<>();
    public List<Colloc> collocList = new ArrayList<>();
    public List<AssoEvent> assoEventList = new ArrayList<>();

    static public int CACHE_EXPIRED = 30; // le cache est expiré dans 30mn
    private static CacheManager instance = null;

    private Context context;
    private SharedPreferences sharedPref;
    private NetworkManager networkManager;

    public CacheManager(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("CacheManager", Context.MODE_PRIVATE);
        networkManager = new NetworkManager(context);

        // On vérifie que le cache est encore valable
        Calendar now = Calendar.getInstance();
        String str_last_update = sharedPref.getString("lastUpdate", null);
        if (str_last_update != null) {
            // Si une version du cache existe
            Log.d("CacheDebug", "Found existing save in cache");
            Gson gson = new Gson();
            Calendar last_update = gson.fromJson(str_last_update, Calendar.class);
            Calendar ttl = (Calendar) last_update.clone();
            ttl.add(Calendar.MINUTE, CACHE_EXPIRED);

            // Si il n'est plus valable
            if (now.after(ttl)) {
                Log.d("CacheDebug", "Cache expired.");
                updateCache();
            } else {
                loadFromPreferences();
            }
        } else {
            Log.d("CacheDebug", "Setting first cache");
            updateCache();
        }
    }

    public static synchronized CacheManager getInstance(Context context)
    {
        if (null == instance)
            instance = new CacheManager(context);
        return instance;
    }

    //this is so you don't need to pass context each time
    public static synchronized CacheManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(CacheManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public void updateCache() {
        Log.d("CacheDebug", "Updating cache...");
        Toast.makeText(context, "Mise à jour des données en cache ...", Toast.LENGTH_SHORT).show();

        Calendar updateTime = Calendar.getInstance();

        loadFromServer();
        networkManager.requestQueue.addRequestFinishedListener(request -> {
            //Log.d("CacheDebug", "Taches restantes : " + networkManager.requestsCounter.get());
            if (networkManager.requestsCounter.get() == 0) {
                saveToPreferences(updateTime);
            }
        });
    }

    public void loadFromPreferences() {
        Log.d("CacheDebug", "Loading from preferences...");
        Gson gson = new Gson();
        associationList = gson.fromJson(sharedPref.getString("associationList", null), new TypeToken<List<Association>>(){}.getType());
        assoEventList = gson.fromJson(sharedPref.getString("assoEventList", null), new TypeToken<List<AssoEvent>>(){}.getType());
        //userList = gson.fromJson(sharedPref.getString("userList", null), new TypeToken<List<User>>(){}.getType());
        collocList = gson.fromJson(sharedPref.getString("collocList", null), new TypeToken<List<Colloc>>(){}.getType());
    }

    public void loadFromServer() {
        Log.d("CacheDebug", "Loading from server...");
        getAssociationsFromServer();
       // getUsersFromServer();
        getAssoEventsFromServer();
        getCollocsFromServer();
    }

    public void saveToPreferences(Calendar updateTime) {
        Log.d("CacheDebug", "Saving to sharedpreferences...");

        // On sauvegarde le cache obtenu dans les préférences
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String str_associationList = gson.toJson(associationList);
        String str_assoEventList = gson.toJson(assoEventList);
        String str_collocList = gson.toJson(collocList);
        // String str_userList = gson.toJson(userList);
        editor.putString("associationList", str_associationList);
        editor.putString("assoEventList", str_assoEventList);
        editor.putString("collocList", str_collocList);
        //editor.putString("userList", str_userList);

        editor.putString("lastUpdate", gson.toJson(updateTime));
        editor.apply();
    }

    public void getCollocsFromServer() {
        String query_string = "collocs.php";
        networkManager.makeJSONArrayRequest(query_string, result -> {
            JSONArray response = new JSONArray(result);
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = null;
                try {
                    // colloc construction
                    obj = response.getJSONObject(i);
                    int id = obj.getInt("id");
                    String name = obj.getString("name");
                    String adresse = obj.getString("adresse");
                    String description = obj.getString("description");
                    String bar_ou_colloc = obj.getString("colloc_ou_bar");

                    Colloc col = new Colloc(id, name, adresse, description, bar_ou_colloc);
                    collocList.add(col);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getAssoEventsFromServer() {
        String query = "asso-events.php";

        networkManager.makeJSONArrayRequest(query, object -> {
            JSONArray response = new JSONArray(object);
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);

                AssoEvent ae = new AssoEvent(obj.getInt("id"),
                        obj.getInt("asso_id"),
                        obj.getString("title"),
                        obj.getString("date_start"),
                        obj.getString("date_end"),
                        obj.getString("location"),
                        obj.getString("description"));
                assoEventList.add(ae);
            }
        });
    }

    public void getUsersFromServer() {

    }

    public void getAssociationsFromServer() {
        String query_url = "asso-list.php";
        networkManager.makeJSONArrayRequest(query_url, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONArray response = new JSONArray(result);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);
                    int col = Color.parseColor("#" + obj.getString("color"));

                    // Creating the prez list
                    HashSet<Integer> prezList = new HashSet<>();
                    String[] id_list = obj.getString("presidents").split(",");
                    for (String s : id_list) {
                        prezList.add(Integer.valueOf(s.trim()));
                    }

                    Association a = new Association(obj.getInt("id"),
                            obj.getString("name"),
                            obj.getString("is_open_to_register").equals("1"),
                            obj.getString("description"),
                            col, prezList,
                            obj.getInt("nb_members"));
                    associationList.add(a);
                }
            }
        });
    }
}
