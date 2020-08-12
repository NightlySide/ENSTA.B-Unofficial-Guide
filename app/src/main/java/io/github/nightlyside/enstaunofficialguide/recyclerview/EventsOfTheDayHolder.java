package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.AssoEvent;

public class EventsOfTheDayHolder extends RecyclerView.ViewHolder {

    private TextView text_title, text_asso_name, text_description, text_date;
    private ImageButton saveBtn;

    public EventsOfTheDayHolder(View itemView) {
        super(itemView);

        text_title = itemView.findViewById(R.id.asso_event_title);
        text_asso_name = itemView.findViewById(R.id.asso_event_asso_name);
        text_description = itemView.findViewById(R.id.asso_event_description);
        text_date = itemView.findViewById(R.id.asso_event_time_startstop);
    }

    public void bind(AssoEvent event) {
        // link widgets to event data
        text_title.setText(event.title);
        text_asso_name.setText(event.asso.name);
        text_description.setText(event.description);
        text_date.setText(event.startDate.getStrHourMinute() + " - " + event.endDate.getStrHourMinute());
    }
}
