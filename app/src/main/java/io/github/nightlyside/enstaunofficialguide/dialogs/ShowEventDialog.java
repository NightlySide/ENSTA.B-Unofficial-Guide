package io.github.nightlyside.enstaunofficialguide.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.AssoEvent;
import io.github.nightlyside.enstaunofficialguide.data_structure.DateHelper;

public class ShowEventDialog extends Dialog {

    private AssoEvent event;
    private TextView show_event_localisation;
    private ImageButton getLocation;

    public ShowEventDialog(Context context, AssoEvent event) {
        super(context);
        this.event = event;
    }

    public ShowEventDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_show_event);


        TextView show_event_title = findViewById(R.id.show_event_title);
        TextView show_event_description = findViewById(R.id.show_event_descrption_desc);
        TextView show_event_start = findViewById(R.id.show_event_startdate_date);
        TextView show_event_end = findViewById(R.id.show_event_enddate_date);
        TextView show_event_asso_name = findViewById(R.id.show_event_asso_name);
        show_event_localisation = findViewById(R.id.show_event_localisation_localisation);
        getLocation = findViewById(R.id.show_event_get_location);
        Button closeButton = findViewById(R.id.show_event_close_btn);

        show_event_title.setText(event.title);
        show_event_description.setText(event.description);
        show_event_start.setText(DateHelper.dateHelperToString(event.startDate));
        show_event_end.setText(DateHelper.dateHelperToString(event.endDate));
        show_event_asso_name.setText(event.asso.name);
        show_event_localisation.setText(event.location);

        closeButton.setOnClickListener(view -> dismiss());
        getLocation.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("geo:48.419051,-4.472092?q=" + Uri.encode(event.location));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            getContext().startActivity(mapIntent);
        });
    }
}
