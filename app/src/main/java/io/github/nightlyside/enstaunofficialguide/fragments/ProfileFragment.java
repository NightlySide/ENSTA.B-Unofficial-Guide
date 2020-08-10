package io.github.nightlyside.enstaunofficialguide.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;

public class ProfileFragment extends Fragment {

    private Button logout;
    private TextView usernametext;
    private ListView infos;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
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
       usernametext.setText(MainActivity.loggedUser.display_name);

       ArrayList<String> itemList = new ArrayList<String>();
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, itemList);
       infos = (ListView) view.findViewById(R.id.infos);
       infos.setAdapter(adapter);

       itemList.add("Username : "+ MainActivity.loggedUser.username);
       itemList.add("Role : " + MainActivity.loggedUser.role);
       itemList.add("Clubs rejoints : aucun");
       adapter.notifyDataSetChanged();
    }
}