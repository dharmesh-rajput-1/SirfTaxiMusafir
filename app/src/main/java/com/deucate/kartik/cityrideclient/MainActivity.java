package com.deucate.kartik.cityrideclient;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener, AutoComplateFragment.OnFragmentInteractionListener {

    private double mUserALat, mUserALon,mDesLat,mDesLon;


    private TextView mYourLocation, mDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mYourLocation = (TextView) findViewById(R.id.mainCurrentLocation);
        mDestination = (TextView) findViewById(R.id.mainDestination);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 9);
            return;
        }
        String provider = LocationManager.GPS_PROVIDER;

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mUserALat = locationManager.getLastKnownLocation(provider).getLatitude();
        mUserALon = locationManager.getLastKnownLocation(provider).getLongitude();

        String addName = getAddress(mUserALat, mUserALon);
        mYourLocation.setHint(addName);

        findViewById(R.id.mainLocationBBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        Log.i("----->", "onPlaceSelected: "+place.getName());


                        mDesLat = place.getLatLng().latitude;
                        mDesLon = place.getLatLng().longitude;

                        mDestination.setText(place.getAddress());

                    }

                    @Override
                    public void onError(Status status) {

                    }
                });

            }
        });


    }

    private String getAddress(double latitude, double longitude) {
        String currentLocationName = "";

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
            currentLocationName = obj.getAddressLine(0);
            currentLocationName = currentLocationName + " " + obj.getAddressLine(1) + "\n" + obj.getAddressLine(2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentLocationName;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int PLACE_AUTO_COMPLATE = 1;
        if (requestCode == PLACE_AUTO_COMPLATE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mUserALat = location.getLatitude();
        mUserALon = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
