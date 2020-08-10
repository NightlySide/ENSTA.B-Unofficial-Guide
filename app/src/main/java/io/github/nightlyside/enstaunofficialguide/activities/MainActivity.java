package io.github.nightlyside.enstaunofficialguide.activities;

import android.content.Intent;
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

import com.google.android.material.navigation.NavigationView;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.User;
import io.github.nightlyside.enstaunofficialguide.fragments.NewsFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.ProfileFragment;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //FOR DESIGN
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private String jwt_token;
    private String username;

    static public User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkManager.getInstance(this);

        loggedUser = User.getUserFromSharedPreferences(this);

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
        invalidateOptionsMenu();

        this.configureNavigationView();
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
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu navmenu = navigationView.getMenu();

        if (loggedUser != null && loggedUser.isConnected) {
            navmenu.findItem(R.id.activity_main_drawer_profile).setTitle("Bonjour " + loggedUser.display_name + " !");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        configureNavigationView();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }
}