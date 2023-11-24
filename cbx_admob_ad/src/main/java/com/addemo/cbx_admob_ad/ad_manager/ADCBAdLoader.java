package com.addemo.cbx_admob_ad.ad_manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.addemo.cbx_admob_ad.R;
import com.addemo.cbx_admob_ad.databinding.AdDialogExitBinding;
import com.addemo.cbx_admob_ad.databinding.AdDialogExitBottomBinding;
import com.addemo.cbx_admob_ad.databinding.AdDialogProgressAdBinding;
import com.addemo.cbx_admob_ad.databinding.AdDialogUpdateBinding;
import com.addemo.cbx_admob_ad.databinding.AdLayoutBannerBinding;
import com.addemo.cbx_admob_ad.databinding.AdLayoutNativeBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;

public class ADCBAdLoader {

    //TODO SET DEFAULT SETTING OF AD LOADER
    private static final boolean IS_SHIMMER_ENABLED = true;
    private static final boolean HIDE_PLACE_HOLDERS_WHEN_AD_FAILED = false;
    private static final String KEY_INTERSTITIAL_INTERVAL_START_ACTIVITY = "KeyInterstitialStartActivity";
    private static final String KEY_IS_INTERSTITIAL_INTERVAL_START_ACTIVITY_FIRST = "KeyIsInterstitialIntervalStartActivityFirst";
    private static final String KEY_INTERSTITIAL_INTERVAL_BACK_ACTIVITY = "KeyInterstitialBackActivity";
    private static final String KEY_IS_INTERSTITIAL_INTERVAL_BACK_ACTIVITY_FIRST = "KeyIsInterstitialIntervalBackActivityFirst";
    private static final String KEY_FAILED_COUNT_INTERSTITIAL = "KeyFailedCountInterstitial";
    private static final String KEY_FAILED_COUNT_NATIVE = "KeyFailedCountNative";
    private static final String KEY_FAILED_COUNT_APP_OPEN = "KeyFailedCountAppOpen";
    private static final int NATIVE_LIST_SIZE = 3;
    private static ADCBAdLoader instance;
    private final ArrayList<NativeAd> nativeAds = new ArrayList<>();
    public boolean isInterstitialLoading = false;
    public boolean isInterstitialShowing = false;
    private InterstitialAd interstitialAd = null;
    private NativeAd nativeAdPreload = null;

    public static ADCBAdLoader getInstance() {
        if (instance == null) {
            instance = new ADCBAdLoader();
        }
        return instance;
    }

