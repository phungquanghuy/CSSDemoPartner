package com.tiger.css;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tiger.css.object.Patron;
import com.tiger.css.object.Partner;

public class TwoActivity extends AppCompatActivity {

    private Patron mPatron = new Patron();
    private Partner mPartner = new Partner();
    private ImageView patronAvt,partnerAvt;
    private TextView patronInfo, title, request;
    private Button acceptBtn, cancelBtn;

    private DatabaseReference partnerDb, patronDb;
    private FirebaseDatabase partnerFb, patronFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        patronAvt = (ImageView) findViewById(R.id.patronAvt);
        partnerAvt = (ImageView) findViewById(R.id.avatar);
        patronInfo = (TextView) findViewById(R.id.patronInfo);
        title = (TextView) findViewById(R.id.title);
        request = (TextView) findViewById(R.id.request);
        acceptBtn = (Button) findViewById(R.id.acceptBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        partnerFb = FirebaseDatabase.getInstance();
        partnerDb = partnerFb.getReference("Partner");
        patronFb = FirebaseDatabase.getInstance();
        patronDb = patronFb.getReference("Patron");

        getInfo();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerDb.child(mPartner.getUsername()).child("status").setValue("actived");
            }
        });
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerDb.child(mPartner.getUsername()).child("status").setValue("busy");
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
                        || mPartner.getStatus().equals("busy")
                ){
                    Intent intent = new Intent(TwoActivity.this,FirstActivity.class);
                    TwoActivity.this.startActivity(intent);
                    finish();
                }
                else {
                    patronDb.child(mPartner.getStatus()).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mPatron = dataSnapshot.getValue(Patron.class);
                            Picasso.get().load(mPatron.getUrl()).into(patronAvt);
                            patronInfo.setText(mPatron.getName()+"\n"
                                    +mPatron.getInfo());
                            title.setText("Mã đơn hàng: CSS-"+mPatron.getUsername());
                            request.setText("Yêu cầu hỗ trợ: "+mPatron.getRequest());
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
