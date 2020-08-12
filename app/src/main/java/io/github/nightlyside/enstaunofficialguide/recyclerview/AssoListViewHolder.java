package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import io.github.nightlyside.enstaunofficialguide.activities.AssoGestionActivity;
import io.github.nightlyside.enstaunofficialguide.activities.RegisterActivity;
import io.github.nightlyside.enstaunofficialguide.fragments.AssoListFragment;
import io.github.nightlyside.enstaunofficialguide.misc.Utils;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class AssoListViewHolder extends RecyclerView.ViewHolder {

    private AssoListFragment parent;
    private Association asso;
    private TextView name;
    private TextView description;
    private Button joinBtn;
    public boolean hasJoined = false;
    private boolean isEditing;

    // itemview = 1 card
    public AssoListViewHolder(final AssoListFragment parent, boolean isEditing, final View itemView) {
        super(itemView);
        this.parent = parent;
        this.isEditing = isEditing;

        name = (TextView) itemView.findViewById(R.id.assolist_card_title);
        description = (TextView) itemView.findViewById(R.id.assolist_card_text);
        joinBtn = (Button) itemView.findViewById(R.id.assolist_join_btn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(view.getContext());
                dBuilder.setTitle("Rejoindre / Quitter une association")
                        .setMessage("Voulez vraiment rejoindre/quitter l'association "+ asso.name +" ?\n\nSi vous étiez président, vous le resterez en la rejoignant de nouveau.")
                        .setPositiveButton("Accepter", (dialogInterface, i) -> {
                            hasJoined =!hasJoined;
                            update_btn(false);
                        })
                        .setNegativeButton("Annuler", (dialogInterface, i) -> dialogInterface.dismiss());
                dBuilder.create().show();
            }
        });
    }

    private void update_btn(boolean onSetup) {
        if (!hasJoined) {
            joinBtn.setText("Rejoindre");
            joinBtn.setBackgroundColor(Color.parseColor("#03A9F4"));
            joinBtn.setTextColor(Color.parseColor("#FFFFFF"));

            if (MainActivity.loggedUser.assosJoined.contains(asso.id))
                MainActivity.loggedUser.assosJoined.remove(asso.id);
        } else {
            joinBtn.setText("Rejoint");
            joinBtn.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
            joinBtn.setTextColor(Color.parseColor("#ededed"));

            MainActivity.loggedUser.assosJoined.add(asso.id);
        }
        if (!onSetup)
            updateJoinedAssos();
    }

    public void bind(Association asso) {
        this.asso = asso;
        name.setText(asso.name);
        description.setText(asso.description);

        if (MainActivity.loggedUser == null || !MainActivity.loggedUser.isConnected || isEditing)
        {
            joinBtn.setEnabled(false);
            joinBtn.setVisibility(View.GONE);

            if (isEditing) {
                itemView.setOnClickListener(view -> {
                    Intent assoGestionActivity = new Intent(itemView.getContext(), AssoGestionActivity.class);
                    itemView.getContext().startActivity(assoGestionActivity);
                });
            }
        } else {
            hasJoined = MainActivity.loggedUser.assosJoined.contains(asso.id);
            update_btn(true);

            if (!asso.isOpenToRegister) {
                joinBtn.setEnabled(false);
                joinBtn.setOnClickListener(view -> Toast.makeText(itemView.getContext(), "L'association a décidé de ne pas ouvrir ses inscriptions.", Toast.LENGTH_SHORT).show());
            }
        }
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
