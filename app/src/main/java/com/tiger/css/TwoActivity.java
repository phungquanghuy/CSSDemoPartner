package com.tiger.css;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private ImageView patronAvt;
    private TextView patronInfo;

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
        patronInfo = (TextView) findViewById(R.id.patronInfo);

        partnerFb = FirebaseDatabase.getInstance();
        partnerDb = partnerFb.getReference("Partner");
        patronFb = FirebaseDatabase.getInstance();
        patronDb = patronFb.getReference("Patron");

        getInfo();
    }

    protected void getInfo(){
        partnerDb.child(mPartner.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPartner = dataSnapshot.getValue(Partner.class);

                patronDb.child(mPartner.getStatus()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mPatron = dataSnapshot.getValue(Patron.class);
                        Picasso.get().load(mPatron.getUrl()).into(patronAvt);
                        patronInfo.setText(mPatron.getName()+"\n"
                        +mPatron.getPhone());
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
