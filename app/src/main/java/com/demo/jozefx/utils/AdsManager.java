package com.demo.jozefx.utils;

import android.app.Activity;


import com.demo.jozefx.BuildConfig;
import com.demo.jozefx.R;
import com.solodroid.ads.sdk.format.AdNetwork;
import com.solodroid.ads.sdk.format.BannerAd;
import com.solodroid.ads.sdk.format.InterstitialAd;


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
                .setStartappAppId(Constants.IRONSOURCE_BANNER_ID)
                .setUnityGameId(Constants.IRONSOURCE_BANNER_ID)
                .setAppLovinSdkKey(activity.getResources().getString(R.string.applovin_sdk_key))
                .setIronSourceAppKey(Constants.ironAppKey)
                .setDebug(BuildConfig.DEBUG)
                .build();
    }



    public void loadIronSourceInterstitial(final Activity activity) {
        interstitialAd = new InterstitialAd.Builder(activity)
                .setAdStatus(Constants.ad_status)
                .setAdNetwork(Constants.ad_network)
                .setBackupAdNetwork(Constants.back_up)
                .setAdMobInterstitialId(Constants.adMobInterstitialId)
                .setGoogleAdManagerInterstitialId(Constants.gAdMangerInterstitialId)
                .setFanInterstitialId(Constants.fanInterstitialId)
                .setUnityInterstitialId(Constants.IRONSOURCE_BANNER_ID)
                .setAppLovinInterstitialId(Constants.maxInterstitialId)
                .setAppLovinInterstitialZoneId(Constants.maxInterstitialId)
                .setIronSourceInterstitialId(Constants.IRONSOURCE_INTERSTITIAL_ID)
                .setInterval(Constants.adsInterval)
                .build();
    }


    public void loadIronSourceInterstitial1(final Activity activity) {
        interstitialAd1 = new InterstitialAd.Builder(activity)
                .setAdStatus(Constants.ad_status)
                .setAdNetwork(Constants.ad_network)
                .setBackupAdNetwork(Constants.back_up)
                .setAdMobInterstitialId(Constants.adMobInterstitialId)
                .setGoogleAdManagerInterstitialId(Constants.gAdMangerInterstitialId)
                .setFanInterstitialId(Constants.fanInterstitialId)
                .setUnityInterstitialId(Constants.IRONSOURCE_BANNER_ID)
                .setAppLovinInterstitialId(Constants.maxInterstitialId)
                .setAppLovinInterstitialZoneId(Constants.maxInterstitialId)
                .setIronSourceInterstitialId(Constants.IRONSOURCE_INTERSTITIAL_ID)
                .setInterval(1)
                .build();
    }



    public void showInterstitialAd(final InterstitialAd.Builder.AdCloseListener adCloseListener) {
        interstitialAd.show(adCloseListener);
    }

    public void showInterstitialAd1(final InterstitialAd.Builder.AdCloseListener adCloseListener) {
        interstitialAd1.show(adCloseListener);
    }

    public void detachbanner() {
        bannerAd.destroyAndDetachBanner();
    }

    BannerAd.Builder bannerAd;
    public void showBannerIronSource(Activity activity) {
        bannerAd = new BannerAd.Builder(activity)
                .setAdStatus(Constants.ad_status)
                .setAdNetwork(Constants.ad_network)
                .setBackupAdNetwork(Constants.back_up)
                .setAdMobBannerId(Constants.adMobBannerId)
                .setGoogleAdManagerBannerId(Constants.gAdMangerBannerId)
                .setFanBannerId(Constants.fanBannerId)
                .setUnityBannerId(Constants.IRONSOURCE_BANNER_ID)
                .setAppLovinBannerId(Constants.maxBannerId)
                .setAppLovinBannerZoneId(Constants.maxBannerId)
                .setIronSourceBannerId(Constants.IRONSOURCE_BANNER_ID)
                .setDarkTheme(false)
                .build();
    }

}
