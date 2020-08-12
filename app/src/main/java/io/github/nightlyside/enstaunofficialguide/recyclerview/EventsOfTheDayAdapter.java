package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.AssoEvent;

public class EventsOfTheDayAdapter extends RecyclerView.Adapter<EventsOfTheDayHolder> {

    private List<AssoEvent> eventList = new ArrayList<>();

    public EventsOfTheDayAdapter() { }

    @Override
    public EventsOfTheDayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_asso_event, parent, false);
        return new EventsOfTheDayHolder(view);
    }

    @Override
    public void onBindViewHolder(EventsOfTheDayHolder holder, int position) {
        AssoEvent evenobj = eventList.get(position);
        holder.bind(evenobj);
    }

    @Override
    public int getItemCount() { return eventList.size(); }

    public void clear() { eventList.clear(); }
    public void add(AssoEvent event) { eventList.add(event); }
    public void add(List<AssoEvent> events) { eventList.addAll(events); }
}
