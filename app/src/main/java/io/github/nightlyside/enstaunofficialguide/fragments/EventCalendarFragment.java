package io.github.nightlyside.enstaunofficialguide.fragments;

import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.AssoEvent;
import io.github.nightlyside.enstaunofficialguide.data_structure.DateHelper;
import io.github.nightlyside.enstaunofficialguide.dialogs.EditEventDialog;
import io.github.nightlyside.enstaunofficialguide.dialogs.ShowEventDialog;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class EventCalendarFragment extends Fragment implements MonthLoader.MonthChangeListener, WeekView.EventClickListener, WeekView.EventLongPressListener {

    private FloatingActionButton currentDayFab;
    private WeekView calendarView;
    private int initFocusOnEventId = -1;
    private AssoEvent initFocusEvent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public List<AssoEvent> eventList = new ArrayList<>();

    public EventCalendarFragment() {}

    public EventCalendarFragment(int eventId) {
        Log.d("NotificationDebug", "Clicked eventId : " + eventId);
        this.initFocusOnEventId = eventId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_calendar, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        calendarView = view.findViewById(R.id.weekView);
        calendarView.setMonthChangeListener(this);
        calendarView.setOnEventClickListener(this);
        calendarView.setEventLongPressListener(this);
        calendarView.setShowNowLine(true);
        calendarView.goToToday();
        calendarView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        currentDayFab = view.findViewById(R.id.current_dat_fab);
        currentDayFab.setOnClickListener(view1 -> {
            calendarView.goToToday();
            calendarView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        });

        getEvents();
    }

    public void getEvents() {
        eventList.clear();

        String query = "asso-events.php";
        NetworkManager.getInstance().makeJSONArrayRequest(query, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String object) throws JSONException {
                JSONArray response = new JSONArray(object);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);

                    AssoEvent ae = new AssoEvent(obj.getInt("id"),
                            obj.getInt("asso_id"),
                            obj.getString("title"),
                            obj.getString("date_start"),
                            obj.getString("date_end"),
                            obj.getString("location"),
                            obj.getString("description"));
                    eventList.add(ae);

                    if (initFocusOnEventId != -1 && obj.getInt("id") == initFocusOnEventId)
                        initFocusEvent = ae;
                }
                calendarView.notifyDatasetChanged();

                initFocusOnEventFunction();
            }
        });
    }

    private void initFocusOnEventFunction() {
        if (initFocusEvent != null) {
            calendarView.goToDate((Calendar) initFocusEvent.startDate.getCalendar().clone());
            //calendarView.goToHour(event.startDate.getHour());
        }
    }

    public List<WeekViewEvent> getMonthEvents(int year, int month) {
        List<WeekViewEvent> weekEvents = new ArrayList<>();
        Log.d("CalendarDebug", "Year : " + year + " Month : " + month);

        for (AssoEvent event : eventList) {
            if (DateHelper.eventInMonth(event.startDate, event.endDate, year, month)) {
                Log.d("CalendarDebug", "Found event : " + event.title);
                WeekViewEvent wve = new WeekViewEvent(String.valueOf(event.id),
                        event.title,
                        event.startDate.getCalendar(),
                        event.endDate.getCalendar());
                wve.setLocation(event.location);
                wve.setColor(event.asso.color);

                weekEvents.add(wve);
            }
        }

        return weekEvents;
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, newYear);
        c.set(Calendar.MONTH, newMonth);

        List<WeekViewEvent> current_month = getMonthEvents(c.get(Calendar.YEAR), c.get(Calendar.MONTH));
        Log.d("CalendarDebug", "Found " + current_month.size() + " events for month : " + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR));
        c.add(Calendar.MONTH, -1);
        List<WeekViewEvent> previous_month = getMonthEvents(c.get(Calendar.YEAR), c.get(Calendar.MONTH));
        Log.d("CalendarDebug", "Found " + previous_month.size() + " events for month : " + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR));
        c.add(Calendar.MONTH, 2);
        List<WeekViewEvent> next_month = getMonthEvents(c.get(Calendar.YEAR), c.get(Calendar.MONTH));
        Log.d("CalendarDebug", "Found " + next_month.size() + " events for month : " + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR));

        List<WeekViewEvent> events = new ArrayList<>();
        events.addAll(current_month);
        events.addAll(previous_month);
        events.addAll(next_month);
        Log.d("CalendarDebug", "Found " + events.size() + " events in total !");

        return events;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        AssoEvent assoEvent = null;
        for (AssoEvent ae : eventList) {
            if (ae.id == Integer.parseInt(event.getIdentifier())) {
                assoEvent = ae;
            }
        }
        if (assoEvent != null) {
            ShowEventDialog dialog = new ShowEventDialog(getContext(), assoEvent);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            Toast.makeText(getContext(), "Erreur : évènement non trouvé.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }
}
