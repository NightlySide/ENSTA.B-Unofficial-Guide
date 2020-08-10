package io.github.nightlyside.enstaunofficialguide.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.recyclerview.widget.SortedList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;
import io.github.nightlyside.enstaunofficialguide.recyclerview.AssoListAdapter;
import io.github.nightlyside.enstaunofficialguide.recyclerview.ShowCollocsAdapter;

public class ShowAndEditCollocsFragment extends Fragment implements OnQueryTextListener {

    private FloatingActionButton addLocBtn;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<Colloc> collocList = new ArrayList<>();

    private ShowCollocsAdapter collocsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_collocs_data, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.colloc_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        collocsAdapter = new ShowCollocsAdapter();
        recyclerView.setAdapter(collocsAdapter);
        searchView = view.findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(this);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        addLocBtn = view.findViewById(R.id.add_location);
        addLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getCollocsInfos();
    }

    public void getCollocsInfos() {
        String query_url = "collocs.php";
        NetworkManager.getInstance().makeJSONArrayRequest(query_url, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONArray response = new JSONArray(result);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);
                    Colloc col = new Colloc(obj.getInt("id"),
                            obj.getString("name"),
                            obj.getString("adresse"),
                            obj.getString("description"));
                    collocList.add(col);
                }
                collocsAdapter.add(collocList);
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        final List<Colloc> filteredModelList = filter(collocList, query);
        collocsAdapter.replaceAll(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private static List<Colloc> filter(List<Colloc> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Colloc> filteredModelList = new ArrayList<>();
        for (Colloc model : models) {
            final String text = model.name.toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
