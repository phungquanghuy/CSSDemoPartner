package com.tiger.css;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

public class FirstActivity extends AppCompatActivity {

    private ImageView avatar;
    private Partner mPartner = new Partner();
    private Client mClient = new Client();
    private Button active;

    //Khai báo cho Map
    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private SupportMapFragment mapFragment;

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
        myProgress.show();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.firstMap);

        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
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

    private void onMyMapReady(GoogleMap googleMap) {
        // Lấy đối tượng Google Map ra:
        myMap = googleMap;

        // Thiết lập sự kiện đã tải Map thành công
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Đã tải thành công thì tắt Dialog Progress đi
                myProgress.dismiss();

                // Hiển thị vị trí người dùng.
//                askPermissionsAndShowMyLocation();
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myMap.setMyLocationEnabled(true);
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
                if(mPartner.getStatus().equals("busy")){
                    Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                    FirstActivity.this.startActivity(intent);
                    finish();
                }
                if(mPartner.getStatus().equals("paired")){
                    Intent intent = new Intent(FirstActivity.this, ThirdActivity.class);
                    FirstActivity.this.startActivity(intent);
                    finish();
                }
                if(mPartner.getStatus().equals("actived")){
                    clientDb.child(mClient.getUsername()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mClient = dataSnapshot.getValue(Client.class);
                            if(mClient.getStatus().contains("waiting") && !mClient.getStatus().contains(mPartner.getUsername())){
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
