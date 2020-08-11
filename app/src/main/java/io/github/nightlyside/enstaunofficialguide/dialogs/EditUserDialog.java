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
import io.github.nightlyside.enstaunofficialguide.data_structure.User;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditCollocsFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditUsersFragment;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class EditUserDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private User user;
    private ShowAndEditUsersFragment parent;

    private Button cancel, commit;
    private EditText edit_nom, edit_display_name, edit_password;

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
    }

    @Override
    public void onClick(View view) {
        String password;
        if (edit_password.getText().toString().equals(""))
            password = user.password;
        else
            password = edit_password.getText().toString();

        User newUser = new User(user.id,
                    edit_nom.getText().toString(),
                    password,
                    edit_display_name.getText().toString(),
                    user.role,
                    user.assosJoined);

        // If there is no change we send no data
        if (newUser.equals(newUser)) {
            dismiss();
        } else {
            // TODO : Requete pour update l'utilisateur
        }
    }
}
