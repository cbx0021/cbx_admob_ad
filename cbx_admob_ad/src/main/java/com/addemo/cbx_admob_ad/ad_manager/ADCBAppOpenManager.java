package com.addemo.cbx_admob_ad.ad_manager;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class ADCBAppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    public static boolean appIsShowingAd = false;
    public AppOpenAd appOpenAd = null;
    public AppOpenAd.AppOpenAdLoadCallback appCallback;
    public Activity appCurrentActivity;
    public long appLoadTime = 0;

    public ADCBAppOpenManager() {
        ADCBMyApplication.getInstance().registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void fetchAd() {

        if (isAdAvailable()) {
            return;
        }
        if (ADCBAdLoader.getFailedCountAppOpen() < ADCBMyApplication.getAdModel().getAdsAppOpenFailedCount() && ADCBMyApplication.getAdModel().getAdsAppOpen().equalsIgnoreCase("Yes")) {
            appCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull AppOpenAd ad) {
                    ADCBAdLoader.log("APPOPEN -> AD LOADED");
                    ADCBAppOpenManager.this.appOpenAd = ad;
                    ADCBAppOpenManager.this.appLoadTime = (new Date()).getTime();
                    ADCBAdLoader.resetFailedCountAppOpen();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    ADCBAdLoader.increaseFailedCountAppOpen();
                    ADCBAdLoader.log("APPOPEN -> AD FAILED (" + ADCBAdLoader.getFailedCountAppOpen() + " of " + ADCBMyApplication.getAdModel().getAdsAppOpenFailedCount() + ")");
                }
            };
            AdRequest request = getAdRequest();
            ADCBAdLoader.log("APPOPEN -> AD REQUEST");
            AppOpenAd.load(ADCBMyApplication.getInstance(), ADCBMyApplication.getAdModel().getAdsAppOpenId(), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, appCallback);
        } else {
            ADCBAdLoader.log("APPOPEN -> FAILED COUNTER IS " + ADCBMyApplication.getAdModel().getAdsAppOpenFailedCount());
        }
    }


    public AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable() {
        if (!ADCBAdLoader.getInstance().isInterstitialShowing && !appIsShowingAd && isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    ADCBAppOpenManager.this.appOpenAd = null;
                    appIsShowingAd = false;
                    fetchAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    appIsShowingAd = true;
                }
            };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            ADCBAdLoader.log("APPOPEN -> AD SHOW");
            appOpenAd.show(appCurrentActivity);
        } else {
            fetchAd();
        }
    }

    public void showAdIfSplashAvailable(@NonNull final Activity activity, @NonNull ADCBMyApplication.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (!appIsShowingAd && isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    ADCBAppOpenManager.this.appOpenAd = null;
                    appIsShowingAd = false;
                    fetchAd();
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    appIsShowingAd = true;
                }
            };
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            ADCBAdLoader.log("APPOPEN -> AD SHOW");
            appOpenAd.show(activity);
        } else {
            if (ADCBAdLoader.getFailedCountAppOpen() < ADCBMyApplication.getAdModel().getAdsAppOpenFailedCount() && ADCBMyApplication.getAdModel().getAdsAppOpen().equalsIgnoreCase("Yes")) {
                appCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        ADCBAdLoader.log("APPOPEN -> AD LOADED");
                        ADCBAdLoader.resetFailedCountAppOpen();
                        ADCBAppOpenManager.this.appOpenAd = ad;
                        ADCBAppOpenManager.this.appLoadTime = (new Date()).getTime();
                        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                ADCBAppOpenManager.this.appOpenAd = null;
                                appIsShowingAd = false;
                                fetchAd();
                                onShowAdCompleteListener.onShowAdComplete();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                onShowAdCompleteListener.onShowAdComplete();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                appIsShowingAd = true;
                            }
                        };
                        appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                        ADCBAdLoader.log("APPOPEN -> AD SHOW");
                        appOpenAd.show(appCurrentActivity);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        ADCBAdLoader.increaseFailedCountAppOpen();
                        ADCBAdLoader.log("APPOPEN -> AD FAILED (" + ADCBAdLoader.getFailedCountAppOpen() + " of " + ADCBMyApplication.getAdModel().getAdsAppOpenFailedCount() + ")");
                        onShowAdCompleteListener.onShowAdComplete();
                    }
                };
                AdRequest request = getAdRequest();
                ADCBAdLoader.log("APPOPEN -> AD REQUEST");
                AppOpenAd.load(ADCBMyApplication.getInstance(), ADCBMyApplication.getAdModel().getAdsAppOpenId(), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, appCallback);
            } else {
                onShowAdCompleteListener.onShowAdComplete();
                ADCBAdLoader.log("APPOPEN -> FAILED COUNTER IS " + ADCBMyApplication.getAdModel().getAdsAppOpenFailedCount());
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        appCurrentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        appCurrentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        appCurrentActivity = null;
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        if (!(appCurrentActivity instanceof ADCBActivitySplash)) {
            showAdIfAvailable();
        }
    }

    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.appLoadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }
}