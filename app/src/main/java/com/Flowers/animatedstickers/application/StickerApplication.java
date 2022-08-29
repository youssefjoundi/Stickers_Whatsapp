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

public class StickerApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "b0d04b5c-2088-4ab3-8ef2-9ecd8b149432";

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }


}
