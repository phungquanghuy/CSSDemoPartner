package com.tiger.css;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.tiger.css.object.Client;
import com.tiger.css.object.Partner;
import com.tiger.css.util.ProgressBarAnimation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity {

    private Client mClient = new Client();
    private Partner mPartner = new Partner();
    private ImageView clientAvt,partnerAvt;
    private TextView clientInfo, title, request, address, countdown, price, distance;
    private Button acceptBtn, cancelBtn;
    private long countSeconds = 21000;
    private CountDownTimer mCountDownTimer;
    private ProgressBar countdownPro;
    private Boolean check = true;

    private DatabaseReference partnerDb, clientDb;
    private FirebaseDatabase partnerFb, clientFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        clientAvt = findViewById(R.id.clientAvt);
        partnerAvt =  findViewById(R.id.avatar);
        clientInfo =  findViewById(R.id.clientInfo);
        title =  findViewById(R.id.title);
        request =  findViewById(R.id.request);
        address = findViewById(R.id.address);
        countdown = findViewById(R.id.countdown);
        acceptBtn =  findViewById(R.id.acceptBtn);
        cancelBtn =  findViewById(R.id.cancelBtn);
        countdownPro =  findViewById(R.id.countdownPro);
        price =  findViewById(R.id.price);
        distance =  findViewById(R.id.distance);

        ProgressBarAnimation anim = new ProgressBarAnimation(countdownPro, 100, 0);
        anim.setDuration(21000);
        countdownPro.startAnimation(anim);

        partnerFb = FirebaseDatabase.getInstance();
        partnerDb = partnerFb.getReference("Partner");
        clientFb = FirebaseDatabase.getInstance();
        clientDb = clientFb.getReference("Client");

        getInfo();
        startTimer();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerDb.child(mPartner.getUsername()).child("status").setValue("actived");
                clientDb.child(mPartner.getClientUsn()).child("status").setValue("waiting"+mPartner.getUsername());
            }
        });
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerDb.child(mPartner.getUsername()).child("status").setValue("paired");
            }
        });
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
                        mCountDownTimer.cancel();
                        Intent intent = new Intent(SecondActivity.this,FirstActivity.class);
                        SecondActivity.this.startActivity(intent);
                        finish();
                    }
                }
                if(mPartner.getStatus().equals("paired") && check){
                    check = false;
                    mCountDownTimer.cancel();
                    clientDb.child(mPartner.getClientUsn()).child("status").setValue(mPartner.getUsername());
                    Intent intent = new Intent(SecondActivity.this,ThirdActivity.class);
                    SecondActivity.this.startActivity(intent);
                    finish();
                }
                clientDb.child(mPartner.getClientUsn()).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mClient = dataSnapshot.getValue(Client.class);
                        Picasso.get().load(mClient.getUrl()).into(clientAvt);
                        clientInfo.setText(mClient.getName()+"\n"
                                + mClient.getInfo());
                        title.setText("Mã khách hàng: CSS-"+ mClient.getUsername());
                        address.setText("Địa chỉ: "+mClient.getAddress());
                        request.setText("Yêu cầu hỗ trợ: "+ mClient.getRequest());
                        price.setText(mClient.getPrice());

                        LatLng to = new LatLng(Double.valueOf(mClient.getLat()),Double.valueOf(mClient.getLng()));
                        LatLng from = new LatLng(Double.valueOf(mPartner.getLat()),Double.valueOf(mPartner.getLng()));
                        double dis = Math.round(SphericalUtil.computeDistanceBetween(from, to)/100);
                        distance.setText(dis/10+"km");
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

    protected void startTimer(){
        mCountDownTimer = new CountDownTimer(countSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countSeconds = millisUntilFinished;
                countdown.setText(countSeconds/1000+"");
            }

            @Override
            public void onFinish() {
                countdown.setText(0+"");
                partnerDb.child(mPartner.getUsername()).child("status").setValue("actived");
                clientDb.child(mPartner.getClientUsn()).child("status").setValue("waiting"+mPartner.getUsername());
            }
        }.start();

    }

}
