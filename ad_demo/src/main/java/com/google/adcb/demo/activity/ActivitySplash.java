package com.google.adcb.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.addemo.cbx_admob_ad.ad_manager.ADCBActivitySplash;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.addemo.cbx_admob_ad.ad_manager.ADCBMyApplication;
import com.google.adcb.demo.BuildConfig;
import com.addemo.cbx_admob_ad.ad_manager.ADCBAdLoader;
import com.addemo.cbx_admob_ad.ad_manager.ADCBAdModel;
import com.addemo.cbx_admob_ad.ad_manager.ADCBEasyAES;
import com.addemo.cbx_admob_ad.ad_manager.ADCBRetrofitClient;
import com.google.adcb.demo.databinding.ActivitySplashBinding;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class ActivitySplash extends ADCBActivitySplash {
    ActivitySplashBinding binding;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ADCBAdLoader.log("ActivitySplash -> OnCreate()");
        updateFields();
    }

    private void updateFields() {
        try {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("AndroidId", androidId);
            jsonObject.addProperty("VersionCode", BuildConfig.VERSION_CODE);
            jsonObject.addProperty("PkgName", BuildConfig.APPLICATION_ID);
            ADCBRetrofitClient.getInstance().getMyApi().data(ADCBEasyAES.encryptString(jsonObject.toString()))
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            try {
                                if (response.body() != null) {
                                    JSONObject jsonObject = new JSONObject(ADCBEasyAES.decryptString(response.body().string()));
                                    ADCBMyApplication.setAdModel(new Gson().fromJson(jsonObject.toString(), ADCBAdModel.class));
                                    ADCBAdLoader.resetCounter();
                                    if (ADCBMyApplication.getAdModel().getAdsAppUpDate().equalsIgnoreCase("Yes")) {
                                        ADCBAdLoader.showUpdateDialog(ActivitySplash.this);
                                    } else {
                                        gotoSkip();
                                    }
                                } else {
                                    gotoSkip();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                gotoSkip();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            gotoSkip();
                        }
                    });
        } catch (Exception e) {
            gotoSkip();
        }
    }

    void gotoSkip() {
        ADCBAdLoader.getInstance().loadNativeAdPreload(this);
        ADCBAdLoader.getInstance().loadInterstitialAds(this);
        if (ADCBMyApplication.getAdModel().getAdsSplash().equalsIgnoreCase("AppOpen")) {
            ADCBMyApplication.getInstance().showAdIfAvailable(this, this::goNext);
        } else {
            goNext();
        }
    }

    private void goNext() {
        //TODO start new activity with ad and finish previous one
        ADCBAdLoader.startActivityWithFinishAd(this, ActivityMain.newIntent(this));
    }
    @Override
    protected void onDestroy() {
        ADCBAdLoader.log("ActivitySplash -> OnDestroy()");
        super.onDestroy();
    }
}