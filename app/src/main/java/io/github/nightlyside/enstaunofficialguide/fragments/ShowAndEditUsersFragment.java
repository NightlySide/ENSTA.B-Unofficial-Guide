package io.github.nightlyside.enstaunofficialguide.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.recyclerview.widget.SortedList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shadow.ShadowDrawableWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;
import io.github.nightlyside.enstaunofficialguide.data_structure.User;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;
import io.github.nightlyside.enstaunofficialguide.recyclerview.ShowUsersAdapter;

public class ShowAndEditUsersFragment extends Fragment implements OnQueryTextListener {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<User> userList = new ArrayList<>();

    private ShowUsersAdapter usersAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_user_data, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersAdapter = new ShowUsersAdapter(this);
        recyclerView.setAdapter(usersAdapter);
        searchView = view.findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(this);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        getUsersInfos();
    }

    public void getUsersInfos() {
        userList.clear();
        usersAdapter.clear();

        String query_url = "users.php?token="+ MainActivity.loggedUser.jwt_token;
        NetworkManager.getInstance().makeJSONRequest(query_url, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONObject response = new JSONObject(result);
                if (!response.getString("message").equals("User list shown with sucess")) {
                    Toast.makeText(getContext(), "Vous n'avez pas les droits pour accèder à ces informations !", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                else {
                    JSONArray userArray = response.getJSONArray("data");
                    for (int i = 0; i < userArray.length(); i++) {
                        JSONObject obj = userArray.getJSONObject(i);
                        HashSet<Integer> assoList = new HashSet<>();
                        if (!obj.getString("assos_joined").equals("")) {
                            String[] id_list = obj.getString("assos_joined").split(",");
                            for (String s : id_list) {
                                assoList.add(Integer.valueOf(s.trim()));
                            }
                        }

                        User u = new User(obj.getInt("id"),
                                obj.getString("username"),
                                obj.getString("password"),
                                obj.getString("display_name"),
                                obj.getString("role"),
                                assoList);
                        userList.add(u);
                    }
                }
                usersAdapter.add(userList);
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        final List<User> filteredModelList = filter(userList, query);
        usersAdapter.replaceAll(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private static List<User> filter(List<User> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<User> filteredModelList = new ArrayList<>();
        for (User model : models) {
            final String text = model.display_name.toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
