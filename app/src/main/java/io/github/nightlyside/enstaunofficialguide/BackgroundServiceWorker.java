package io.github.nightlyside.enstaunofficialguide;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;
import io.github.nightlyside.enstaunofficialguide.data_structure.AssoEvent;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class BackgroundServiceWorker extends Worker {

    public List<AssoEvent> eventList;

    public BackgroundServiceWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        Log.d("BackgroundServiceDebug", "Doing my job...");

        // Do the work here
        eventList = getEvents();
        // Return with success
        return Result.success();
    }

    private List<AssoEvent> getEvents() {
        List<AssoEvent> res = new ArrayList<>();
        String query = "asso-events.php";

        NetworkManager.getInstance().makeJSONArrayRequest(query, object -> {
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
                sendNotification(ae);
            }
        });

        return res;
    }

    private void sendNotification(AssoEvent event) {
        // Log.d("NotificationDebug", "Sending notif with id : " + event.id);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("menuFragment", "calendar");
        intent.putExtra("eventId", event.id);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), event.id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "ENSTABUnofficialGuide";
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "ENSTA B. UnofficialGuide",
                NotificationManager.IMPORTANCE_DEFAULT);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_assos_schedule)
                .setContentTitle(event.title)
                .setContentText(event.startDate.getStrHourMinute() + " - " + event.endDate.getStrHourMinute() + "\n" + event.location)
                .setWhen(System.currentTimeMillis())
                //.setLargeIcon(emailObject.getSenderAvatar()) if we want to put icons to asso
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(event.startDate.getStrHourMinute() + " - " + event.endDate.getStrHourMinute() + "\n" + event.location + "\n\n" + event.description)
                        .setSummaryText(event.asso.name))
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);;
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify("EventNotification", event.id, notification);
    }
}
