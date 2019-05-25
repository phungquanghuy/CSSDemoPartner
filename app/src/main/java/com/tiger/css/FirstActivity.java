package com.tiger.css;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tiger.css.object.SupportUser;

public class FirstActivity extends AppCompatActivity {

    private ImageView avatar;
    private SupportUser mSupportUser = new SupportUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        avatar = (ImageView) findViewById(R.id.avatar);
        Picasso.get().load(mSupportUser.getUrl()).into(avatar);


    }
}
