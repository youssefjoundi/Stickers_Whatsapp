/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.Flowers.animatedstickers.application;


import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

public class StickerApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "cb58ecd2-9d63-4851-9fc6-7f2d3f2d6010";
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)

        FirebaseMessaging.getInstance().subscribeToTopic("sticker_notification");
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }


}
