package com.tiger.css;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tiger.css.object.Client;
import com.tiger.css.object.Partner;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.provider.Telephony.TextBasedSmsColumns.ADDRESS;

public class FirstActivity extends AppCompatActivity implements LocationListener {

    private ImageView avatar;
    private Partner mPartner = new Partner();
    private Client mClient = new Client();
    private Button active;
    private Boolean check = true;

    //Khai báo cho Map
    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private int REQUEST_GPS = 100;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference partnerDb;
    private DatabaseReference clientDb = FirebaseDatabase.getInstance().getReference("Client");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        // Tạo Progress Bar
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Bản đồ đang tải ...");
        myProgress.setMessage("Vui lòng chờ...");
        myProgress.setCancelable(true);

        // Hiển thị Progress Bar
//        myProgress.show();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.firstMap);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locateGPS();


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (checkPermission()){
                    onMyMapReady(googleMap);
                }
            }
        });


        avatar = (ImageView) findViewById(R.id.avatar);
        active = (Button) findViewById(R.id.active);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        partnerDb = mFirebaseDatabase.getReference("Partner");

        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBtn();
            }
        });
        statusChange();
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

    private void onMyMapReady(GoogleMap googleMap) {
        // Lấy đối tượng Google Map ra:
        myMap = googleMap;

        // Thiết lập sự kiện đã tải Map thành công
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Đã tải thành công thì tắt Dialog Progress đi
                myProgress.dismiss();
                //Marker
//                LatLng TTTH_KHTN = new LatLng(21.035478, 105.787772);
//                MarkerOptions option=new MarkerOptions();
//                option.position(TTTH_KHTN);
//                option.title("Trung tâm tin học ĐH KHTN").snippet("This is cool");
//                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                option.alpha(0.8f);
//                Marker maker = myMap.addMarker(option);
////                maker.showInfoWindow();
//                LatLng latLng = new LatLng(21.038126,105.783345);
//                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

//                myMap.moveCamera(CameraUpdateFactory.zoomBy(16));

            }
        });
        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        LatLng latLng = new LatLng(locateGPS().getLatitude(),locateGPS().getLongitude());
        if (checkPermission()){
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        }
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(false);
        if (checkPermission()){
            myMap.setMyLocationEnabled(true);
        }
    }

    private String locationName(LatLng latLng) throws IOException {
        Geocoder geocode = new Geocoder(FirstActivity.this, Locale.getDefault());
        List<Address> listAddress = geocode.getFromLocation(latLng.latitude, latLng.longitude, 100);
        Address address = listAddress.get(0);

        return address.getAddressLine(0);
    }

    protected void toggleBtn(){
        if(mPartner.getStatus().equals("offline")){
            partnerDb.child(mPartner.getUsername()).child("status").setValue("actived");
        }
        else{
            partnerDb.child(mPartner.getUsername()).child("status").setValue("offline");
        }
    }

    private void statusBtn(){
        if(mPartner.getStatus().equals("offline")){
            active.setBackgroundResource(R.drawable.active_btn);
            active.setTextColor(0xFFFFFFFF);
            active.setText("Bật");
        }
        else{
            active.setBackgroundResource(R.drawable.offline_btn);
            active.setTextColor(0xFF000000);
            active.setText("Tắt");
        }
    }

    private void statusChange(){
        partnerDb.child(mPartner.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPartner = dataSnapshot.getValue(Partner.class);
                statusBtn();
                Picasso.get().load(mPartner.getUrl()).into(avatar);

                if(mPartner.getStatus().equals("busy") && check){
                    check = false;
                    Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                    FirstActivity.this.startActivity(intent);
                    finish();
                }
                if(mPartner.getStatus().equals("paired") && check){
                    check = false;
                    Intent intent = new Intent(FirstActivity.this, ThirdActivity.class);
                    FirstActivity.this.startActivity(intent);
                    finish();
                }
                clientDb.child(mClient.getUsername()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mClient = dataSnapshot.getValue(Client.class);
                        if(mClient.getStatus().contains("waiting")
                                && mPartner.getStatus().equals("actived")
                                && !mClient.getStatus().contains(mPartner.getUsername())){
                            clientDb.child(mClient.getUsername()).child("status").setValue("busy");
                            partnerDb.child(mPartner.getUsername()).child("status").setValue("busy");
                            partnerDb.child(mPartner.getUsername()).child("clientUsn").setValue(mClient.getUsername());
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
        if (check){
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
