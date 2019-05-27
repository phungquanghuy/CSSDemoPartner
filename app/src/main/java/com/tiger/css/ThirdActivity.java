package com.tiger.css;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.squareup.picasso.Picasso;
import com.tiger.css.object.Client;
import com.tiger.css.object.Partner;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ThirdActivity extends AppCompatActivity implements LocationListener,
        OnMapReadyCallback{

    private Button call, finish;
    private Client mClient = new Client();
    private Partner mPartner = new Partner();
    private ImageView partnerAvt, clientAvt;
    private TextView clientName, address, price, title;
    private Boolean check = true;
    private static final int overview = 0;
    //Khai báo cho Map
    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private int REQUEST_GPS = 100;
    private MarkerOptions option1;
    private ImageView direction;


    private DatabaseReference clientDb = FirebaseDatabase.getInstance().getReference("Client");
    private DatabaseReference partnerDb = FirebaseDatabase.getInstance().getReference("Partner");

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        call = findViewById(R.id.call);
        finish = findViewById(R.id.finish);
        partnerAvt = findViewById(R.id.avatar);
        clientAvt = findViewById(R.id.clientAvt);
        clientName = findViewById(R.id.clientName);
        address = findViewById(R.id.address);
        price = findViewById(R.id.price);
        title = findViewById(R.id.title);
        direction = findViewById(R.id.direction);


        // Tạo Progress Bar
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Bản đồ đang tải ...");
        myProgress.setMessage("Vui lòng chờ...");
        myProgress.setCancelable(true);

        // Hiển thị Progress Bar
//        myProgress.show();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.thirdMap);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locateGPS();

        mapFragment.getMapAsync(this);

        getInfo();

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerDb.child(mPartner.getUsername()).child("status").setValue("actived");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 100:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    locateGPS();
                }
                return;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        setupGoogleMapScreenSettings(myMap);

        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
            }
        });
        LatLng latLng = new LatLng(locateGPS().getLatitude(),locateGPS().getLongitude());
        if (checkPermission()){
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }

    }

    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_GPS);
            return false;
        }
        return true;
    }

    private Location locateGPS(){
        Location localGpsLocation = null;
        if (checkPermission()){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,this);
            localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(localGpsLocation == null){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,3000,0,this);
                localGpsLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        return localGpsLocation;
    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        if (checkPermission()){
            mMap.setMyLocationEnabled(true);
        }
    }

    protected void getInfo(){
        partnerDb.child(mPartner.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPartner = dataSnapshot.getValue(Partner.class);
                Picasso.get().load(mPartner.getUrl()).into(partnerAvt);

                if(mPartner.getStatus().equals("offline")
                        || mPartner.getStatus().equals("actived")
                ){
                    if(check){
                        check = false;
                        clientDb.child(mPartner.getClientUsn()).child("status").setValue("available");
                        Intent intent = new Intent(ThirdActivity.this,FirstActivity.class);
                        ThirdActivity.this.startActivity(intent);
                        finish();
                    }
                }
                clientDb.child(mPartner.getClientUsn()).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mClient = dataSnapshot.getValue(Client.class);
                        Picasso.get().load(mClient.getUrl()).into(clientAvt);
                        address.setText("Địa chỉ: "+mClient.getAddress());
                        clientName.setText(mClient.getName());
                        price.setText("Giá : "+mClient.getPrice());
                        title.setText("Mã khách hàng: CSS-"+ mClient.getUsername());
                        if (check){
                            call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    check = false;
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mClient.getPhone(), null));
                                    startActivity(intent);
                                }
                            });
                        }
                        LatLng latLng2 = new LatLng(Double.valueOf(mClient.getLat()),Double.valueOf(mClient.getLng()));
                        MarkerOptions option=new MarkerOptions();
                        option.position(latLng2);
                        option.alpha(0.8f);
                        Marker maker2 = myMap.addMarker(option);
                        if (check){
                            direction.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+mClient.getAddress());
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    check = false;
                                    startActivity(mapIntent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        locateGPS();
        if(check){
            partnerDb.child(mPartner.getUsername()).child("lat").setValue(location.getLatitude()+"");
            partnerDb.child(mPartner.getUsername()).child("lng").setValue(location.getLongitude()+"");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
}
