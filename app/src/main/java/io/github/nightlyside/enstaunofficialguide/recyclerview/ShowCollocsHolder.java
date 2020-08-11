package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;
import io.github.nightlyside.enstaunofficialguide.dialogs.EditCollocDialog;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditCollocsFragment;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class ShowCollocsHolder extends RecyclerView.ViewHolder {

    private ShowAndEditCollocsFragment parent;
    private Colloc col;
    private TextView title;
    private TextView text;
    private ImageButton editbtn;
    private ImageButton deletebtn;

    public ShowCollocsHolder(final ShowAndEditCollocsFragment parent, final View itemView) {
        super(itemView);
        this.parent = parent;

        title = itemView.findViewById(R.id.card_title);
        text = itemView.findViewById(R.id.card_text);
        editbtn = itemView.findViewById(R.id.edit_btn);
        deletebtn = itemView.findViewById(R.id.delete_btn);

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditCollocDialog dialog = new EditCollocDialog(parent, itemView.getContext(), col);
                dialog.show();
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(view.getContext());
                dBuilder.setTitle("Supprimer une colloc / un bar ?")
                        .setMessage("Voulez vous vraiment supprimer : '" + col.name + "' ?\n\nAttention: Cette action est irreversible !");
                dBuilder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        // Clicked delete
                        String query = "delete-colloc.php?token="+MainActivity.loggedUser.jwt_token
                                        +"&id="+col.id;
                        NetworkManager.getInstance().makeJSONRequest(query, new NetworkResponseListener<String>() {
                            @Override
                            public void getResult(String object) throws JSONException {
                                JSONObject response = new JSONObject(object);
                                if (!response.getString("message").equals("Colloc removed successfully."))
                                {
                                    Toast.makeText(itemView.getContext(), "Erreur : " + response.getString("message"), Toast.LENGTH_SHORT).show();
                                    parent.getCollocsInfos();
                                    dialogInterface.dismiss();
                                } else {
                                    Toast.makeText(itemView.getContext(), "Le bar/colloc à été supprimé avec succès !", Toast.LENGTH_SHORT).show();
                                    parent.getCollocsInfos();
                                    dialogInterface.dismiss();
                                }
                            }
                        });
                    }
                });
                dBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Clicked cancel
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = dBuilder.create();
                dialog.show();
            }
        });
    }

    public void bind(Colloc col) {
        this.col = col;

        title.setText(col.name);
        text.setText(col.adresse);
    }
}
