package io.github.nightlyside.enstaunofficialguide.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;
import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditCollocsFragment;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class EditCollocDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private Colloc colloc;
    private ShowAndEditCollocsFragment parent;

    private Button cancel, commit;
    private EditText edit_nom, edit_adresse, edit_desc;
    private RadioButton bar_btn, colloc_btn;

    public EditCollocDialog(ShowAndEditCollocsFragment parent, Context context, Colloc col) {
        super(context);
        this.parent  = parent;
        this.context = context;
        this.colloc = col;
    }

    public EditCollocDialog(ShowAndEditCollocsFragment parent, Context context) {
        super(context);
        this.parent  = parent;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_edit_colloc);

        cancel = findViewById(R.id.cancel);
        commit = findViewById(R.id.commit);
        edit_nom = findViewById(R.id.colloc_name_editview);
        edit_adresse = findViewById(R.id.colloc_adresse_editview);
        edit_desc = findViewById(R.id.colloc_desc_textedit);
        bar_btn = findViewById(R.id.bar_choice);
        colloc_btn = findViewById(R.id.colloc_choice);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        commit.setOnClickListener(this);

        if (colloc != null) {
            edit_nom.setText(colloc.name);
            edit_adresse.setText(colloc.adresse);
            edit_desc.setText(colloc.description);

            if (colloc.bar_ou_colloc.equals("bar")) {
                bar_btn.setChecked(true);
                colloc_btn.setChecked(false);
            } else {
                bar_btn.setChecked(false);
                colloc_btn.setChecked(true);
            }
        }
    }

    @Override
    public void onClick(View view) {
        Colloc newCol;
        String bar_ou_col;
        if (bar_btn.isChecked())
            bar_ou_col = "bar";
        else
            bar_ou_col = "colloc";
        // If we create a new colloc
        if (colloc == null) {
            newCol = new Colloc(-1,
                    edit_nom.getText().toString(),
                    edit_adresse.getText().toString(),
                    edit_desc.getText().toString(),
                    bar_ou_col);
        } else {
            newCol = new Colloc(colloc.id,
                    edit_nom.getText().toString(),
                    edit_adresse.getText().toString(),
                    edit_desc.getText().toString(),
                    bar_ou_col);
        }

        // If there is no change we send no data
        if (colloc != null && colloc.equals(newCol)) {
            dismiss();
        } else {
            try {
                String query = "update-colloc.php?token=" + MainActivity.loggedUser.jwt_token
                        + "&id=" + newCol.id
                        + "&name=" + URLEncoder.encode(newCol.name, StandardCharsets.UTF_8.toString())
                        + "&adresse=" + URLEncoder.encode(newCol.adresse, StandardCharsets.UTF_8.toString())
                        + "&description=" + URLEncoder.encode(newCol.description, StandardCharsets.UTF_8.toString())
                        + "&colloc_ou_bar=" + newCol.bar_ou_colloc;
                Log.d("EditCollocDebug", "Query: " + query);
                NetworkManager.getInstance().makeJSONRequest(query, new NetworkResponseListener<String>() {
                    @Override
                    public void getResult(String object) throws JSONException {
                        JSONObject response = new JSONObject(object);
                        if (response.getString("message").equals("Colloc updated/created.")) {
                            Toast.makeText(context, "Colloc ou bar crée/mis à jour avec succès !", Toast.LENGTH_SHORT).show();
                            Log.d("EditCollocDebug", "Updated successfully !");
                            dismiss();
                            parent.getCollocsInfos();
                        } else {
                            // in that case we don't dismiss
                            Toast.makeText(context, "Erreur : " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.d("EditCollocDebug", "Error : " + response.getString("message"));
                        }
                    }
                });
            } catch (UnsupportedEncodingException e) {
                Log.e("Updating colloc data", e.toString());
            }
        }
    }
}
