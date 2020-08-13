package io.github.nightlyside.enstaunofficialguide.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.navigation.NavigationView;

import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.github.nightlyside.enstaunofficialguide.BackgroundServiceWorker;
import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.data_structure.User;
import io.github.nightlyside.enstaunofficialguide.fragments.AssoListFragment;
import io.github.nightlyside.enstaunofficialguide.dialogs.FirstRunDialogFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.EventCalendarFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.NewsFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.ProfileFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.SettingsFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditCollocsFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditUsersFragment;
import io.github.nightlyside.enstaunofficialguide.misc.RoleLevel;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    static public User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkManager.getInstance(this);

        loggedUser = User.getUserFromSharedPreferences(this);
        Association.updateAssociationLocalDB(object -> { invalidateOptionsMenu(); });

        if (loggedUser != null && loggedUser.isConnected)
            Log.d("MainDebug", "Found logged user : " + loggedUser.display_name + "(" + loggedUser.username + ")");

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.activity_main_frame_layout, new NewsFragment());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();

        // 6 - Configure all views

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("isFirstRun", true))
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("isFirstRun", false);
            editor.commit();

            FirstRunDialogFragment dialogFragment = new FirstRunDialogFragment();
            dialogFragment.show(getSupportFragmentManager().beginTransaction(), "First run message");
        }

        String menuFragment = getIntent().getStringExtra("menuFragment");
        if (menuFragment != null) {
            switch (menuFragment) {
                case "calendar":
                    int eventId = getIntent().getIntExtra("eventId", -1);
                    Log.d("NotificationDebug", "Received id : " + eventId);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.activity_main_frame_layout, new EventCalendarFragment(eventId));
                    ft.commit();
                    break;
                default:
                    break;
            }
        }

        // Background activity
        int REMINDER_HOUR = 7;
        int REMINDER_MINUTE = 30;
        long delay;
        Calendar cal = Calendar.getInstance();
        Calendar reminder_calendar = Calendar.getInstance();
        reminder_calendar.set(Calendar.MINUTE, REMINDER_MINUTE);
        reminder_calendar.set(Calendar.HOUR_OF_DAY, REMINDER_HOUR);

        if (cal.after(reminder_calendar)) {
            reminder_calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        delay = (reminder_calendar.getTime().getTime() - cal.getTime().getTime()) / 1000 / 60;
        Log.d("TimeDebug", "Delay of " + delay + " minutes.");

        if (loggedUser != null && loggedUser.isConnected) {
            Log.d("BackgroundServiceDebug", "Ready to send notification");
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            PeriodicWorkRequest eventNotificationRefresh = new PeriodicWorkRequest.Builder(BackgroundServiceWorker.class, 12, TimeUnit.HOURS, 15, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .setInitialDelay(delay, TimeUnit.MINUTES)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                    .addTag("daily-events-notification")
                    //.setInputData(new Data.Builder().putString("token", loggedUser.jwt_token).build())
                    .build();
            WorkManager.getInstance(this).enqueueUniquePeriodicWork("daily-events-notification", ExistingPeriodicWorkPolicy.KEEP, eventNotificationRefresh);

            /*WorkRequest eventNotificationRefresh = new OneTimeWorkRequest.Builder(BackgroundServiceWorker.class)
                    .setConstraints(constraints)
                    //.setInitialDelay(delay, TimeUnit.MINUTES)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                    .addTag("daily-events-notification")
                    //.setInputData(new Data.Builder().putString("token", loggedUser.jwt_token).build())
                    .build();
            WorkManager.getInstance(this).enqueue(eventNotificationRefresh);*/
        }
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // 4 - Handle Navigation Item Click
        int id = item.getItemId();
        FragmentTransaction ft;

        switch (id){
            case R.id.activity_main_drawer_news :
                 ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.activity_main_frame_layout, new NewsFragment());
                ft.commit();
                break;
            case R.id.activity_main_drawer_assos_list :
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.activity_main_frame_layout, new AssoListFragment(false));
                ft.commit();
                break;
            case R.id.activity_main_drawer_assos_schedule :
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.activity_main_frame_layout, new EventCalendarFragment());
                ft.commit();
                break;
            case R.id.activity_main_drawer_map:
                Intent mapintent = new Intent(this, MapsActivity.class);
                startActivity(mapintent);
                break;
            case R.id.activity_main_drawer_profile:
                if (loggedUser != null && loggedUser.isConnected)
                {
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.activity_main_frame_layout, new ProfileFragment());
                    ft.commit();
                } else {
                    Intent loginintent = new Intent(this, LoginActivity.class);
                    startActivity(loginintent);
                }
                break;
            case R.id.activity_main_drawer_register:
                Intent registerintent = new Intent(this, RegisterActivity.class);
                startActivity(registerintent);
                break;
            case R.id.activity_main_drawer_admin_users:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.activity_main_frame_layout, new ShowAndEditUsersFragment());
                ft.commit();
                break;
            case R.id.activity_main_drawer_admin_collocs:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.activity_main_frame_layout, new ShowAndEditCollocsFragment());
                ft.commit();
                break;
            case R.id.activity_main_drawer_prez_asso :
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.activity_main_frame_layout, new AssoListFragment(true));
                ft.commit();
                break;
            case R.id.activity_main_drawer_settings:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.activity_main_frame_layout, new SettingsFragment());
                ft.commit();
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    // 1 - Configure Toolbar
    private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    public void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu navmenu = navigationView.getMenu();

        if (loggedUser != null && loggedUser.isConnected) {
            navmenu.findItem(R.id.activity_main_drawer_profile).setTitle("Bonjour " + loggedUser.display_name + " !");
            navmenu.findItem(R.id.activity_main_drawer_register).setVisible(false);
        }

        Boolean show_admin = loggedUser != null && loggedUser.isConnected && RoleLevel.getLevelFromRole(loggedUser.role).isAllowed(RoleLevel.Level.EDITOR);
        navmenu.findItem(R.id.activity_main_drawer_admin_collocs).setVisible(show_admin);
        navmenu.findItem(R.id.activity_main_drawer_admin_users).setVisible(show_admin);
        navmenu.findItem(R.id.activity_main_drawer_prez_asso).setVisible(loggedUser != null && loggedUser.isConnected && Association.isUserPresident(loggedUser));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        configureNavigationView();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }
}