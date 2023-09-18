package com.demo.jozefx.utils;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;


import com.demo.jozefx.BuildConfig;
import com.demo.jozefx.R;
import com.solodroid.ads.sdk.format.AdNetwork;
import com.solodroid.ads.sdk.format.BannerAd;
import com.solodroid.ads.sdk.format.InterstitialAd;
import com.solodroid.ads.sdk.format.NativeAd;
import com.solodroid.ads.sdk.util.OnInterstitialAdDismissedListener;
import com.solodroid.ads.sdk.util.OnNativeLoaded;


public class AdsManager {
    private static AdsManager instance;
    AdNetwork.Initialize adNetwork;
    com.solodroid.ads.sdk.format.InterstitialAd.Builder interstitialAd;
    com.solodroid.ads.sdk.format.InterstitialAd.Builder interstitialAd1;
    public AdsManager() {

    }

    public static AdsManager getInstance() {
        if (instance == null) {
            instance = new AdsManager();
        }
        return instance;
    }

    public void init(Activity activity) {

        adNetwork = new AdNetwork.Initialize(activity)
                .setAdStatus(Constants.ad_status)
                .setAdNetwork(Constants.ad_network)
                .setBackupAdNetwork(Constants.back_up)
                .setAdMobAppId(activity.getResources().getString(R.string.admob_app_id))
                .setAppLovinSdkKey(activity.getResources().getString(R.string.applovin_sdk_key))
                .setDebug(BuildConfig.DEBUG)
                .build();
    }



    public void loadInterstitial(final Activity activity) {
        interstitialAd = new InterstitialAd.Builder(activity)
                .setAdStatus(Constants.ad_status)
                .setAdNetwork(Constants.ad_network)
                .setBackupAdNetwork(Constants.back_up)
                .setAdMobInterstitialId(Constants.adMobInterstitialId)
                .setGoogleAdManagerInterstitialId(Constants.adMobInterstitialId)
                .setFanInterstitialId(Constants.fanInterstitialId)
                .setAppLovinInterstitialId(Constants.maxInterstitialId)
                .setAppLovinInterstitialZoneId(Constants.maxInterstitialId)
                .setInterval(Constants.adsInterval)
                .build();
    }


    public void loadInterstitial1(final Activity activity) {
        interstitialAd1 = new InterstitialAd.Builder(activity)
                .setAdStatus(Constants.ad_status)
                .setAdNetwork(Constants.ad_network)
                .setBackupAdNetwork(Constants.back_up)
                .setAdMobInterstitialId(Constants.adMobInterstitialId)
                .setGoogleAdManagerInterstitialId(Constants.adMobInterstitialId)
                .setFanInterstitialId(Constants.fanInterstitialId)
                .setAppLovinInterstitialId(Constants.maxInterstitialId)
                .setAppLovinInterstitialZoneId(Constants.maxInterstitialId)
                .setInterval(1)
                .build();
    }



    public void showInterstitialAd(final OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
        interstitialAd.show(onInterstitialAdDismissedListener);
    }

    public void showInterstitialAd1(final OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
        interstitialAd1.show(onInterstitialAdDismissedListener);
    }

    public void detachbanner() {
        bannerAd.destroyAndDetachBanner();
    }

    BannerAd.Builder bannerAd;
    public void showBanner(Activity activity) {
        bannerAd = new BannerAd.Builder(activity)
                .setAdStatus(Constants.ad_status)
                .setAdNetwork(Constants.ad_network)
                .setBackupAdNetwork(Constants.back_up)
                .setAdMobBannerId(Constants.adMobBannerId)
                .setGoogleAdManagerBannerId(Constants.adMobBannerId)
                .setFanBannerId(Constants.fanBannerId)
                .setAppLovinBannerId(Constants.maxBannerId)
                .setAppLovinBannerZoneId(Constants.maxBannerId)
                .setDarkTheme(false)
                .build();
    }

    NativeAd.Builder nativeAd;

    public void loadNative(Activity activity, OnNativeLoaded OnNativeLoaded) {
        nativeAd = new NativeAd.Builder(activity)
                .setAdStatus(Constants.ad_status)
                .setAdNetwork(Constants.ad_network)
                .setBackupAdNetwork(Constants.back_up)
                .setAdMobNativeId(Constants.adMobNativeId)
                .setAdManagerNativeId(Constants.adMobNativeId)
                .setFanNativeId(Constants.fanNativeId)
                .setAppLovinNativeId(Constants.maxNativeId)
                .setAppLovinDiscoveryMrecZoneId(Constants.maxNativeId)
                .setNativeAdStyle(Constants.nativeStyle)
                .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                .setPadding(0, 0, 0, 0)
                .setDarkTheme(false)
                .build(OnNativeLoaded);
    }

    public void setNativeAdStyle(LinearLayout nativeAdView, Activity activity) {
        switch (Constants.nativeStyle) {
            case "news":
                nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_news, null));
                break;
            case "radio":
                nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_radio, null));
                break;
            case "video_small":
                nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_video_small, null));
                break;
            case "video_large":
                nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_video_large, null));
                break;
            default:
                nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_medium, null));
                break;
        }
    }




}
