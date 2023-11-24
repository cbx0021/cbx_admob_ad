package com.addemo.cbx_admob_ad.ad_manager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ADCBAdModel {
    @SerializedName("ads_app_open_id")
    @Expose
    private String adsAppOpenId = "None";

    @SerializedName("ads_interstitial_id")
    @Expose
    private String adsInterstitialId = "None";

    @SerializedName("ads_banner_id")
    @Expose
    private String adsBannerId = "None";

    @SerializedName("ads_native_id")
    @Expose
    private String adsNativeId = "None";

    @SerializedName("ads_splash")
    @Expose
    private String adsSplash = "None";

    @SerializedName("ads_exit")
    @Expose
    private String adsExit = "None";

    @SerializedName("ads_banner")
    @Expose
    private String adsBanner = "No";

    @SerializedName("ads_interstitial")
    @Expose
    private String adsInterstitial = "No";

    @SerializedName("ads_interstitial_back")
    @Expose
    private String adsInterstitialBack = "No";

    @SerializedName("ads_native")
    @Expose
    private String adsNative = "No";

    @SerializedName("ads_app_open")
    @Expose
    private String adsAppOpen = "No";

    @SerializedName("ads_interstitial_count")
    @Expose
    private int adsInterstitialCount = 3;

    @SerializedName("ads_interstitial_count_show")
    @Expose
    private int adsInterstitialCountShow = 3;

    @SerializedName("ads_interstitial_back_count")
    @Expose
    private int adsInterstitialBackCount = 3;

    @SerializedName("ads_interstitial_back_count_show")
    @Expose
    private int adsInterstitialBackCountShow = 3;

    @SerializedName("ads_interstitial_failed_count")
    @Expose
    private int adsInterstitialFailedCount = 3;

    @SerializedName("ads_native_failed_count")
    @Expose
    private int adsNativeFailedCount = 3;

    @SerializedName("ads_appopen_failed_count")
    @Expose
    private int adsAppOpenFailedCount = 3;

    @SerializedName("ads_native_view_id")
    @Expose
    private int adsNativeViewId = 1;

    @SerializedName("ads_native_color")
    @Expose
    private String adsNativeColor = "None";

    @SerializedName("ads_native_preload")
    @Expose
    private String adsNativePreload = "None";

    @SerializedName("ads_app_id")
    @Expose
    private String adsAppId = "None";

    @SerializedName("ads_app_up_date")
    @Expose
    private String adsAppUpDate = "None";

    @SerializedName("ads_app_up_date_link")
    @Expose
    private String adsAppUpDateLink = "None";

    @SerializedName("qureka_link")
    @Expose
    private String qurekaLink = "None";

    @SerializedName("privacy_link")
    @Expose
    private String privacyPolicy = "None";

    public void setAdsAppOpenId(String adsAppOpenId) {
        this.adsAppOpenId = adsAppOpenId;
    }

    public void setAdsInterstitialId(String adsInterstitialId) {
        this.adsInterstitialId = adsInterstitialId;
    }

    public void setAdsBannerId(String adsBannerId) {
        this.adsBannerId = adsBannerId;
    }

    public void setAdsNativeId(String adsNativeId) {
        this.adsNativeId = adsNativeId;
    }

    public void setAdsSplash(String adsSplash) {
        this.adsSplash = adsSplash;
    }

    public void setAdsExit(String adsExit) {
        this.adsExit = adsExit;
    }

    public void setAdsBanner(String adsBanner) {
        this.adsBanner = adsBanner;
    }

    public void setAdsInterstitial(String adsInterstitial) {
        this.adsInterstitial = adsInterstitial;
    }

    public void setAdsInterstitialBack(String adsInterstitialBack) {
        this.adsInterstitialBack = adsInterstitialBack;
    }

    public int getAdsInterstitialFailedCount() {
        return adsInterstitialFailedCount;
    }

    public void setAdsInterstitialFailedCount(int adsInterstitialFailedCount) {
        this.adsInterstitialFailedCount = adsInterstitialFailedCount;
    }

    public int getAdsNativeFailedCount() {
        return adsNativeFailedCount;
    }

    public void setAdsNativeFailedCount(int adsNativeFailedCount) {
        this.adsNativeFailedCount = adsNativeFailedCount;
    }

    public int getAdsAppOpenFailedCount() {
        return adsAppOpenFailedCount;
    }

    public void setAdsAppOpenFailedCount(int adsAppOpenFailedCount) {
        this.adsAppOpenFailedCount = adsAppOpenFailedCount;
    }

    public void setAdsNative(String adsNative) {
        this.adsNative = adsNative;
    }

    public void setAdsAppOpen(String adsAppOpen) {
        this.adsAppOpen = adsAppOpen;
    }

    public void setAdsInterstitialCount(int adsInterstitialCount) {
        this.adsInterstitialCount = adsInterstitialCount;
    }

    public void setAdsInterstitialCountShow(int adsInterstitialCountShow) {
        this.adsInterstitialCountShow = adsInterstitialCountShow;
    }

    public void setAdsInterstitialBackCount(int adsInterstitialBackCount) {
        this.adsInterstitialBackCount = adsInterstitialBackCount;
    }

    public void setAdsInterstitialBackCountShow(int adsInterstitialBackCountShow) {
        this.adsInterstitialBackCountShow = adsInterstitialBackCountShow;
    }

    public void setAdsNativeViewId(int adsNativeViewId) {
        this.adsNativeViewId = adsNativeViewId;
    }

    public void setAdsNativeColor(String adsNativeColor) {
        this.adsNativeColor = adsNativeColor;
    }

    public void setAdsNativePreload(String adsNativePreload) {
        this.adsNativePreload = adsNativePreload;
    }

    public void setAdsAppId(String adsAppId) {
        this.adsAppId = adsAppId;
    }

    public void setAdsAppUpDate(String adsAppUpDate) {
        this.adsAppUpDate = adsAppUpDate;
    }

    public void setAdsAppUpDateLink(String adsAppUpDateLink) {
        this.adsAppUpDateLink = adsAppUpDateLink;
    }

    public void setQurekaLink(String qurekaLink) {
        this.qurekaLink = qurekaLink;
    }

    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public String getAdsAppOpenId() {
        return adsAppOpenId;
    }

    public String getAdsInterstitialId() {
        return adsInterstitialId;
    }

    public String getAdsBannerId() {
        return adsBannerId;
    }

    public String getAdsNativeId() {
        return adsNativeId;
    }

    public String getAdsSplash() {
        return adsSplash;
    }

    public String getAdsExit() {
        return adsExit;
    }

    public String getAdsBanner() {
        return adsBanner;
    }

    public String getAdsInterstitial() {
        return adsInterstitial;
    }

    public String getAdsInterstitialBack() {
        return adsInterstitialBack;
    }

    public String getAdsNative() {
        return adsNative;
    }

    public String getAdsAppOpen() {
        return adsAppOpen;
    }

    public int getAdsInterstitialCount() {
        return adsInterstitialCount;
    }

    public int getAdsInterstitialCountShow() {
        return adsInterstitialCountShow;
    }

    public int getAdsInterstitialBackCount() {
        return adsInterstitialBackCount;
    }

    public int getAdsInterstitialBackCountShow() {
        return adsInterstitialBackCountShow;
    }

    public int getAdsNativeViewId() {
        return adsNativeViewId;
    }

    public String getAdsNativeColor() {
        return adsNativeColor;
    }

    public String getAdsNativePreload() {
        return adsNativePreload;
    }

    public String getAdsAppId() {
        return adsAppId;
    }

    public String getAdsAppUpDate() {
        return adsAppUpDate;
    }

    public String getAdsAppUpDateLink() {
        return adsAppUpDateLink;
    }

    public String getQurekaLink() {
        return qurekaLink;
    }

    public String getPrivacyPolicy() {
        return privacyPolicy;
    }
}