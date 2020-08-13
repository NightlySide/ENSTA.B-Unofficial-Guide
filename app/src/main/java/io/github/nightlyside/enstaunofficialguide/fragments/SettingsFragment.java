package io.github.nightlyside.enstaunofficialguide.fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;

public class SettingsFragment extends Fragment {

    private Button testNotif;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_settings, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        testNotif = view.findViewById(R.id.test_notification_btn);
        testNotif.setOnClickListener(btnview -> {
            Intent intent = new Intent(getContext(), MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            String channelId = "ENSTABUnofficialGuide";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);

            Notification notification = new NotificationCompat.Builder(getContext(), channelId)
                    .setSmallIcon(R.drawable.ic_assos_schedule)
                    .setContentTitle("Vous avez un créneau de prévu avec une asso.")
                    .setContentText("Date - Location")
                    .setWhen(System.currentTimeMillis())
                    //.setLargeIcon(emailObject.getSenderAvatar()) if we want to put icons to asso
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Ici ça sera le long text qui apparait en déroulant la notification.\nDescription complémentaire a faire en HTML")
                            .setSummaryText("Nom de l'association"))
                    .setContentIntent(pendingIntent)
                    .setChannelId(channelId)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);;
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify("TestNotification", 1, notification);

            Toast.makeText(getContext(), "Notification envoyée", Toast.LENGTH_SHORT).show();
        });
    }
}
