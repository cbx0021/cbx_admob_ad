package com.addemo.cbx_admob_ad.ad_manager;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;

public class ADCBMyApplication extends Application {

    private static ADCBMyApplication application;
    private static ADCBAdModel adModel = new ADCBAdModel();
    private ADCBAppOpenManager appOpenManager;
    public static CharSequence VIEW_LOG_DATA = "";
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        MobileAds.initialize(this, initializationStatus -> {
        });
        appOpenManager = new ADCBAppOpenManager();
    }

    public static ADCBAdModel getAdModel() {
        if (adModel == null) {
            adModel = new ADCBAdModel();
        }
        return adModel;
    }

    public static void setAdModel(ADCBAdModel adModel) {
        ADCBMyApplication.adModel = adModel;
    }

    public static synchronized ADCBMyApplication getInstance() {
        ADCBMyApplication myApp;
        synchronized (ADCBMyApplication.class) {
            myApp = application;
        }
        return myApp;
    }

    public void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        appOpenManager.showAdIfSplashAvailable(activity, onShowAdCompleteListener);
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    //TODO Load Native Library
    static {
        System.loadLibrary("demo");
    }
}
