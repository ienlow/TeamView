package com.example.isaacenlow.my_application;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayMessageActivity extends AppCompatActivity implements OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    String team = " ";
    String type = "login";
    List<android.location.Address> test;
    Geocoder coder;
    String[] address;

    /**
     * Creates instance of database connector.
     * Creates and inserts information into list.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        team = intent.getStringExtra("team");
        backgroundWorker.execute(type, team);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_display_message);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        String[] place = backgroundWorker.getPlace();
        ArrayList<Team> teams = new ArrayList<Team>();
        address = backgroundWorker.getAddress();
        if (address != null) {
            for (int i = 0; i < 10; i++) {
                if (address[i] != null && place[i] != null) {
                    address[i] = this.getAddressFromLocation(this, backgroundWorker.getAddress()[i]);
                    Team one = new Team(place[i], address[i]);
                    teams.add(one);
                }
            }
        }
        else
        teams.add(new Team("No Results", null));
        ListView listView = (ListView) findViewById(R.id.results);
        TeamAdapter adapter = new TeamAdapter(this, R.layout.adapter_layout, teams);
        listView.setAdapter(adapter);
        listView.setBackgroundResource(R.drawable.sports);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("failed", "connection");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null){
        }
        else {
        }
        Log.i("success", "connection");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        Log.i("Stop", "connection");
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    /**
     * Finds location of address.
     * @param context
     * @param strAddress
     * @return
     */
    public String getAddressFromLocation(Context context, String strAddress) {
        String address_line = "";
        try {
            coder = new Geocoder(this);
            test = coder.getFromLocationName(strAddress, 1);
            android.location.Address add = test.get(0);
            address_line = add.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address_line;
    }
}
