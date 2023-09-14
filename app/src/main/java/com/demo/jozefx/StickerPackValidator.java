/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.demo.jozefx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;

import com.facebook.animated.webp.WebPImage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class StickerPackValidator {
    private static final int STICKER_FILE_SIZE_LIMIT_KB = 500;
    static final int EMOJI_MAX_LIMIT = 3;
    private static final int EMOJI_MIN_LIMIT = 1;
    private static final int IMAGE_HEIGHT = 512;
    private static final int IMAGE_WIDTH = 512;
    private static final int STICKER_SIZE_MIN = 3;
    private static final int STICKER_SIZE_MAX = 30;
    private static final int CHAR_COUNT_MAX = 128;
    private static final long ONE_KIBIBYTE = 8 * 1024;
    private static final int TRAY_IMAGE_FILE_SIZE_MAX_KB = 50;
    private static final int TRAY_IMAGE_DIMENSION_MIN = 24;
    private static final int TRAY_IMAGE_DIMENSION_MAX = 512;


    /**
     * Checks whether a sticker pack contains valid data
     */
    public static void verifyStickerPackValidity(@NonNull Context context, @NonNull StickerPack stickerPack)  {
        if (TextUtils.isEmpty(stickerPack.identifier)) {
            throw new IllegalStateException("sticker pack identifier is empty");
        }
        if (stickerPack.identifier.length() > CHAR_COUNT_MAX) {
            throw new IllegalStateException("sticker pack identifier cannot exceed " + CHAR_COUNT_MAX + " characters");
        }
        checkStringValidity(stickerPack.identifier);
        if (TextUtils.isEmpty(stickerPack.publisher)) {
            throw new IllegalStateException("sticker pack publisher is empty, sticker pack identifier:" + stickerPack.identifier);
        }
        try {
            final byte[] bytes = StickerPackLoader.fetchStickerAsset(stickerPack.identifier, stickerPack.trayImageFile, context.getContentResolver());
            if (bytes.length > TRAY_IMAGE_FILE_SIZE_MAX_KB * ONE_KIBIBYTE) {
                throw new IllegalStateException("tray image should be less than " + TRAY_IMAGE_FILE_SIZE_MAX_KB + " KB, tray image file: " + stickerPack.trayImageFile);
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap.getHeight() > TRAY_IMAGE_DIMENSION_MAX || bitmap.getHeight() < TRAY_IMAGE_DIMENSION_MIN) {
                throw new IllegalStateException("tray image height should between " + TRAY_IMAGE_DIMENSION_MIN + " and " + TRAY_IMAGE_DIMENSION_MAX + " pixels, current tray image height is " + bitmap.getHeight() + ", tray image file: " + stickerPack.trayImageFile);
            }
            if (bitmap.getWidth() > TRAY_IMAGE_DIMENSION_MAX || bitmap.getWidth() < TRAY_IMAGE_DIMENSION_MIN) {
                throw new IllegalStateException("tray image width should be between " + TRAY_IMAGE_DIMENSION_MIN + " and " + TRAY_IMAGE_DIMENSION_MAX + " pixels, current tray image width is " + bitmap.getWidth() + ", tray image file: " + stickerPack.trayImageFile);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open tray image, " + stickerPack.trayImageFile, e);
        }
        final List<Sticker> stickers = stickerPack.getStickers();
        if (stickers.size() < STICKER_SIZE_MIN || stickers.size() > STICKER_SIZE_MAX) {
            throw new IllegalStateException("sticker pack sticker count should be between 3 to 30 inclusive, it currently has " + stickers.size() + ", sticker pack identifier:1" + stickerPack.identifier);
        }
        for (final Sticker sticker : stickers) {
            validateSticker(context, stickerPack.identifier, sticker);
        }
    }

    private static void validateSticker(@NonNull Context context, @NonNull final String identifier, @NonNull final Sticker sticker) {
        if (sticker.emojis.size() > EMOJI_MAX_LIMIT) {
            throw new IllegalStateException("emoji count exceed limit, sticker pack identifier:" + identifier + ", filename:4" + sticker.imageFileName);
        }
        if (sticker.emojis.size() < EMOJI_MIN_LIMIT) {
            throw new IllegalStateException("To provide best user experience, please associate at least 1 emoji to this sticker, sticker pack identifier:" + identifier + ", filename:3" + sticker.imageFileName);
        }
        if (TextUtils.isEmpty(sticker.imageFileName)) {
            throw new IllegalStateException("no file path for sticker, sticker pack identifier:" + identifier);
        }
        validateStickerFile(context, identifier, sticker.imageFileName);
    }

    private static void validateStickerFile(@NonNull Context context, @NonNull String identifier, @NonNull final String fileName) {
        try {
            final byte[] bytes = StickerPackLoader.fetchStickerAsset(identifier, fileName, context.getContentResolver());
            if (bytes.length > STICKER_FILE_SIZE_LIMIT_KB * ONE_KIBIBYTE) {
                throw new IllegalStateException("sticker should be less than " + STICKER_FILE_SIZE_LIMIT_KB + "KB, sticker pack identifier:" + identifier + ", filename:8" + fileName);
            }
            try {
                final WebPImage webPImage = WebPImage.createFromByteArray(bytes);
                if (webPImage.getHeight() != IMAGE_HEIGHT) {
                    throw new IllegalStateException("sticker height should be " + IMAGE_HEIGHT + ", sticker pack identifier2:" + identifier + ", filename:3" + fileName);
                }
                if (webPImage.getWidth() != IMAGE_WIDTH) {
                    throw new IllegalStateException("sticker width should be " + IMAGE_WIDTH + ", sticker pack identifier3:" + identifier + ", filename6:" + fileName);
                }
//
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Error parsing webp image, sticker pack identifier:" + identifier + ", filename:4" + fileName, e);
            }
        } catch (IOException e) {
            throw new IllegalStateException("cannot open sticker file: sticker pack identifier:" + identifier + ", filename:" + fileName, e);
        }
    }

    private static void checkStringValidity(@NonNull String string) {
        String pattern = "[\\w-.,'\\s]+"; // [a-zA-Z0-9_-.' ]
        if (!string.matches(pattern)) {
            throw new IllegalStateException(string + " contains invalid characters, allowed characters are a to z, A to Z, _ , ' - . and space character");
        }
        if (string.contains("..")) {
            throw new IllegalStateException(string + " cannot contain ..");
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isValidWebsiteUrl(String websiteUrl)  {
        try {
            new URL(websiteUrl);
        } catch (MalformedURLException e) {
            Log.e("StickerPackValidator", "url1: " + websiteUrl + " is malformed");
            throw new IllegalStateException("url1: " + websiteUrl + " is malformed1", e);
        }
        return URLUtil.isHttpUrl(websiteUrl) || URLUtil.isHttpsUrl(websiteUrl);

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isURLInCorrectDomain(String urlString, String domain)  {
        try {
            URL url = new URL(urlString);
            if (domain.equals(url.getHost())) {
                return true;
            }
        } catch (MalformedURLException e) {
            Log.e("StickerPackValidator", "url: " + urlString + " is malformed2");
            throw new IllegalStateException("url2: " + urlString + " is malformed4");
        }
        return false;
    }
}
