package com.tiger.css;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import com.tiger.css.object.Client;
import com.tiger.css.object.Partner;

public class ThirdActivity extends AppCompatActivity {

    private Button call, finish;
    private Client mClient = new Client();
    private Partner mPartner = new Partner();
    private ImageView partnerAvt, clientAvt;
    private TextView clientName, address, price;
    private Boolean check = true;

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

        getInfo();
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerDb.child(mPartner.getUsername()).child("status").setValue("actived");
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
