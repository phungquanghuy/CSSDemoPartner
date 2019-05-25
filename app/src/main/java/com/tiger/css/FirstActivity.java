package com.tiger.css;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tiger.css.object.Partner;
import com.tiger.css.object.Patron;

public class FirstActivity extends AppCompatActivity {

    private ImageView avatar;
    private Partner mPartner = new Partner();
    private Button active;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        avatar = (ImageView) findViewById(R.id.avatar);
        active = (Button) findViewById(R.id.active);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Partner");

        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBtn();
            }
        });

        statusChange();
    }

    protected void toggleBtn(){
        if(mPartner.getStatus().equals("offline")){
            mPartner.setStatus("actived");
            mDatabaseReference.child(mPartner.getUsername()).child("status").setValue("actived");
        }
        else{
            mPartner.setStatus("offline");
            mDatabaseReference.child(mPartner.getUsername()).child("status").setValue("offline");
        }
    }

    private void statusBtn(){
        if(mPartner.getStatus().equals("offline")){
            active.setBackgroundResource(R.drawable.active_btn);
            active.setTextColor(0xFFFFFFFF);
            active.setText("Active");
        }
        else{
            active.setBackgroundResource(R.drawable.offline_btn);
            active.setTextColor(0xFF000000);
            active.setText("Offline");
        }
    }

    private void statusChange(){
        mDatabaseReference.child(mPartner.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPartner = dataSnapshot.getValue(Partner.class);
                statusBtn();
                Picasso.get().load(mPartner.getUrl()).into(avatar);
                if(!mPartner.getStatus().equals("offline")
                    && !mPartner.getStatus().equals("actived")
                    && !mPartner.getStatus().equals("busy")
                ){
                    Intent intent = new Intent(FirstActivity.this,TwoActivity.class);
                    FirstActivity.this.startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
