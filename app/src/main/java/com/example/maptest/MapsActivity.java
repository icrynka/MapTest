package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.text.TextWatcher;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private OrderMarker orderMarker;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Button bAddPoint;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    double latitude;
    double longitude;
    String currentAddress;



    private Button ibMakeOrder;
    private TextView tvCurrentAddress;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText pnumber;
    private EditText food;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ibMakeOrder = findViewById(R.id.ib_make_order);
        tvCurrentAddress = ((TextView)findViewById(R.id.tv_current_address));
        pnumber = findViewById(R.id.phone_number);
        food = findViewById(R.id.food);

        ibMakeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("orders").push().setValue(new OrderMarker(currentAddress, food.getText().toString(), new LatLng(latitude, longitude)));
                Intent intent = new Intent(MapsActivity.this, MarkerActivity.class);
                intent.putExtra("marker", currentAddress + " - " + food.getText().toString());
                startActivity(intent);
            }
        });


        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    try {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        currentAddress = formatLocation(location);
                        tvCurrentAddress.setText(currentAddress);
                        LatLng pos = new LatLng(latitude, longitude);
                        //mMap.clear();
                        //mMap.addMarker(new MarkerOptions().position(pos).title("test"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(17.0f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tvCurrentAddress.setText(currentAddress);

                }
            };
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //TODO: Consider calling
                //	Activity#requestPermissions
                //here to request the missing permissions, and then overriding
                //public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //	int[] grantResults)
                //to handle the case wherethe user grants the permissions. See the documentation
                //for Activity#requestPermissions for more details.
                return;
            }
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess (Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            try {
                                currentAddress = formatLocation(location);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            tvCurrentAddress.setText(currentAddress);

                        }
                    }
                });


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    OrderMarker orderMarker = postSnapshot.getValue(OrderMarker.class);

                    mMap.addMarker(new MarkerOptions().position(orderMarker.getLatLng()).title(orderMarker.getCurrentAddress() + " - " + orderMarker.getFood()));


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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


        LatLng myPoint = new LatLng(62.0755802, 129.7345511);
        mMap.addMarker(new MarkerOptions().position(myPoint).title("Start"));
        //LatLng sydney2 = new LatLng(62, 129);
        //mMap.addMarker(new MarkerOptions().position(myP).title("Marker in Ykt"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPoint));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17.0f));


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String m = marker.getTitle();
                Intent intent = new Intent(MapsActivity.this,  MarkerActivity.class);
                intent.putExtra("marker", m);
                startActivity(intent);

            }
        });

    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private String formatLocation(Location location) throws IOException {
        if (location == null)
            return "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); //1 num of possible location returned
        String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
        String[] a = address.split(",", 3);

        return a[0] + ", " + a[1];

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startLocationUpdates() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission (android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           // TODO: Consider calling
            //      Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //      public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                              int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();

        startLocationUpdates();

    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        tvCurrentAddress.setText("");
    }


    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
