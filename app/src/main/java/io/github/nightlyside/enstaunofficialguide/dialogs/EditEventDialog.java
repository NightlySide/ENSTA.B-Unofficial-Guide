package io.github.nightlyside.enstaunofficialguide.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.AssoEvent;
import io.github.nightlyside.enstaunofficialguide.data_structure.DateHelper;

public class EditEventDialog extends Dialog {

    private AssoEvent event;
    private EditText edit_event_title, edit_event_description, edit_event_start, edit_event_end;
    private DateHelper start_helper, end_helper;

    public EditEventDialog(Context context, AssoEvent event) {
        super(context);
        this.event = event;
    }

    public EditEventDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_edit_event);


        edit_event_title = findViewById(R.id.event_title_edittext);
        edit_event_description = findViewById(R.id.event_text_edittext);
        edit_event_start = findViewById(R.id.event_start_date_edittext);
        edit_event_end = findViewById(R.id.event_end_date_edittext);

        if (event != null) {
            start_helper = event.startDate;
            end_helper = event.endDate;
            Log.d("EditEventDebug", event.title);
            edit_event_title.setText(event.title);
            edit_event_description.setText(event.description);
        } else {
            start_helper = new DateHelper();
            end_helper = new DateHelper();
        }

        edit_event_start.setText(DateHelper.dateHelperToString(start_helper));
        edit_event_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_picker(edit_event_start, start_helper);
            }
        });

        edit_event_end.setText(DateHelper.dateHelperToString(end_helper));
        edit_event_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_picker(edit_event_end, end_helper);
            }
        });
    }

    private void date_picker(final EditText date_edittext, final DateHelper helper) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                        helper.setYear(year);
                        helper.setMonth(monthOfYear);
                        helper.setDay(dayOfMonth);
                        time_picker(date_edittext, helper);
                    }
                }, helper.getYear(), helper.getMonth(), helper.getDay());
        datePickerDialog.show();
    }

    private void time_picker(final EditText date_edittext, final DateHelper helper) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,int minute) {
                        helper.setHour(hourOfDay);
                        helper.setMinute(minute);
                        date_edittext.setText(DateHelper.dateHelperToString(helper));
                    }
                }, helper.getHour(), helper.getMinute(), true);
        timePickerDialog.show();
    }
}
