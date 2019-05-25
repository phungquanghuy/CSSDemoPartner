package com.tiger.css;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tiger.css.object.SupportUser;

public class FirstActivity extends AppCompatActivity {

    private ImageView avatar;
    private SupportUser mSupportUser = new SupportUser();
    Button active;

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

        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBtn();
            }
        });

    }

    protected void toggleBtn(){
        if(!mSupportUser.getStatus().equals("offline")){
            mSupportUser.setStatus("offline");
            active.setBackgroundResource(R.drawable.active_btn);
            active.setTextColor(0xFFFFFFFF);
            active.setText("Active");
        }
        else{
            mSupportUser.setStatus("actived");
            active.setBackgroundResource(R.drawable.offline_btn);
            active.setTextColor(0xFF000000);
            active.setText("Offlne");
        }
    }
}
