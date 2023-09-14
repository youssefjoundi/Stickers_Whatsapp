/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.demo.jozefx.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.demo.jozefx.BuildConfig;
import com.demo.jozefx.StickerPack;
import com.demo.jozefx.adapters.StickerPackListAdapter;
import com.demo.jozefx.stickerloader.WhitelistCheck;
import com.demo.jozefx.utils.AdsManager;
import com.demo.jozefx.utils.Constants;
import com.demo.jozefx.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class StickerPackListActivity extends AddStickerPackActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static Activity fa;
    private String appUrl = "https://play.google.com/store/apps/details?id=";

    private DrawerLayout drawer;
    //////////

    public static final String EXTRA_STICKER_PACK_LIST_DATA = "sticker_pack_list";
    private static final int STICKER_PREVIEW_DISPLAY_LIMIT = 5;
    private LinearLayoutManager packLayoutManager;
    private RecyclerView packRecyclerView;
    private StickerPackListAdapter allStickerPacksListAdapter;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    private ArrayList<StickerPack> stickerPackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sticker_pack_list);

        fa = this;
        AdsManager.getInstance().init(this);
        AdsManager.getInstance().showBannerIronSource(this);
        AdsManager.getInstance().loadIronSourceInterstitial(this);
/////////////oncreate///////////////
        drawer = findViewById(R.id.draw);
        Toolbar toolbar;
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_format_align_left_24);

        Toolbar toolbar1 = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar1);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);






        packRecyclerView = findViewById(R.id.sticker_pack_list);
        stickerPackList = getIntent().getParcelableArrayListExtra(EXTRA_STICKER_PACK_LIST_DATA);
        showStickerPackList(stickerPackList);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getQuantityString(R.plurals.title_activity_sticker_packs_list, stickerPackList.size()));
        }

    }






    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
        whiteListCheckAsyncTask.execute(stickerPackList.toArray(new StickerPack[0]));
        if (Constants.counter >= Constants.adsInterval){
            AdsManager.getInstance().loadIronSourceInterstitial(this);
            Constants.counter = 0;
        }
        AdsManager.getInstance().init(this);
        AdsManager.getInstance().showBannerIronSource(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
        AdsManager.getInstance().init(this);
//        AdsManager.getInstance().showBannerIronSource(this);
    }

    private void showStickerPackList(List<StickerPack> stickerPackList) {
        allStickerPacksListAdapter = new StickerPackListAdapter(stickerPackList, onAddButtonClickedListener);
        packRecyclerView.setAdapter(allStickerPacksListAdapter);
        packLayoutManager = new LinearLayoutManager(this);
        packLayoutManager.setOrientation(RecyclerView.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                packRecyclerView.getContext(),
                packLayoutManager.getOrientation()
        );
        packRecyclerView.addItemDecoration(dividerItemDecoration);
        packRecyclerView.setLayoutManager(packLayoutManager);
        observerRecycleView();
    }


    private final StickerPackListAdapter.OnAddButtonClickedListener onAddButtonClickedListener =  pack -> addStickerPackToWhatsApp(pack.identifier, pack.name);

    public void observerRecycleView () {
        packRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this::recalculateColumnCount);
    }

    private void recalculateColumnCount() {
        final int previewSize = getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size);
        int firstVisibleItemPosition = packLayoutManager.findFirstVisibleItemPosition();
        try {
            StickerPackListAdapter.StickerListViewHolder viewHolder = (StickerPackListAdapter.StickerListViewHolder) packRecyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition);
            if (viewHolder != null) {
                final int widthOfImageRow = viewHolder.itemView.getMeasuredWidth();
                final int max = Math.max(widthOfImageRow / previewSize, 1);
                int maxNumberOfImagesInARow = Math.min(STICKER_PREVIEW_DISPLAY_LIMIT, max);
                int minMarginBetweenImages = (widthOfImageRow - maxNumberOfImagesInARow * previewSize) / (maxNumberOfImagesInARow - 1);
                allStickerPacksListAdapter.setImageRowSpec(maxNumberOfImagesInARow, minMarginBetweenImages);
            }
        } catch (Exception e){
            Log.e("Ads Layout" , e.toString());
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.privacy_side:

                Uri urz = Uri.parse(getString(R.string.privacy_policy_url));
                Intent rate = new Intent(Intent.ACTION_VIEW, urz);
                startActivity(rate);

                break;

            case R.id.moreapps_side:
                Uri w = Uri.parse(getString(R.string.developer_page_more_apps));
                Intent q = new Intent(Intent.ACTION_VIEW, w);
                startActivity(q);

                break;
            case R.id.rateapp_side:
                urlsite();

                break;
            case R.id.exit_side:
                onBackPressed();

                break;

            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "i am using this amazing Animated whatsapp stickers app contain 1000+ Funny stickers\n " +   appUrl + BuildConfig.APPLICATION_ID;
                String shareSub = "Your subject here";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));

                break;
            default:
                break;

        }



        drawer.closeDrawer(GravityCompat.START);

        return true;

    }


    static class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, List<StickerPack>> {
        private final WeakReference<StickerPackListActivity> stickerPackListActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackListActivity stickerPackListActivity) {
            this.stickerPackListActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @Override
        protected final List<StickerPack> doInBackground(StickerPack... stickerPackArray) {
            final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity == null) {
                return Arrays.asList(stickerPackArray);
            }
            for (StickerPack stickerPack : stickerPackArray) {
                stickerPack.setIsWhitelisted(WhitelistCheck.isWhitelisted(stickerPackListActivity, stickerPack.identifier));
            }
            return Arrays.asList(stickerPackArray);
        }

        @Override
        protected void onPostExecute(List<StickerPack> stickerPackList) {
            final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity != null) {
                stickerPackListActivity.allStickerPacksListAdapter.setStickerPackList(stickerPackList);
                stickerPackListActivity.allStickerPacksListAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, exit_activity.class);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void urlsite(){
        Uri f = Uri.parse( appUrl + BuildConfig.APPLICATION_ID);
        Intent b = new Intent(Intent.ACTION_VIEW, f);
        startActivity(b);
    }




}
