package com.google.adcb.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.adcb.demo.BuildConfig;
import com.google.adcb.demo.R;
import com.addemo.cbx_admob_ad.ad_manager.ADCBAdLoader;
import com.addemo.cbx_admob_ad.ad_manager.ADCBMyApplication;
import com.google.adcb.demo.databinding.ActivityMainBinding;

public class ActivityMain extends AppCompatActivity {
    ActivityResultLauncher<Intent> intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            //TODO get result from activity
            Intent data = result.getData();
            if (data != null && data.hasExtra(Intent.EXTRA_TEXT)) {
                Toast.makeText(ActivityMain.this, data.getStringExtra(Intent.EXTRA_TEXT), Toast.LENGTH_SHORT).show();
            }
        }
    });
    private ActivityMainBinding binding;

    public static Intent newIntent(Context context) {
        return new Intent(context, ActivityMain.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ADCBAdLoader.log("ActivityMain -> OnCreate()");
        onInit();

        binding.btnShareApp.setOnClickListener(v -> {
            //TODO share app
            ADCBAdLoader.shareApp(ActivityMain.this, getString(R.string.app_name), BuildConfig.APPLICATION_ID);
        });

        binding.btnRateUs.setOnClickListener(v -> {
            //TODO rate us
            ADCBAdLoader.rateUs(ActivityMain.this);
        });

        binding.btnPrivacyPolicy.setOnClickListener(v -> {
            //TODO open privacy policy
            ADCBAdLoader.openUrl(ActivityMain.this, ADCBMyApplication.getAdModel().getPrivacyPolicy());
        });

        binding.btnStart.setOnClickListener(v -> {
            //TODO start new activity with ad
            ADCBAdLoader.startActivityWithAd(ActivityMain.this, ActivityDummy.newIntent(ActivityMain.this));
        });


        binding.btnStartIntentLauncher.setOnClickListener(v -> {
            //TODO start new activity with result ad
            ADCBAdLoader.startActivityWithAd(ActivityMain.this, intentLauncher, ActivityNativeList.newIntent(ActivityMain.this));
        });

        binding.btnViewLog.setOnClickListener(v -> startActivity(ActivityViewLog.newIntent(ActivityMain.this)));
    }

    private void onInit() {
        //TODO Handle OnBackPress
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ADCBAdLoader.showExit(ActivityMain.this);
            }
        });

        //TODO show native large ad
        ADCBAdLoader.getInstance().showNativeLarge(this, binding.ltNativeLarge);

        //TODO show native small ad
        ADCBAdLoader.getInstance().showNativeSmall(this, binding.ltNativeSmall);

        //TODO show banner ad
        ADCBAdLoader.getInstance().showBanner(this, binding.ltBanner);
    }

    @Override
    protected void onDestroy() {
        ADCBAdLoader.log("ActivityMain -> OnDestroy()");
        super.onDestroy();
    }
}