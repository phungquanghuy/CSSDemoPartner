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

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
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
import com.tiger.css.util.GetDirectionsTask;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

        mapFragment.getMapAsync(this);
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

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//
//        // Lấy đối tượng Google Map ra:
//        myMap = googleMap;
//        setupGoogleMapScreenSettings(myMap);
//        // Thiết lập sự kiện đã tải Map thành công
//
//        DirectionsResult results = getDirectionsDetails("483 George St, Sydney NSW 2000, Australia","182 Church St, Parramatta NSW 2150, Australia",TravelMode.DRIVING);
//        if (results != null) {
//            addPolyline(results, googleMap);
//            positionCamera(results.routes[overview], googleMap);
//            addMarkersToMap(results, googleMap);
//        }
//
//        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//                // Đã tải thành công thì tắt Dialog Progress đi
//                myProgress.dismiss();
//
////                Marker maker1 = myMap.addMarker(option1);
//                //Marker
////                LatLng TTTH_KHTN = new LatLng(21.035478, 105.787772);
////                MarkerOptions option=new MarkerOptions();
////                option.position(TTTH_KHTN);
////                option.title("Trung tâm tin học ĐH KHTN").snippet("This is cool");
////                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
////                option.alpha(0.8f);
////                Marker maker = myMap.addMarker(option);
//////                maker.showInfoWindow();
////                LatLng latLng = new LatLng(21.038126,105.783345);
////                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
//
////                myMap.moveCamera(CameraUpdateFactory.zoomBy(16));
////                @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
////
////                    @Override
////                    protected Void doInBackground(Void... params) {
////
////                        // 227 Nguyễn Văn Cừ : 10.762643, 106.682079
////                        // Phố đi bộ Nguyễn Huệ : 10.774467, 106.703274
////
////                        String request = makeURL("10.762643","106.682079","10.774467","106.703274");
////                        GetDirectionsTask task = new GetDirectionsTask(request);
////                        ArrayList<LatLng> list = task.testDirection();
////                        for (LatLng latLng : list) {
////                            listStep.add(latLng);
////                        }
////                        return null;
////                    }
////                    @Override
////                    protected void onPostExecute(Void result) {
////                        // TODO Auto-generated method stub
////                        super.onPostExecute(result);
////                        polyline.addAll(listStep);
////                        Polyline line = myMap.addPolyline(polyline);
////                        line.setColor(Color.BLUE);
////                        line.setWidth(5);
////                    }
////                };
////                task.execute();
//
//            }
//        });
//        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
//        LatLng latLng = new LatLng(locateGPS().getLatitude(),locateGPS().getLongitude());
////        if (checkPermission()){
////            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
////        }
//    }

    private DirectionsResult getDirectionsDetails(String origin,String destination,TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        setupGoogleMapScreenSettings(myMap);

        List<LatLng> waypoints = Arrays.asList(
                new LatLng(41.8766061, -87.6556908),
                new LatLng(41.8909056, -87.6467561)
        );
        GoogleDirection.withServerKey("AIzaSyB7C0IAuO-rd-wNSkiC9M7-fLg-sjwwBBk")
                .from(new LatLng(41.8838111, -87.6657851))
                .and(waypoints)
                .to(new LatLng(41.9007082, -87.6488802))
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()) {
                            // Do something
                        } else {
                            // Do something
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });
        if (checkPermission()){
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.8766061, -87.6556908),16));
        }
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                DirectionsResult results = getDirectionsDetails(
//                        "Disneyland",
//                        "Universal+Studios+Hollywood",
//                        TravelMode.DRIVING);
//                if (results != null) {
//                    addPolyline(results, myMap);
//                    positionCamera(results.routes[overview], myMap);
//                    addMarkersToMap(results, myMap);
//                }
            }
        });

    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat,results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat,results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 12));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getString(R.string.api_map_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
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
        mUiSettings.setZoomControlsEnabled(true);
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
