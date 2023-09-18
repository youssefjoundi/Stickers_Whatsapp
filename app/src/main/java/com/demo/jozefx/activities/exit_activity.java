package com.demo.jozefx.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.jozefx.R;
import com.demo.jozefx.utils.AdsManager;
import com.solodroid.ads.sdk.format.NativeAd;

public class exit_activity extends AppCompatActivity {

    private TextView text1;
    private TextView text2;
    NativeAd.Builder nativeAd;
    private LinearLayout yes;

    LinearLayout nativeAdViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        text1 = (TextView) findViewById(R.id.text_Wait);
        text2 = (TextView)  findViewById(R.id.text_exit);
        LinearLayout no;
        no = (LinearLayout) findViewById(R.id.no);
        yes = (LinearLayout) findViewById(R.id.yes);
        yes.setOnClickListener((View v) -> {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            StickerPackListActivity.fa.finish();
            this.finish();
        });
        no.setOnClickListener((View v) -> {
            this.onBackPressed();
        });
        nativeAdViewContainer = findViewById(R.id.native_ad);


        new AdsManager();
        AdsManager.getInstance().setNativeAdStyle(nativeAdViewContainer, this);



        AdsManager.getInstance().init(this);
        AdsManager.getInstance().loadNative(this, ()-> show());
    }

    public void show() {
        text1.setVisibility(View.GONE);
        text2.setVisibility(View.VISIBLE);
    }
}