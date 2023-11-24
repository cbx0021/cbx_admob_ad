package com.google.adcb.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.addemo.cbx_admob_ad.ad_manager.ADCBAdLoader;
import com.google.adcb.demo.databinding.ActivityDummyBinding;

public class ActivityDummy extends AppCompatActivity {
    private ActivityDummyBinding binding;

    public static Intent newIntent(Context context) {
        return new Intent(context, ActivityDummy.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDummyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ADCBAdLoader.log("ActivityDummy -> OnCreate()");

        onInit();

        binding.toolbar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.btnStartFinish.setOnClickListener(v -> {
            //TODO start new activity with finish ad
            ADCBAdLoader.startActivityWithFinishAd(ActivityDummy.this, ActivityDummy.newIntent(ActivityDummy.this));
        });


        binding.btnStartFinishAffinity.setOnClickListener(v -> {
            //TODO start new activity with finish affinity ad
            ADCBAdLoader.startActivityWithFinishAffinityAd(ActivityDummy.this, ActivityMain.newIntent(ActivityDummy.this));
        });

        binding.btnFinish.setOnClickListener(v -> {
            //TODO finish activity with ad
            ADCBAdLoader.finishWithAd(ActivityDummy.this);
        });

        binding.btnFinishAffinity.setOnClickListener(v -> {
            //TODO finish affinity activity with ad
            ADCBAdLoader.finishAffinityWithAd(ActivityDummy.this);
        });
        binding.btnViewLog.setOnClickListener(v -> startActivity(ActivityViewLog.newIntent(ActivityDummy.this)));
    }

    private void onInit() {
        ADCBAdLoader.getInstance().showBanner(this, binding.ltBanner);

        //TODO Handle OnBackPress
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ADCBAdLoader.finishWithAd(ActivityDummy.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        ADCBAdLoader.log("ActivityDummy -> OnDestroy()");
        super.onDestroy();
    }
}