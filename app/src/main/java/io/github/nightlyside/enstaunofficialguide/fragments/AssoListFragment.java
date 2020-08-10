package io.github.nightlyside.enstaunofficialguide.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;
import io.github.nightlyside.enstaunofficialguide.recyclerview.AssoListAdapter;

public class AssoListFragment extends Fragment {

    private RecyclerView recyclerView;
    private AssoListAdapter assoListAdapter;
    private List<Association> assoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_asso_list, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.assolist_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assoListAdapter = new AssoListAdapter(assoList);
        recyclerView.setAdapter(assoListAdapter);

        getAssosList();
    }

    private void getAssosList() {
        String query_url = "asso-list.php";
        NetworkManager.getInstance().makeJSONArrayRequest(query_url, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONArray response = new JSONArray(result);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);
                    Association a = new Association(obj.getInt("id"),
                            obj.getString("name"),
                            obj.getString("is_open_to_register").equals("1"),
                            obj.getString("description"));
                    assoList.add(a);
                }
                assoListAdapter.notifyDataSetChanged();
            }
        });
    }
}