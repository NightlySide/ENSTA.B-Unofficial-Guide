package io.github.nightlyside.enstaunofficialguide.data_structure;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class Association {

    public int id;
    public String name;
    public boolean isOpenToRegister;
    public String description;
    public int color;
    public HashSet<Integer> presidents;

    static public List<Association> cache_associations = new ArrayList<>();

    public Association(int id, String name, boolean isOpen, String description, int color, HashSet<Integer> presidents) {
        this.id = id;
        this.name = name;
        this.isOpenToRegister = isOpen;
        this.description = description;
        this.color = color;
        this.presidents = presidents;
    }

    static public Association getAssociationById(int id) {
        for (Association a : Association.cache_associations) {
            if (a.id == id)
                return a;
        }
        return null;
    }

    static public void updateAssociationLocalDB(NetworkResponseListener<Boolean> listener) {
        String query = "asso-list.php";
        NetworkManager.getInstance().makeJSONArrayRequest(query, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String object) throws JSONException {
                JSONArray response = new JSONArray(object);
                for (int i=0; i < response.length(); i++) {
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
                            col, prezList);

                    // remove if alreay present
                    Association asToRemove = null;
                    for (Association ac : Association.cache_associations) {
                        if (ac.id == a.id) {
                            asToRemove = ac;
                            break;
                        }
                    }
                    if (asToRemove != null)
                        Association.cache_associations.remove(asToRemove);
                    Association.cache_associations.add(a);
                }
                listener.getResult(true);
            }
        });
    }

    static public boolean isUserPresident(User user) {
        for (Association a : Association.cache_associations) {
            if (a.presidents.contains(user.id))
                return true;
        }
        return false;
    }
}
