/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.Flowers.animatedstickers.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.Flowers.animatedstickers.R;
import com.Flowers.animatedstickers.StickerPack;
import com.Flowers.animatedstickers.StickerPackLoader;
import com.Flowers.animatedstickers.StickerPackValidator;
import com.Flowers.animatedstickers.utils.AdsManager;
import com.Flowers.animatedstickers.utils.Constants;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.solodroid.ads.sdk.format.NativeAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class EntryActivity extends BaseActivity {

    private Button btn;
    private RelativeLayout saka;

    private boolean connexion;

    private View progressBar;
    private LoadListAsyncTask loadListAsyncTask;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        saka = (RelativeLayout) findViewById(R.id.content);
        btn = (Button) findViewById(R.id.btnDownload);

        new AdsManager();
        overridePendingTransition(0, 0);



        progressBar = findViewById(R.id.progress);
        getData();
    }

    private void loadListAsync() {
        loadListAsyncTask = new LoadListAsyncTask(this);
        loadListAsyncTask.execute();
    }

    private void checkYourInternet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning !");
        builder.setMessage("Check your internet connection to start");
        builder.setCancelable(false);

        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getData();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getData() {
        Volley.newRequestQueue(this).add(new StringRequest(0, Constants.jsonUrl, new Response.Listener<String>() {
            public void onResponse(String str) {
                try {
                    JSONObject jSONObjectRoot = new JSONObject(str);
                    JSONObject jsonAds = jSONObjectRoot.getJSONObject("Ads");
                    Constants.adsInterval = jsonAds.getInt("ads_interval");
                    Constants.itemFeed = jsonAds.getInt("itemFeed");
                    Constants.ad_network = jsonAds.getString("ad_network");
                    Constants.back_up = jsonAds.getString("back_up");
                    Constants.nativeStyle = jsonAds.getString("stylenative");
                    Constants.ad_status = jsonAds.getString("ad_status");
                    //----------------------- AdMob -----------------------//
                    JSONObject jsonAdMob = jsonAds.getJSONObject("AdMob");
                    Constants.adMobBannerId = jsonAdMob.getString("banner");
                    Constants.adMobInterstitialId = jsonAdMob.getString("interstitial");
                    Constants.adMobNativeId = jsonAdMob.getString("native");
                    //----------------------- AdMob -----------------------//
                    //----------------------- AdMob -----------------------//
                    JSONObject jsonGamanager = jsonAds.getJSONObject("GAM");
                    Constants.gAdMangerBannerId = jsonGamanager.getString("banner");
                    Constants.gAdMangerInterstitialId = jsonGamanager.getString("interstitial");
                    Constants.gAdMangerNativeId = jsonGamanager.getString("native");
                    //----------------------- AdMob -----------------------//

                    //----------------------- FAN -----------------------//
                    JSONObject jsonFan = jsonAds.getJSONObject("FAN");
                    Constants.fanBannerId = jsonFan.getString("banner");
                    Constants.fanInterstitialId = jsonFan.getString("interstitial");
                    Constants.fanNativeId = jsonFan.getString("native");
                    //----------------------- FAN -----------------------//

                    //----------------------- ironSource -----------------------//
                    JSONObject jsonIron = jsonAds.getJSONObject("ironSource");
                    Constants.ironAppKey = jsonIron.getString("app_key");
                    //----------------------- ironSource -----------------------//

                    //----------------------- MAX -----------------------//
                    JSONObject jsonMax = jsonAds.getJSONObject("MAX");
                    Constants.maxBannerId = jsonMax.getString("banner");
                    Constants.maxInterstitialId = jsonMax.getString("interstitial");
                    Constants.maxNativeId = jsonMax.getString("native");
                    //----------------------- MAX -----------------------//


                    connexion = true;
                    loadListAsync();

                } catch (JSONException e) {
                    checkYourInternet();
                    loadListAsync();
                    Log.e("Catch", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                checkYourInternet();
                loadListAsync();
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadListAsyncTask != null && !loadListAsyncTask.isCancelled()) {
            loadListAsyncTask.cancel(true);
        }
    }

    class LoadListAsyncTask extends AsyncTask<Void, Void, Pair<String, ArrayList<StickerPack>>> {
        private final WeakReference<EntryActivity> contextWeakReference;

        LoadListAsyncTask(EntryActivity activity) {
            this.contextWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Pair<String, ArrayList<StickerPack>> doInBackground(Void... voids) {
            ArrayList<StickerPack> stickerPackList;
            try {
                final Context context = contextWeakReference.get();
                if (context != null) {
                    stickerPackList = StickerPackLoader.fetchStickerPacks(context);
                    if (stickerPackList.isEmpty()) {
                        return new Pair<>("could not find any packs", null);
                    }
                    for (StickerPack stickerPack : stickerPackList) {
                        StickerPackValidator.verifyStickerPackValidity(context, stickerPack);
                    }
                    return new Pair<>(null, stickerPackList);
                } else {
                    return new Pair<>("could not fetch sticker packs", null);
                }
            } catch (Exception e) {
                Log.e("EntryActivity", "error fetching sticker packs", e);
                return new Pair<>(e.getMessage(), null);
            }
        }


        @Override
        protected void onPostExecute(Pair<String, ArrayList<StickerPack>> stringListPair) {

            final EntryActivity entryActivity = contextWeakReference.get();
            if (entryActivity != null) {
                if (stringListPair.first != null) {
                    entryActivity.showErrorMessage(stringListPair.first);
                } else {
                    refreshAd();
                    btn.setOnClickListener((View v) ->
                            entryActivity.showStickerPack(stringListPair.second)
                    );


                }
            }
        }

        NativeAd.Builder nativeAd;
        private void refreshAd() {
            if (connexion)
            {
                nativeAd = new com.solodroid.ads.sdk.format.NativeAd.Builder(EntryActivity.this)
                        .setAdStatus(Constants.ad_status)
                        .setAdNetwork(Constants.ad_network)
                        .setBackupAdNetwork(Constants.back_up)
                        .setAdMobNativeId(Constants.adMobNativeId)
                        .setAdManagerNativeId(Constants.gAdMangerNativeId)
                        .setFanNativeId(Constants.fanNativeId)
                        .setAppLovinNativeId(Constants.maxNativeId)
                        .setNativeAdStyle(Constants.nativeStyle)
                        .setDarkTheme(false)
                        .setNativeEvent(()-> showButton())
                        .build();
            }

        }
    }

    private void showButton() {
        saka.setVisibility(View.GONE);
        btn.setVisibility(View.VISIBLE);
    }


    private void showErrorMessage(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        Log.e("EntryActivity", "error fetching sticker packs, " + errorMessage);
        final TextView errorMessageTV = findViewById(R.id.error_message);
        errorMessageTV.setText(getString(R.string.error_message, errorMessage));
    }

    private void showStickerPack(ArrayList<StickerPack> stickerPackList) {
        progressBar.setVisibility(View.GONE);
        if (stickerPackList.size() > 1) {
            final Intent intent = new Intent(this, StickerPackListActivity.class);
            intent.putParcelableArrayListExtra(StickerPackListActivity.EXTRA_STICKER_PACK_LIST_DATA, stickerPackList);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else {
            final Intent intent = new Intent(this, StickerPackDetailsActivity.class);
            intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, false);
            intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPackList.get(0));
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }
}
