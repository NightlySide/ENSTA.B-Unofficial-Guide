package io.github.nightlyside.enstaunofficialguide.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.io.InputStream;

public class FirstRunDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        if (getArguments() != null) {
            if (getArguments().getBoolean("notAlertDialog")) {
                return super.onCreateDialog(savedInstanceState);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bienvenue sur le guide non-officiel de l'ENSTA Breton !");

        try {
            InputStream is = getContext().getAssets().open("firstrunmessage.html");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String msg = new String(buffer);
            builder.setMessage(Html.fromHtml(msg, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        builder.setPositiveButton("Reçu !", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        //https://www.journaldev.com/23096/android-dialogfragment

        return builder.create();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean setFullScreen = false;
        if (getArguments() != null) {
            setFullScreen = getArguments().getBoolean("fullScreen");
        }

        if (setFullScreen)
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
}
