package com.example.favouriteplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.favouriteplaces.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.favouriteplaces.databinding.ActivityMapsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.favouriteplaces.MainActivity.arrayAdapter;
import static com.example.favouriteplaces.MainActivity.locations;
import static com.example.favouriteplaces.MainActivity.totalPlaces;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    LocationListener locationListener;
    LocationManager locationManager;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Give us update about user if permission is granted. minTimeMs-> after how much time you need to be updated, minDistanceMs-> After what distance you should be updated
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerMapOnLocation(lastKnownLocation, "Your location!");
                }
            }
        }
    }

    public void centerMapOnLocation(Location location, String title) {
        if (location != null) {
            LatLng currLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 13));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.clear();
        // Long press
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault()); // Locale is for different countries
                try {
                    String address = "";
                    List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (listAddresses != null && listAddresses.size() > 0) {
                        if (listAddresses.get(0).getSubThoroughfare() != null) {
                            address += listAddresses.get(0).getSubThoroughfare() + ", ";
                        }
                        if (listAddresses.get(0).getLocality() != null) {
                            address += listAddresses.get(0).getLocality();
                        }
//                        Log.i("PlaceInfo", address);
                    }
                    if(!address.equals("")){
                        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                        totalPlaces.add(address);
                    }
                    else{
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(currentDateandTime));
                        totalPlaces.add(currentDateandTime);
                    }
                    locations.add(latLng);
                    Toast.makeText(MapsActivity.this, "Location added!!", Toast.LENGTH_SHORT).show();
                    arrayAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        // Get intent here not in onCreate to make sure the map is ready
        Intent intent = getIntent();
        int placeId = intent.getIntExtra("placeId", -1);
        Log.i("Place ID", String.valueOf(placeId));

        if (placeId == 0) {
            // Zoom in on user location
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                // Updates location based on whether device has moved or not
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.i("Location", location.toString());

                    centerMapOnLocation(location, "Your current location!");
                }
            };
            if (Build.VERSION.SDK_INT < 23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    centerMapOnLocation(lastKnownLocation, "Your location!");
                }
            }


        } else {
            // Open location selected
            Location placeLocation=new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(locations.get(placeId).latitude);
            placeLocation.setLongitude(locations.get(placeId).longitude);
            centerMapOnLocation(placeLocation, "Your saved location!");
        }
    }
}