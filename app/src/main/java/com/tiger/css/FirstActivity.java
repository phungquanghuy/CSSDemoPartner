package com.tiger.css;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tiger.css.object.Patron;
import com.tiger.css.object.SupportUser;

public class FirstActivity extends AppCompatActivity {

    private ImageView avatar;
    private SupportUser mSupportUser = new SupportUser();
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
        Picasso.get().load(mSupportUser.getUrl()).into(avatar);
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
        if(!mSupportUser.getStatus().equals("offline")){
            mSupportUser.setStatus("offline");
            mDatabaseReference.child(mSupportUser.getUsername()).child("status").setValue("offline");
        }
        else{
            mSupportUser.setStatus("actived");
            mDatabaseReference.child(mSupportUser.getUsername()).child("status").setValue("actived");
        }
    }

    private void statusBtn(){
        if(mSupportUser.getStatus().equals("offline")){
            active.setBackgroundResource(R.drawable.active_btn);
            active.setTextColor(0xFFFFFFFF);
            active.setText("Active");
        }
        else{
            active.setBackgroundResource(R.drawable.offline_btn);
            active.setTextColor(0xFF000000);
            active.setText("Offlne");
        }
    }

    private void statusChange(){
        mDatabaseReference.child(mSupportUser.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSupportUser = dataSnapshot.getValue(SupportUser.class);
                statusBtn();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
