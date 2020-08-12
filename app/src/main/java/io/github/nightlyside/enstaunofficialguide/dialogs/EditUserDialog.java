package io.github.nightlyside.enstaunofficialguide.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;
import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;
import io.github.nightlyside.enstaunofficialguide.data_structure.User;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditCollocsFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditUsersFragment;
import io.github.nightlyside.enstaunofficialguide.misc.RoleLevel;
import io.github.nightlyside.enstaunofficialguide.misc.Utils;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class EditUserDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private User user;
    private ShowAndEditUsersFragment parent;

    private Button cancel, commit;
    private EditText edit_nom, edit_display_name, edit_password;
    private Spinner roleSpinner;

    public EditUserDialog(ShowAndEditUsersFragment parent, Context context, User user) {
        super(context);
        this.parent  = parent;
        this.context = context;
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_edit_user);

        cancel = findViewById(R.id.cancel);
        commit = findViewById(R.id.commit);
        edit_nom = findViewById(R.id.user_name_editview);
        edit_display_name = findViewById(R.id.user_display_name_editview);
        edit_password = findViewById(R.id.user_password_editview);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        commit.setOnClickListener(this);

        edit_nom.setText(user.username);
        edit_display_name.setText(user.display_name);
        edit_password.setText(""); // Empty to signal it's unchanged

        roleSpinner = findViewById(R.id.user_admin_role_spinner);
        if (RoleLevel.getLevelFromRole(MainActivity.loggedUser.role).isAllowed(RoleLevel.Level.EDITOR)) {
            ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(getContext(), R.array.roles, android.R.layout.simple_spinner_item);
            spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            roleSpinner.setAdapter(spinAdapter);
            if (Utils.roles.contains(user.role))
                roleSpinner.setSelection(Utils.roles.indexOf(user.role));
            else
                roleSpinner.setSelection(spinAdapter.getCount());
        } else {
            findViewById(R.id.user_admin_role_textview).setVisibility(View.GONE);
            roleSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        String password;
        if (edit_password.getText().toString().equals(""))
            password = user.password;
        else
            password = Utils.md5(edit_password.getText().toString());

        String role;
        //((TextView)roleSpinner.getSelectedView()).getText().toString()
        switch (roleSpinner.getSelectedItemPosition()) {
            case 0:
                role = "admin";
                break;
            case 1:
                role = "moderateur";
                break;
            case 2:
                role = "editeur";
                break;
            default:
                role = "membre";
                break;
        }

        Log.d("UserEditDebug", "Role detected : " + role);

        User newUser = new User(user.id,
                    edit_nom.getText().toString(),
                    password,
                    edit_display_name.getText().toString(),
                    role,
                    user.assosJoined);

        // If there is no change we send no data
        if (user.equals(newUser)) {
            Log.d("UserEditDebug", "Same users skipping...");
            dismiss();
        } else {
            try {
                String assoList = Utils.hashsetToString(newUser.assosJoined);
                String query = "update-user.php?token=" + MainActivity.loggedUser.jwt_token
                        + "&id=" + newUser.id
                        + "&username=" + URLEncoder.encode(newUser.username, StandardCharsets.UTF_8.toString())
                        + "&display_name=" + URLEncoder.encode(newUser.display_name, StandardCharsets.UTF_8.toString())
                        + "&password=" + URLEncoder.encode(newUser.password, StandardCharsets.UTF_8.toString())
                        + "&role=" + newUser.role
                        + "&assos_joined=" + assoList;

                Log.d("UserEditDebug", query);

                NetworkManager.getInstance().makeJSONRequest(query, new NetworkResponseListener<String>() {
                    @Override
                    public void getResult(String object) throws JSONException {
                        JSONObject response = new JSONObject(object);
                        if(response.getString("message").equals("User updated successfully.")) {
                            Toast.makeText(getContext(), "Utilisateur mis à jour avec succès.", Toast.LENGTH_SHORT).show();
                            parent.getUsersInfos();
                            dismiss();
                        } else {
                            // Error while updating user
                            Toast.makeText(getContext(), "Erreur : "+response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (UnsupportedEncodingException e) {
                Log.e("EditUserDebug", e.toString());
            }
        }
    }
}
