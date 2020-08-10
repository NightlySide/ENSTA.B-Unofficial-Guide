package io.github.nightlyside.enstaunofficialguide.activities;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;
import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FloatingActionButton iti_btn;
    private List<Colloc> collocs = new ArrayList<Colloc>();
    private Colloc selected_colloc;

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
        iti_btn = (FloatingActionButton) findViewById(R.id.fab);
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

                Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+ enc_addr +"&travelmode=walking");
                Intent gmmIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //gmmIntent.setPackage("com.google.android.apps.maps");

                startActivity(gmmIntent);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String query_string = "collocs.php";
        NetworkManager.getInstance().makeJSONArrayRequest(query_string, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONArray response = new JSONArray(result);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = null;
                    try {
                        // colloc construction
                        obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String name = obj.getString("name");
                        String adresse = obj.getString("adresse");
                        String description = obj.getString("description");

                        Colloc col = new Colloc(id, name, adresse, description);
                        collocs.add(col);

                        // adding it on the map
                        addCollocOnMap(col);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng pos_ensta_b = getLocationFromAddress(this, "ENSTA Bretagne Brest");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos_ensta_b, 12f));

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.setOnMarkerClickListener(this);
    }

    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;
    }

    public void addCollocOnMap(Colloc col) {
        Log.d("MapDebug", "Adding the colloc : " + col.name);

        LatLng position = getLocationFromAddress(this, col.adresse);
        Log.d("MapDebug", "Adresse : " + col.adresse + " LatLong : " + position.toString());
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(col.name)
                .snippet(col.description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        );
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_SHORT);


        selected_colloc = null;
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

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}