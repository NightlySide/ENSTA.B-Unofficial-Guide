package io.github.nightlyside.enstaunofficialguide.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;
import io.github.nightlyside.enstaunofficialguide.recyclerview.AssoListAdapter;

public class AssoListFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
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
        assoListAdapter = new AssoListAdapter(this);
        recyclerView.setAdapter(assoListAdapter);
        searchView = view.findViewById(R.id.asso_searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                // Here is where we are going to implement the filter logic
                final List<Association> filteredModelList = filter(assoList, query);
                assoListAdapter.replaceAll(filteredModelList);
                recyclerView.scrollToPosition(0);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

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
                    int col = Color.parseColor("#" + obj.getString("color"));

                    Association a = new Association(obj.getInt("id"),
                            obj.getString("name"),
                            obj.getString("is_open_to_register").equals("1"),
                            obj.getString("description"),
                            col);
                    assoList.add(a);
                }
                assoListAdapter.add(assoList);
            }
        });
    }

    private static List<Association> filter(List<Association> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Association> filteredModelList = new ArrayList<>();
        for (Association model : models) {
            final String text = model.name.toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}