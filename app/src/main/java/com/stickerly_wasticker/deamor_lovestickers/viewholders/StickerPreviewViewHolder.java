/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.stickerly_wasticker.deamor_lovestickers.viewholders;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.stickerly_wasticker.deamor_lovestickers.R;

public  class StickerPreviewViewHolder extends RecyclerView.ViewHolder {

    public  final SimpleDraweeView stickerPreviewView;

    public  StickerPreviewViewHolder(final View itemView) {
        super(itemView);
        stickerPreviewView = itemView.findViewById(R.id.sticker_preview);
    }
}