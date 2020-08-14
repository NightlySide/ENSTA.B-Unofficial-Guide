package io.github.nightlyside.enstaunofficialguide.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;

public class ShowAssoDialog extends Dialog {

    private Association asso;

    public ShowAssoDialog(Context context, Association asso) {
        super(context);
        this.asso = asso;
    }

    public ShowAssoDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_show_association);


        TextView show_asso_name = findViewById(R.id.show_asso_name);
        TextView show_asso_desc = findViewById(R.id.show_asso_descrption_desc);
        TextView show_asso_nb_members = findViewById(R.id.show_number_of_members);
        TextView show_asso_open_register = findViewById(R.id.show_asso_is_open_to_register);
        Button closeButton = findViewById(R.id.show_asso_close_btn);

        show_asso_name.setText(asso.name);
        show_asso_desc.setText(asso.description);
        show_asso_nb_members.setText(asso.nb_members + " membre(s)");

        if (asso.isOpenToRegister)
            show_asso_open_register.setText("Inscriptions ouvertes.");
        else
            show_asso_open_register.setText("Inscriptions fermées.");


        closeButton.setOnClickListener(view -> dismiss());
    }
}
