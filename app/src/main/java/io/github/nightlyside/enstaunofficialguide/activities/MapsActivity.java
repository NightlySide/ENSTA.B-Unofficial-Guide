package io.github.nightlyside.enstaunofficialguide.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;
import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.misc.Utils;
import io.github.nightlyside.enstaunofficialguide.network.CacheManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;

    public GoogleMap mMap;
    private FloatingActionButton iti_btn;
    private FloatingActionButton my_loc_btn;
    private FloatingActionButton nearest_btn;
    private ProgressBar loadingSpinner;

    public Colloc selected_colloc;
    public boolean isMyLocationEnabled = false;
    public Marker marker;
    public List<Marker> collocsMarkers = new ArrayList<>();

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("MapDebug", String.format("%f, %f", location.getLatitude(), location.getLongitude()));
                drawMarker(location);
                locationManager.removeUpdates(locationListener);
            } else {
                Log.d("MapDebug", "Location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private LocationManager locationManager;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Vous avez besoin d'autoriser l'application à accèder à votre position pour l'afficher sur la carte.\nDésactivation de l'affichage sur la carte ...", Toast.LENGTH_SHORT).show();
                    isMyLocationEnabled = false;
                    nearest_btn.setEnabled(false);
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // On récupère la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        // On récupère le bouton d'itinéraire
        iti_btn = findViewById(R.id.goto_fab);
        iti_btn.setEnabled(false);
        iti_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enc_addr = null;
                try {
                    enc_addr = URLEncoder.encode(selected_colloc.adresse, StandardCharsets.UTF_8.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + enc_addr + "&travelmode=walking");
                Intent gmmIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //gmmIntent.setPackage("com.google.android.apps.maps");

                startActivity(gmmIntent);
            }
        });

        my_loc_btn = findViewById(R.id.mylocation_fab);
        nearest_btn = findViewById(R.id.nearest_fab);
        nearest_btn.setEnabled(false);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        loadingSpinner = findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng pos_ensta_b = Utils.getLocationFromAddress(this, "ENSTA Bretagne Brest");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos_ensta_b, 13f));

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(this);

        my_loc_btn.setOnClickListener(view -> {
            isMyLocationEnabled = !isMyLocationEnabled;
            if (isMyLocationEnabled) {
                my_loc_btn.setImageDrawable(getDrawable(R.drawable.ic_my_location));
                getCurrentLocation();
            } else {
                my_loc_btn.setImageDrawable(getDrawable(R.drawable.ic_no_location));
            }
        });

        nearest_btn.setOnClickListener(view -> {
            if (isMyLocationEnabled && marker != null) {
                // haversine formula
                double R = 6371; // radius of earth in km
                double[] distances = new double[collocsMarkers.size()];
                int closest = -1;
                double u_lat = marker.getPosition().latitude;
                double u_long = marker.getPosition().longitude;

                for (int i = 0; i < collocsMarkers.size(); i++) {
                    double m_lat = collocsMarkers.get(i).getPosition().latitude;
                    double m_long = collocsMarkers.get(i).getPosition().longitude;
                    double d_lat = Math.toRadians(m_lat - u_lat);
                    double d_long = Math.toRadians(m_long - u_long);
                    double a = Math.sin(d_lat/2) * Math.sin(d_lat/2) +
                            Math.cos(Math.toRadians(u_lat)) * Math.cos(Math.toRadians(u_lat)) * Math.sin(d_long) * Math.sin(d_long);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                    double d = R * c;
                    distances[i] = d;

                    if (closest == -1 || d < distances[closest])
                        closest = i;
                }

                Marker closestMarker = collocsMarkers.get(closest);
                Toast.makeText(this, "La colloc la plus proche de vous est : '" + closestMarker.getTitle() + "'.", Toast.LENGTH_SHORT).show();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(closestMarker.getPosition(), 17f));
                closestMarker.showInfoWindow();
                selectCollocMarker(closestMarker);
            }
        });

        getCollocsPositions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMyLocationEnabled)
            getCurrentLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private void getCurrentLocation() {
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("MapDebug","Trying to get location ...");

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled))
            Toast.makeText(this, "Vous n'avez pas activé la localisation.", Toast.LENGTH_SHORT).show();
        else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // On a la permission de le faire
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

                if (location != null) {
                    Log.d("MapDebug", String.format("getCurrentLocation(%f, %f)", location.getLatitude(),
                            location.getLongitude()));
                    drawMarker(location);
                }
                else {
                    Log.d("MapDebug", "Location not found :(");
                    //nearest_btn.setEnabled(false);
                }
            } else {
                Log.d("MapDebug", "L'application n'as pas les droits nécessaire pour afficher votre position...\nVeuillez accepter puis recommencer.");
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                return;
            }
        }
    }

    private void getCollocsPositions() {
        for (Colloc c : CacheManager.getInstance().collocList) {
            addCollocOnMap(c);
        }
        loadingSpinner.setVisibility(View.GONE);
    }

    public void addCollocOnMap(Colloc col) {
        Log.d("MapDebug", "Adding the colloc : " + col.name);

        LatLng position = Utils.getLocationFromAddress(getApplicationContext(), col.adresse);
        Log.d("MapDebug", "Adresse : " + col.adresse + " LatLong : " + position.toString());
        collocsMarkers.add(mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(col.name)
                .snippet(col.description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        ));
    }

    private void drawMarker(Location location) {
        nearest_btn.setEnabled(true);

        if (mMap != null) {
            if (marker != null)
                marker.remove();

            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            marker = mMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Position actuelle")
                    .flat(true));
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(this.marker))
            return false;

        selectCollocMarker(marker);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private void selectCollocMarker(Marker marker) {
        selected_colloc = null;
        List<Colloc> collocs = CacheManager.getInstance().collocList;
        for (int i = 0; i < collocs.size(); i++) {
            if (collocs.get(i).name.equals(marker.getTitle())) {
                selected_colloc = collocs.get(i);
                break;
            }
        }

        Log.d("MapDebug", "Selected colloc : " + selected_colloc.name);
        if (selected_colloc != null) {
            iti_btn.setEnabled(true);
        } else {
            iti_btn.setEnabled(false);
        }
    }
}