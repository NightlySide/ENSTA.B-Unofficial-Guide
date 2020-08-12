package io.github.nightlyside.enstaunofficialguide.data_structure;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class Association {

    public int id;
    public String name;
    public boolean isOpenToRegister;
    public String description;
    public int color;

    static public List<Association> cache_associations = new ArrayList<>();

    public Association(int id, String name, boolean isOpen, String description, int color) {
        this.id = id;
        this.name = name;
        this.isOpenToRegister = isOpen;
        this.description = description;
        this.color = color;
    }

    static public Association getAssociationById(int id) {
        for (Association a : Association.cache_associations) {
            if (a.id == id)
                return a;
        }
        return null;
    }

    static public void updateAssociationLocalDB() {
        cache_associations.clear();

        String query = "asso-list.php";
        NetworkManager.getInstance().makeJSONArrayRequest(query, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String object) throws JSONException {
                JSONArray response = new JSONArray(object);
                for (int i=0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);
                    int col = Color.parseColor("#" + obj.getString("color"));

                    Association a = new Association(obj.getInt("id"),
                            obj.getString("name"),
                            obj.getString("is_open_to_register").equals("1"),
                            obj.getString("description"),
                            col);
                    Association.cache_associations.add(a);
                }
            }
        });
    }
}
