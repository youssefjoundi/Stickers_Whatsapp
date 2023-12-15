package com.demo.jozefx.utils;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


import com.demo.jozefx.BuildConfig;
import com.demo.jozefx.R;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.solodroid.ads.sdk.format.AdNetwork;
import com.solodroid.ads.sdk.format.BannerAd;
import com.solodroid.ads.sdk.format.InterstitialAd;
import com.solodroid.ads.sdk.format.NativeAd;
import com.solodroid.ads.sdk.util.OnInterstitialAdDismissedListener;
import com.solodroid.ads.sdk.util.OnNativeLoaded;

import java.util.concurrent.atomic.AtomicBoolean;


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
    private ConsentInformation consentInformation;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    public void init(Activity activity) {
        requestConstentForm(activity);
    }



    public void requestConstentForm(Activity activity) {

//        ConsentDebugSettings.Builder().addTestDeviceHashedId("75B4865EF1E789733D7A46D7EB6D805B")
        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("75B4865EF1E789733D7A46D7EB6D805B")
                .build();


        // Set tag for under age of consent. false means users are not under age
        // of consent.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            activity,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w("Jozef Here : ", String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initializeMobileAdsSdk(activity);
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w("Jozef Here : ", String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk(activity);
        }
    }


    private void initializeMobileAdsSdk(Activity activity) {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize the Google Mobile Ads SDK.
        adNetwork = new AdNetwork.Initialize(activity)
                .setAdStatus(Constants.ad_status)
                .setAdNetwork(Constants.ad_network)
                .setBackupAdNetwork(Constants.back_up)
                .setAdMobAppId(activity.getResources().getString(R.string.admob_app_id))
                .setAppLovinSdkKey(activity.getResources().getString(R.string.applovin_sdk_key))
                .setDebug(BuildConfig.DEBUG)
                .build();

        // TODO: Request an ad.
        // InterstitialAd.load(...);
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