    private static void showProgressDialog(Activity activity,ADCBAdDialogListener listener){
        Dialog progressDialog = new Dialog(activity, R.style.FullWidth_Dialog);
        progressDialog.setContentView(AdDialogProgressAdBinding.inflate(LayoutInflater.from(activity), null, false).getRoot());
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawableResource(R.color.white25);
        }
        if (!activity.isDestroyed() && !activity.isFinishing()) {
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new Handler(activity.getMainLooper()).postDelayed(() -> {
                    listener.onDismiss();
                    progressDialog.dismiss();
                }, 1000);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void resetInterstitialInterval(boolean isBack) {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(isBack ? KEY_INTERSTITIAL_INTERVAL_BACK_ACTIVITY : KEY_INTERSTITIAL_INTERVAL_START_ACTIVITY, 0).apply();
    }

    public static void resetCounter() {
        resetInterstitialInterval(true);
        resetInterstitialInterval(false);
        resetInterstitialIntervalFirst(true);
        resetInterstitialIntervalFirst(false);
        resetFailedCountInterstitial();
        resetFailedCountNative();
        resetFailedCountAppOpen();
    }

    private static void increaseInterstitialInterval(boolean isBack) {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(isBack ? KEY_INTERSTITIAL_INTERVAL_BACK_ACTIVITY : KEY_INTERSTITIAL_INTERVAL_START_ACTIVITY, getInterstitialInterval(isBack) + 1).apply();
    }

    private static int getInterstitialInterval(boolean isBack) {
        SharedPreferences preferences = getPreference();
        return preferences.getInt(isBack ? KEY_INTERSTITIAL_INTERVAL_BACK_ACTIVITY : KEY_INTERSTITIAL_INTERVAL_START_ACTIVITY, 0);
    }

    private static SharedPreferences getPreference() {
        return ADCBMyApplication.getInstance().getSharedPreferences(ADCBMyApplication.getInstance().getPackageName(), Context.MODE_PRIVATE);
    }

    private static boolean isInterstitialIntervalFirst(boolean isBack) {
        SharedPreferences preferences = getPreference();
        return preferences.getBoolean(isBack ? KEY_IS_INTERSTITIAL_INTERVAL_BACK_ACTIVITY_FIRST : KEY_IS_INTERSTITIAL_INTERVAL_START_ACTIVITY_FIRST, true);
    }

    private static void applyInterstitialIntervalFirst(boolean isBack) {
        SharedPreferences preferences = getPreference();
        preferences.edit().putBoolean(isBack ? KEY_IS_INTERSTITIAL_INTERVAL_BACK_ACTIVITY_FIRST : KEY_IS_INTERSTITIAL_INTERVAL_START_ACTIVITY_FIRST, false).apply();
    }

    private static void resetInterstitialIntervalFirst(boolean isBack) {
        SharedPreferences preferences = getPreference();
        preferences.edit().putBoolean(isBack ? KEY_IS_INTERSTITIAL_INTERVAL_BACK_ACTIVITY_FIRST : KEY_IS_INTERSTITIAL_INTERVAL_START_ACTIVITY_FIRST, true).apply();
    }

    private static void resetFailedCountInterstitial() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_INTERSTITIAL, 0).apply();
    }

    private static void resetFailedCountNative() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_NATIVE, 0).apply();
    }

    public static void resetFailedCountAppOpen() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_APP_OPEN, 0).apply();
    }

    private static void increaseFailedCountInterstitial() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_INTERSTITIAL, getFailedCountInterstitial() + 1).apply();
    }

    private static void increaseFailedCountNative() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_NATIVE, getFailedCountNative() + 1).apply();
    }

    public static void increaseFailedCountAppOpen() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_APP_OPEN, getFailedCountAppOpen() + 1).apply();
    }

    private static int getFailedCountInterstitial() {
        SharedPreferences preferences = getPreference();
        return preferences.getInt(KEY_FAILED_COUNT_INTERSTITIAL, 0);
    }

    private static int getFailedCountNative() {
        SharedPreferences preferences = getPreference();
        return preferences.getInt(KEY_FAILED_COUNT_NATIVE, 0);
    }

    public static int getFailedCountAppOpen() {
        SharedPreferences preferences = getPreference();
        return preferences.getInt(KEY_FAILED_COUNT_APP_OPEN, 0);
    }

    public static void showExit(Activity activity) {
        if (ADCBMyApplication.getAdModel().getAdsExit().equalsIgnoreCase("Yes")) {
            showExitBottomDialog(activity);
        } else {
            showExitDialog(activity);
        }
    }

    private static void showExitBottomDialog(Activity activity) {
        try {
            BottomSheetDialog dialog = new BottomSheetDialog(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog);
            AdDialogExitBottomBinding binding = AdDialogExitBottomBinding.inflate(LayoutInflater.from(activity), null, false);
            dialog.setContentView(binding.getRoot());
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            dialog.setOnShowListener(dialog1 -> ADCBAdLoader.getInstance().showNativeLarge(activity, binding.ltNative));
            binding.cvExit.setOnClickListener(v -> {
                dialog.setOnDismissListener(dialog12 -> activity.finishAffinity());
                dialog.dismiss();
            });
            if (ADCBMyApplication.getAdModel().getQurekaLink() == null || ADCBMyApplication.getAdModel().getQurekaLink().trim().equals("")) {
                binding.btnPlayGame.setVisibility(View.GONE);
            } else {
                binding.btnPlayGame.setVisibility(View.VISIBLE);
                Animation anim = new ScaleAnimation(0.9f, 1F, // Start and end values for the X axis scaling
                        0.9F, 1F, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(1000); //You can manage the blinking time with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setFillAfter(true);
                anim.setRepeatCount(Animation.INFINITE);
                binding.btnPlayGame.startAnimation(anim);
                binding.btnPlayGame.setOnClickListener(v -> {
                    dialog.setOnDismissListener(dialog13 -> openUrl(activity, ADCBMyApplication.getAdModel().getQurekaLink()));
                    dialog.dismiss();
                });
            }
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showExitDialog(Activity activity) {
        try {
            Dialog dialog = new Dialog(activity);
            AdDialogExitBinding binding = AdDialogExitBinding.inflate(LayoutInflater.from(activity), null, false);
            dialog.setContentView(binding.getRoot());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            dialog.show();
            binding.btnYes.setOnClickListener(v -> {
                dialog.setOnDismissListener(dialog1 -> activity.finishAffinity());
                dialog.dismiss();
            });
            binding.btnNo.setOnClickListener(v -> dialog.dismiss());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showUpdateDialog(Activity activity) {
        Dialog dialog = new Dialog(activity);
        AdDialogUpdateBinding vBinding = AdDialogUpdateBinding.inflate(LayoutInflater.from(activity), null, false);
        dialog.setContentView(vBinding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
        vBinding.tvUpdate.setOnClickListener(v -> {
            ADCBAdLoader.openUrl(activity, ADCBMyApplication.getAdModel().getAdsAppUpDateLink());
        });
    }

    public static void startActivityWithAd(Activity activity, Intent intent) {
        ADCBAdLoader.getInstance().showInterstitialAd(activity, false, () -> activity.startActivity(intent));
    }

    public static void startActivityWithAd(Activity activity, ActivityResultLauncher<Intent> launcherIntent, Intent intent) {
        ADCBAdLoader.getInstance().showInterstitialAd(activity, false, () -> launcherIntent.launch(intent));
    }

    public static void startActivityWithFinishAd(Activity activity, Intent intent) {
        ADCBAdLoader.getInstance().showInterstitialAd(activity, false, () -> {
            activity.startActivity(intent);
            activity.finish();
        });
    }

    public static void startActivityWithFinishAffinityAd(Activity activity, Intent intent) {
        ADCBAdLoader.getInstance().showInterstitialAd(activity, false, () -> {
            activity.startActivity(intent);
            activity.finishAffinity();
        });
    }

    public static void finishWithAd(Activity activity) {
        ADCBAdLoader.getInstance().showInterstitialAd(activity, true, activity::finish);
    }

    public static void finishWithResultAd(Activity activity, int resultCode, Intent result) {
        ADCBAdLoader.getInstance().showInterstitialAd(activity, true, () -> {
            activity.setResult(resultCode, result);
            activity.finish();
        });
    }

    public static void finishAffinityWithAd(Activity activity) {
        ADCBAdLoader.getInstance().showInterstitialAd(activity, true, activity::finishAffinity);
    }

    public static void openUrl(Activity activity, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rateUs(Activity activity) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
            activity.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName()));
                activity.startActivity(intent);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void shareApp(Activity activity, String appTitle, String appPackage) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, appTitle);
            var shareMessage = "Let me recommend you this application";
            shareMessage = (shareMessage + " https://play.google.com/store/apps/details?id=" + appPackage);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            activity.startActivity(Intent.createChooser(shareIntent, "Select App"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String log, Exception e) {
        Log.d("ADCBLOGICLOG", log, e);
    }

    public static void log(String log) {
        Log.d("ADCBLOGICLOG", log);
        ADCBMyApplication.VIEW_LOG_DATA += log;
        ADCBMyApplication.VIEW_LOG_DATA += ";";
    }

    public ArrayList<NativeAd> getNativeAds() {
        return nativeAds;
    }

    public void loadInterstitialAds(final Activity activity) {
        if (ADCBMyApplication.getAdModel().getAdsInterstitial().equalsIgnoreCase("Yes")) {
            loadInterstitialAd(activity);
        } else if (ADCBMyApplication.getAdModel().getAdsInterstitialBack().equalsIgnoreCase("Yes")) {
            loadInterstitialAd(activity);
        }
    }

    public void showBanner(final Activity activity, final AdLayoutBannerBinding ltBanner) {
        if (ADCBMyApplication.getAdModel().getAdsBanner().equalsIgnoreCase("Yes")) {
            AdView adView = new AdView(activity);
            adView.setAdUnitId(ADCBMyApplication.getAdModel().getAdsBannerId());
            AdSize adSize = getAdSize(activity);
            adView.setAdSize(adSize);
            AdRequest adRequest = new AdRequest.Builder().build();
            if (IS_SHIMMER_ENABLED) {
                ltBanner.flShimmer.setVisibility(View.VISIBLE);
                ltBanner.tvAdSpace.setVisibility(View.GONE);
            } else {
                ltBanner.flShimmer.setVisibility(View.GONE);
                ltBanner.tvAdSpace.setVisibility(View.VISIBLE);
            }
            log("BANNER -> AD REQUEST");
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    log("BANNER -> AD LOADED");
                    log("BANNER -> AD SHOW");
                    ltBanner.flShimmer.setVisibility(View.GONE);
                    ltBanner.tvAdSpace.setVisibility(View.GONE);
                    ltBanner.flAd.setVisibility(View.VISIBLE);
                    ltBanner.flAd.removeAllViews();
                    ltBanner.flAd.addView(adView);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    showBanner(activity, ltBanner);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    log("BANNER -> AD FAILED");
                    if (HIDE_PLACE_HOLDERS_WHEN_AD_FAILED) {
                        ltBanner.flShimmer.setVisibility(View.GONE);
                        ltBanner.tvAdSpace.setVisibility(View.GONE);
                    }
                    ltBanner.flAd.setVisibility(View.GONE);
                }
            });
        } else {
            ltBanner.flShimmer.setVisibility(View.GONE);
            ltBanner.tvAdSpace.setVisibility(View.GONE);
            ltBanner.flAd.setVisibility(View.GONE);
        }
    }

    private void loadInterstitialAd(final Activity activity) {
        if (getFailedCountInterstitial() < ADCBMyApplication.getAdModel().getAdsInterstitialFailedCount()) {
            if (interstitialAd == null && !isInterstitialLoading) {
                isInterstitialLoading = true;
                AdRequest adRequest = new AdRequest.Builder().build();
                log("INTERSTITIAL -> AD REQUEST");
                InterstitialAd.load(activity, ADCBMyApplication.getAdModel().getAdsInterstitialId(), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        log("INTERSTITIAL -> AD LOADED");
                        ADCBAdLoader.this.interstitialAd = interstitialAd;
                        resetFailedCountInterstitial();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        increaseFailedCountInterstitial();
                        log("INTERSTITIAL -> AD FAILED (" + getFailedCountInterstitial() + " of " + ADCBMyApplication.getAdModel().getAdsInterstitialFailedCount() + ")\nKEY: " + ADCBMyApplication.getAdModel().getAdsInterstitialId() + "ERROR: " + loadAdError.getMessage());
                        interstitialAd = null;
                        isInterstitialLoading = false;
                    }
                });
            }
        } else {
            log("INTERSTITIAL -> FAILED COUNTER IS " + ADCBMyApplication.getAdModel().getAdsInterstitialFailedCount());
        }
    }

    public void showInterstitialAd(Activity activity, boolean isBack, FullScreenDismissListener listener) {
        if (isBack ? ADCBMyApplication.getAdModel().getAdsInterstitialBack().equalsIgnoreCase("Yes") : ADCBMyApplication.getAdModel().getAdsInterstitial().equalsIgnoreCase("Yes")) {
            int currentInterval = getInterstitialInterval(isBack);
            boolean isStartInterval = isInterstitialIntervalFirst(isBack);
            int startInterval = isBack ? ADCBMyApplication.getAdModel().getAdsInterstitialBackCountShow() : ADCBMyApplication.getAdModel().getAdsInterstitialCountShow();
            int regularInterval = isBack ? ADCBMyApplication.getAdModel().getAdsInterstitialBackCount() : ADCBMyApplication.getAdModel().getAdsInterstitialCount();
            if (currentInterval == (isStartInterval ? startInterval : regularInterval)) {
                if (interstitialAd != null) {
                    showProgressDialog(activity, () -> {
                        if (interstitialAd != null) {
                            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    applyInterstitialIntervalFirst(isBack);
                                    resetInterstitialInterval(isBack);
                                    interstitialAd = null;
                                    isInterstitialLoading = false;
                                    isInterstitialShowing = false;
                                    loadInterstitialAd(activity);
                                    listener.onDismiss();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    interstitialAd = null;
                                    isInterstitialLoading = false;
                                    isInterstitialShowing = false;
                                    listener.onDismiss();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    interstitialAd = null;
                                    isInterstitialShowing = true;
                                }
                            });
                            ADCBAdLoader.log("INTERSTITIAL -> AD SHOW");
                            interstitialAd.show(activity);
                        } else {
                            listener.onDismiss();
                        }
                    });
                } else {
                    loadInterstitialAds(activity);
                    listener.onDismiss();
                }
            } else {
                increaseInterstitialInterval(isBack);
                listener.onDismiss();
            }
        } else {
            listener.onDismiss();
        }
    }

    private AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public void showNativeLarge(Activity activity, AdLayoutNativeBinding ltNative) {
        showNative(activity, ltNative, "Large");
    }

    public void showNativeSmall(Activity activity, AdLayoutNativeBinding ltNative) {
        showNative(activity, ltNative, "Small");
    }

    public void showNativeLargeList(Activity activity, AdLayoutNativeBinding ltNative) {
        showNativeList(activity, ltNative, "Large");
    }

    public void showNativeSmallList(Activity activity, AdLayoutNativeBinding ltNative) {
        showNativeList(activity, ltNative, "Small");
    }

    public void showNativeList(Activity activity, AdLayoutNativeBinding ltNative, String adType) {
        if (nativeAds.size() > 0) {
            Collections.shuffle(nativeAds);
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
            if (adType.equalsIgnoreCase("Small")) {
                if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 1) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_1, null, false);
                } else if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 2) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_2, null, false);
                }
            } else if (adType.equalsIgnoreCase("Large")) {
                adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
            }
            ADCBAdLoader.getInstance().inflateGoogleNativeAd(activity, nativeAds.get(0), adView);
            ltNative.sflShimmerSmall1.setVisibility(View.GONE);
            ltNative.sflShimmerSmall2.setVisibility(View.GONE);
            ltNative.sflShimmerLarge.setVisibility(View.GONE);
            ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
            ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
            ltNative.flAd.setVisibility(View.VISIBLE);
            ltNative.flAd.removeAllViews();
            ltNative.flAd.addView(adView);
        } else {
            if (adType.equalsIgnoreCase("Small")) {
                if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 1) {
                    if (IS_SHIMMER_ENABLED) {
                        ltNative.sflShimmerSmall1.setVisibility(View.VISIBLE);
                        ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                        ltNative.sflShimmerLarge.setVisibility(View.GONE);
                        ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                        ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                    } else {
                        ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                        ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                        ltNative.sflShimmerLarge.setVisibility(View.GONE);
                        ltNative.tvAdSpaceSmall.setVisibility(View.VISIBLE);
                        ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                    }
                } else if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 2) {
                    if (IS_SHIMMER_ENABLED) {
                        ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                        ltNative.sflShimmerSmall2.setVisibility(View.VISIBLE);
                        ltNative.sflShimmerLarge.setVisibility(View.GONE);
                        ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                        ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                    } else {
                        ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                        ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                        ltNative.sflShimmerLarge.setVisibility(View.GONE);
                        ltNative.tvAdSpaceSmall.setVisibility(View.VISIBLE);
                        ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                    }
                }
            } else if (adType.equalsIgnoreCase("Large")) {
                if (IS_SHIMMER_ENABLED) {
                    ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                    ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                    ltNative.sflShimmerLarge.setVisibility(View.VISIBLE);
                    ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                    ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                } else {
                    ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                    ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                    ltNative.sflShimmerLarge.setVisibility(View.GONE);
                    ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                    ltNative.tvAdSpaceLarge.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showNative(Activity activity, AdLayoutNativeBinding ltNative, String adType) {
        if (ADCBMyApplication.getAdModel().getAdsNative().equalsIgnoreCase("Yes")) {
            if (nativeAdPreload != null) {
                showNativeAd(activity, ltNative, adType);
            } else {
                loadNativeAd(activity, ltNative, adType);
            }
        } else {
            ltNative.sflShimmerSmall1.setVisibility(View.GONE);
            ltNative.sflShimmerSmall2.setVisibility(View.GONE);
            ltNative.sflShimmerLarge.setVisibility(View.GONE);
            ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
            ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
            ltNative.flAd.setVisibility(View.GONE);
        }
    }

    private void showNativeAd(Activity activity, AdLayoutNativeBinding ltNative, String adType) {
        if (nativeAdPreload != null) {
            ADCBAdLoader.log("NATIVE (PRELOAD) -> AD SHOW");
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
            if (adType.equalsIgnoreCase("Small")) {
                if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 1) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_1, null, false);
                } else if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 2) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_2, null, false);
                }
            } else if (adType.equalsIgnoreCase("Large")) {
                adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
            }

            ADCBAdLoader.getInstance().inflateGoogleNativeAd(activity, nativeAdPreload, adView);
            ltNative.sflShimmerSmall1.setVisibility(View.GONE);
            ltNative.sflShimmerSmall2.setVisibility(View.GONE);
            ltNative.sflShimmerLarge.setVisibility(View.GONE);
            ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
            ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
            ltNative.flAd.setVisibility(View.VISIBLE);
            ltNative.flAd.removeAllViews();
            ltNative.flAd.addView(adView);
            loadNativeAdPreload(activity);
        } else {
            loadNativeAd(activity, ltNative, adType);
        }
    }

    public void loadNativeAdPreload(Activity activity) {
        if (getFailedCountNative() < ADCBMyApplication.getAdModel().getAdsNativeFailedCount()) {
            if (ADCBMyApplication.getAdModel().getAdsNativePreload().equalsIgnoreCase("Yes") && ADCBMyApplication.getAdModel().getAdsNative().equalsIgnoreCase("Yes")) {
                nativeAdPreload = null;
                VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
                NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                AdLoader appLoaderNativeOne = new AdLoader.Builder(activity, ADCBMyApplication.getAdModel().getAdsNativeId()).forNativeAd(nativeAd -> {
                    log("NATIVE (PRELOAD) -> AD LOADED");
                    this.nativeAdPreload = nativeAd;
                    if (nativeAds.size() < NATIVE_LIST_SIZE) {
                        nativeAds.add(this.nativeAdPreload);
                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        increaseFailedCountNative();
                        log("NATIVE (PRELOAD) -> AD FAILED (" + getFailedCountNative() + " of " + ADCBMyApplication.getAdModel().getAdsNativeFailedCount() + ")\nKEY: " + ADCBMyApplication.getAdModel().getAdsNativeId() + "ERROR: " + adError.getMessage());
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }
                }).withNativeAdOptions(adOptions).build();

                AdRequest adRequest = new AdRequest.Builder().build();
                log("NATIVE (PRELOAD) -> AD REQUEST");
                appLoaderNativeOne.loadAd(adRequest);
            }
        } else {
            log("NATIVE (PRELOAD) -> FAILED COUNTER IS " + ADCBMyApplication.getAdModel().getAdsNativeFailedCount());
        }
    }

    private void loadNativeAd(Activity activity, AdLayoutNativeBinding ltNative, String adType) {
        if (adType.equalsIgnoreCase("Small")) {
            if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 1) {
                if (IS_SHIMMER_ENABLED) {
                    ltNative.sflShimmerSmall1.setVisibility(View.VISIBLE);
                    ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                    ltNative.sflShimmerLarge.setVisibility(View.GONE);
                    ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                    ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                } else {
                    ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                    ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                    ltNative.sflShimmerLarge.setVisibility(View.GONE);
                    ltNative.tvAdSpaceSmall.setVisibility(View.VISIBLE);
                    ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                }
            } else if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 2) {
                if (IS_SHIMMER_ENABLED) {
                    ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                    ltNative.sflShimmerSmall2.setVisibility(View.VISIBLE);
                    ltNative.sflShimmerLarge.setVisibility(View.GONE);
                    ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                    ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                } else {
                    ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                    ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                    ltNative.sflShimmerLarge.setVisibility(View.GONE);
                    ltNative.tvAdSpaceSmall.setVisibility(View.VISIBLE);
                    ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                }
            }
        } else if (adType.equalsIgnoreCase("Large")) {
            if (IS_SHIMMER_ENABLED) {
                ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                ltNative.sflShimmerLarge.setVisibility(View.VISIBLE);
                ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
            } else {
                ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                ltNative.sflShimmerLarge.setVisibility(View.GONE);
                ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                ltNative.tvAdSpaceLarge.setVisibility(View.VISIBLE);
            }
        }
        if (getFailedCountNative() < ADCBMyApplication.getAdModel().getAdsNativeFailedCount()) {
            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
            AdLoader adLoaderNative = new AdLoader.Builder(activity, ADCBMyApplication.getAdModel().getAdsNativeId()).forNativeAd(nativeAd -> {
                log("NATIVE -> AD LOADED");
                resetFailedCountNative();
                if (nativeAds.size() < NATIVE_LIST_SIZE) {
                    nativeAds.add(nativeAd);
                }
                ADCBAdLoader.log("NATIVE -> AD SHOW");
                NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
                if (adType.equalsIgnoreCase("Small")) {
                    if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 1) {
                        adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_1, null, false);
                    } else if (ADCBMyApplication.getAdModel().getAdsNativeViewId() == 2) {
                        adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_2, null, false);
                    }
                } else if (adType.equalsIgnoreCase("Large")) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
                }

                ADCBAdLoader.getInstance().inflateGoogleNativeAd(activity, nativeAd, adView);
                ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                ltNative.sflShimmerLarge.setVisibility(View.GONE);
                ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                ltNative.flAd.setVisibility(View.VISIBLE);
                ltNative.flAd.removeAllViews();
                ltNative.flAd.addView(adView);
                loadNativeAdPreload(activity);
            }).withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    increaseFailedCountNative();
                    log("NATIVE -> AD FAILED (" + getFailedCountNative() + " of " + ADCBMyApplication.getAdModel().getAdsNativeFailedCount() + ")\nKEY: " + ADCBMyApplication.getAdModel().getAdsInterstitialId() + "ERROR: " + adError.getMessage());
                    if (HIDE_PLACE_HOLDERS_WHEN_AD_FAILED) {
                        ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                        ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                        ltNative.sflShimmerLarge.setVisibility(View.GONE);
                        ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                        ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
                    }
                    ltNative.flAd.setVisibility(View.GONE);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    showNative(activity, ltNative, adType);
                }
            }).withNativeAdOptions(adOptions).build();

            AdRequest adRequest = new AdRequest.Builder().build();
            log("NATIVE -> AD REQUEST");
            adLoaderNative.loadAd(adRequest);
        } else {
            if (HIDE_PLACE_HOLDERS_WHEN_AD_FAILED) {
                ltNative.sflShimmerSmall1.setVisibility(View.GONE);
                ltNative.sflShimmerSmall2.setVisibility(View.GONE);
                ltNative.sflShimmerLarge.setVisibility(View.GONE);
                ltNative.tvAdSpaceLarge.setVisibility(View.GONE);
                ltNative.tvAdSpaceSmall.setVisibility(View.GONE);
            }
            ltNative.flAd.setVisibility(View.GONE);
            log("NATIVE -> FAILED COUNTER IS " + ADCBMyApplication.getAdModel().getAdsNativeFailedCount());
        }
    }

    private void inflateGoogleNativeAd(Activity activity, NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        //adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        AppCompatButton install = adView.findViewById(R.id.ad_call_to_action);
        install.setText(nativeAd.getCallToAction());
        if (ADCBMyApplication.getAdModel().getAdsNativeColor().equalsIgnoreCase("Yes")) {
            install.getBackground().setColorFilter(ContextCompat.getColor(activity, R.color.ad_color_default), PorterDuff.Mode.SRC_ATOP);
        } else if (ADCBMyApplication.getAdModel().getAdsNativeColor().equalsIgnoreCase("No")) {
            install.getBackground().setColorFilter(ContextCompat.getColor(activity, R.color.ad_color_contrast), PorterDuff.Mode.SRC_ATOP);
        } else {
            install.getBackground().setColorFilter(ContextCompat.getColor(activity, R.color.ad_color_default), PorterDuff.Mode.SRC_ATOP);
        }

        adView.setCallToActionView(install);

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        if (adView.getMediaView() != null) {
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        }

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.GONE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.GONE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
            CardView ad_app_icon_cards = adView.findViewById(R.id.ad_app_icon_cards);
            View vSpace = adView.findViewById(R.id.vSpace);
            vSpace.setVisibility(View.VISIBLE);
            ad_app_icon_cards.setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
            View vSpace = adView.findViewById(R.id.vSpace);
            vSpace.setVisibility(View.GONE);
            CardView ad_app_icon_cards = adView.findViewById(R.id.ad_app_icon_cards);
            ad_app_icon_cards.setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.GONE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.GONE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.GONE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

        VideoController vc = nativeAd.getMediaContent().getVideoController();
        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    public interface FullScreenDismissListener {
        void onDismiss();
    }
}
