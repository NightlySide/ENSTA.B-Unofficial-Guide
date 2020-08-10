package io.github.nightlyside.enstaunofficialguide.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;
import io.github.nightlyside.enstaunofficialguide.data_structure.User;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class ProfileFragment extends Fragment {

    private Button logout;
    private TextView usernametext;
    private ListView infos;

    private ArrayList<String> itemList;
    private ArrayAdapter<String> infosAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               MainActivity.loggedUser.disconnect(getContext());
               Toast.makeText(getContext(), "Vous avez été déconnecté.", Toast.LENGTH_SHORT).show();

               // Restart of the activity
               Intent intent = getActivity().getIntent();
               getActivity().finish();
               startActivity(intent);
            }
        });

        usernametext = (TextView) view.findViewById(R.id.display_name);

        itemList = new ArrayList<String>();
        infosAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, itemList);
        infos = (ListView) view.findViewById(R.id.infos);
        infos.setAdapter(infosAdapter);

        updateLocalInfos();

        User.updateUserFromCurrentJWTToken(getContext(), MainActivity.loggedUser.jwt_token, new NetworkResponseListener<Boolean>() {
            @Override
            public void getResult(Boolean object) { updateLocalInfos(); }
        });
    }

    private void updateLocalInfos() {
        itemList.clear();

        usernametext.setText(MainActivity.loggedUser.display_name);
        itemList.add("Username : "+ MainActivity.loggedUser.username);
        itemList.add("Role : " + MainActivity.loggedUser.role);
        infosAdapter.notifyDataSetChanged();

        updateJoinedClubs();
    }

    private void updateJoinedClubs() {
        NetworkManager.getInstance().makeJSONArrayRequest("asso-list.php", new NetworkResponseListener<String>() {
            @Override
            public void getResult(String object) throws JSONException {
                JSONArray response = new JSONArray(object);
                List<String> assos_joined_list = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);
                    if (MainActivity.loggedUser.assosJoined.contains(obj.getInt("id"))) {
                        assos_joined_list.add(obj.getString("name"));
                    }
                }

                java.util.Collections.sort(assos_joined_list);
                String list_assos = assos_joined_list.toString();
                list_assos = list_assos.substring(1, list_assos.length()-1);
                itemList.add("Clubs rejoints : " + list_assos);
                Log.d("ProfileDebug", list_assos);
                infosAdapter.notifyDataSetChanged();
            }
        });
    }
}