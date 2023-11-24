package com.addemo.cbx_admob_ad.ad_manager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class ADCBActivitySplash extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ADCBAdLoader.log("ActivitySplash -> OnCreate()");
    }
    @Override
    protected void onDestroy() {
        ADCBAdLoader.log("ActivitySplash -> OnDestroy()");
        super.onDestroy();
    }
}