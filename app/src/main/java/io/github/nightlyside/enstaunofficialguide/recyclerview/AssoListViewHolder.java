package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.fragments.AssoListFragment;
import io.github.nightlyside.enstaunofficialguide.misc.Utils;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class AssoListViewHolder extends RecyclerView.ViewHolder {

    private AssoListFragment parent;
    private int asso_id;
    private TextView name;
    private TextView description;
    private Button joinBtn;
    public boolean hasJoined = false;

    // itemview = 1 card
    public AssoListViewHolder(final AssoListFragment parent, final View itemView) {
        super(itemView);
        this.parent = parent;

        name = (TextView) itemView.findViewById(R.id.assolist_card_title);
        description = (TextView) itemView.findViewById(R.id.assolist_card_text);
        joinBtn = (Button) itemView.findViewById(R.id.assolist_join_btn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasJoined = !hasJoined;
                update_btn(false);
            }
        });
    }

    private void update_btn(boolean onSetup) {
        if (!hasJoined) {
            joinBtn.setText("Rejoindre");
            joinBtn.setBackgroundColor(Color.parseColor("#03A9F4"));
            joinBtn.setTextColor(Color.parseColor("#FFFFFF"));

            if (MainActivity.loggedUser.assosJoined.contains(asso_id))
                MainActivity.loggedUser.assosJoined.remove(asso_id);
        } else {
            joinBtn.setText("Rejoint");
            joinBtn.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
            joinBtn.setTextColor(Color.parseColor("#ededed"));

            MainActivity.loggedUser.assosJoined.add(asso_id);
        }
        if (!onSetup)
            updateJoinedAssos();
    }

    public void bind(Association asso) {
        asso_id = asso.id;
        name.setText(asso.name);
        description.setText(asso.description);

        if (MainActivity.loggedUser == null || !MainActivity.loggedUser.isConnected)
        {
            joinBtn.setEnabled(false);
            joinBtn.setVisibility(View.GONE);
        } else {
            hasJoined = MainActivity.loggedUser.assosJoined.contains(asso.id);
            update_btn(true);
        }
        //Picasso.with(imageView.getContext()).load(myObject.getImageUrl()).centerCrop().fit().into(imageView);
    }

    private void updateJoinedAssos() {
        // We save the user
        MainActivity.loggedUser.saveUserToSharedPreferences(itemView.getContext());
        // We get the list of assos
        String list_assos = Utils.hashsetToString(MainActivity.loggedUser.assosJoined);
        String query_string = "modify-joined-assos.php?token="+MainActivity.loggedUser.jwt_token+"&assos_list="+list_assos;
        Log.d("AssoListDebug", query_string);

        NetworkManager.getInstance().makeJSONRequest(query_string, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONObject response = new JSONObject(result);
                if (response.getString("message").equals("BDD updated successfully.")) {
                    String msg;
                    if (hasJoined)
                        msg = "Vous avez rejoint le club  '" + name.getText().toString() + "' avec succès.";
                    else
                        msg = "Vous avez quitté le club  '" + name.getText().toString() + "' avec succès.";
                    Toast.makeText(itemView.getContext(), msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(itemView.getContext(), "Erreur : "+response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
