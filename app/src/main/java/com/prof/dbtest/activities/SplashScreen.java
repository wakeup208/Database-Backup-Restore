package com.prof.dbtest.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.prof.dbtest.R;

public class SplashScreen extends AppCompatActivity {
    Context mContxt;
    Typeface sf_bold;
    Typeface sf_pro_light;
    Typeface sf_regular;
    TextView tv_iphone;
    TextView tv_iphone_ringtune;
    TextView tv_loading;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.splash_screen);
        this.mContxt = this;
        Window window = getWindow();
        window.setFlags(512, 512);
        window.setFlags(1024, 1024);
        this.tv_iphone = (TextView) findViewById(R.id.tv_iphone);
        this.tv_iphone_ringtune = (TextView) findViewById(R.id.tv_iphone_ringtune);
        this.tv_loading = (TextView) findViewById(R.id.tv_loading);
        this.sf_pro_light = Typeface.createFromAsset(getAssets(), "sf_pro_light.otf");
        this.sf_bold = Typeface.createFromAsset(getAssets(), "sf_bold.otf");
        this.sf_regular = Typeface.createFromAsset(getAssets(), "sf_regular.otf");
        this.tv_iphone.setTypeface(this.sf_pro_light);
        this.tv_iphone_ringtune.setTypeface(this.sf_regular);
        this.tv_loading.setTypeface(this.sf_bold);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                SplashScreen.this.startActivity(new Intent(SplashScreen.this, MainActivity.class));
                SplashScreen.this.finish();
            }
        }, 2000);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
