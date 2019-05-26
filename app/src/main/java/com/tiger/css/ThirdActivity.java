package com.tiger.css;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tiger.css.object.Client;
import com.tiger.css.object.Partner;

public class ThirdActivity extends AppCompatActivity {

    private Button call, finish;
    private Client mClient = new Client();
    private Partner mPartner = new Partner();
    private ImageView partnerAvt, clientAvt;
    private TextView clientName, address, price;
    private Boolean check = true;

    //Khai báo cho Map
    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private SupportMapFragment mapFragment;

    private DatabaseReference clientDb = FirebaseDatabase.getInstance().getReference("Client");
    private DatabaseReference partnerDb = FirebaseDatabase.getInstance().getReference("Partner");

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

        // Tạo Progress Bar
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Bản đồ đang tải ...");
        myProgress.setMessage("Vui lòng chờ...");
        myProgress.setCancelable(true);

        // Hiển thị Progress Bar
        myProgress.show();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.thirdMap);

        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });

        getInfo();
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerDb.child(mPartner.getUsername()).child("status").setValue("actived");
            }
        });
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
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mClient.getPhone(), null));
                                startActivity(intent);
                            }
                        });
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
}
