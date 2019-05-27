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
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.squareup.picasso.Picasso;
import com.tiger.css.object.Client;
import com.tiger.css.object.Partner;
import com.tiger.css.util.GetDirectionsTask;

import java.util.ArrayList;

public class ThirdActivity extends AppCompatActivity implements LocationListener {

    private Button call, finish;
    private Client mClient = new Client();
    private Partner mPartner = new Partner();
    private ImageView partnerAvt, clientAvt;
    private TextView clientName, address, price, title;
    private Boolean check = true;

    //Khai báo cho Map
    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private int REQUEST_GPS = 100;
    private ArrayList<LatLng> listStep;
    private PolylineOptions polyline;
    private MarkerOptions option1;

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

        listStep = new ArrayList<LatLng>();
        polyline = new PolylineOptions();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (checkPermission()){
                    onMyMapReady(googleMap);
//                    final AsyncTask<Void, Void, Void> task  = new AsyncTask<Void, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Void... params) {
//
//                            // 227 Nguyễn Văn Cừ : 10.762643, 106.682079
//                            // Phố đi bộ Nguyễn Huệ : 10.774467, 106.703274
//
//                            String request = makeURL("10.762643","106.682079","10.774467","106.703274");
//                            GetDirectionsTask task = new GetDirectionsTask(request);
//                            ArrayList<LatLng> list = task.testDirection();
//                            for (LatLng latLng : list) {
//                                listStep.add(latLng);
//                            }
//                            return null;
//                        }
//                        @Override
//                        protected void onPostExecute(Void result) {
//                            // TODO Auto-generated method stub
//                            super.onPostExecute(result);
//                            polyline.addAll(listStep);
//                            Polyline line = myMap.addPolyline(polyline);
//                            line.setColor(Color.BLUE);
//                            line.setWidth(5);
//                        }
//                    };
//
//                    task.execute();
                }
            }
        });
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        option1 = new MarkerOptions();
        option1.position(latLng);
        option1.alpha(0.8f);

        getInfo();
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerDb.child(mPartner.getUsername()).child("status").setValue("actived");
            }
        });
    }

    public String makeURL (String sourcelat, String sourcelng, String destlat, String destlng ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(sourcelat);
        urlString.append(",");
        urlString.append(sourcelng);
        urlString.append("&destination=");// to
        urlString.append(destlat);
        urlString.append(",");
        urlString.append(destlng);
        urlString.append("&key="+getResources().getString(R.string.api_map_key));
        return urlString.toString();
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

                Marker maker1 = myMap.addMarker(option1);
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
                @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {

                        // 227 Nguyễn Văn Cừ : 10.762643, 106.682079
                        // Phố đi bộ Nguyễn Huệ : 10.774467, 106.703274

                        String request = makeURL("10.762643","106.682079","10.774467","106.703274");
                        GetDirectionsTask task = new GetDirectionsTask(request);
                        ArrayList<LatLng> list = task.testDirection();
                        for (LatLng latLng : list) {
                            listStep.add(latLng);
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result) {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);
                        polyline.addAll(listStep);
                        Polyline line = myMap.addPolyline(polyline);
                        line.setColor(Color.BLUE);
                        line.setWidth(5);
                    }
                };
                task.execute();

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
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mClient.getPhone(), null));
                                startActivity(intent);
                            }
                        });
                        LatLng latLng2 = new LatLng(Double.valueOf(mClient.getLat()),Double.valueOf(mClient.getLng()));
                        MarkerOptions option=new MarkerOptions();
                        option.position(latLng2);
                        option.alpha(0.8f);
                        Marker maker2 = myMap.addMarker(option);
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
