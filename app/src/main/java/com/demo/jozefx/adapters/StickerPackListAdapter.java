/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.demo.jozefx.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.demo.jozefx.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.demo.jozefx.R;
import com.demo.jozefx.StickerPack;
import com.demo.jozefx.activities.StickerPackDetailsActivity;
import com.demo.jozefx.StickerPackLoader;
import com.demo.jozefx.databinding.ItemPackBinding;
import com.demo.jozefx.utils.AdsManager;
import com.solodroid.ads.sdk.format.NativeAdViewHolder;

import java.util.List;

import static com.demo.jozefx.activities.StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA;

public class StickerPackListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    private List<StickerPack> stickerPacks;
    @NonNull
    private final OnAddButtonClickedListener onAddButtonClickedListener;
    private int maxNumberOfStickersInARow;
    private int minMarginBetweenImages;
    private static final int ITEM_VIEW = 0;
    private static final int AD_VIEW = 1;
    private final int counter = 1;
    private Context context;


    public StickerPackListAdapter(@NonNull List<StickerPack> stickerPacks, @NonNull OnAddButtonClickedListener onAddButtonClickedListener) {
        this.stickerPacks = stickerPacks;
        this.onAddButtonClickedListener = onAddButtonClickedListener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from((Activity) context);
        if (viewType == ITEM_VIEW) {
            View view = layoutInflater.inflate(R.layout.item_pack, parent, false);
            return new StickerListViewHolder(view);
        } else if (viewType == AD_VIEW) {
            View v;
            switch (Constants.nativeStyle) {
                case "news":
                    v = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_news, parent, false);
                    break;
                case "radio":
                    v = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_radio, parent, false);
                    break;
                case "video_small":
                    v = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_video_small, parent, false);
                    break;
                case "video_large":
                    v = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_video_large, parent, false);
                    break;
                default:
                    v = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_medium, parent, false);
                    break;
            }
            return new NativeAdViewHolder(v);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int index) {
        if (holder.getItemViewType() == ITEM_VIEW) {
            int pos = index - Math.round(index / Constants.itemFeed);
            ((StickerListViewHolder) holder).bind(stickerPacks.get(pos));
        } else if (holder.getItemViewType() == AD_VIEW) {
//            ((NativeViewHolder) holder).bindAdData();
            final NativeAdViewHolder vItem = (NativeAdViewHolder) holder;
            vItem.loadNativeAd(context,
                    Constants.ad_status,
                    1,
                    Constants.ad_network,
                    Constants.back_up,
                    Constants.adMobNativeId,
                    Constants.adMobNativeId,
                    Constants.fanNativeId,
                    Constants.maxNativeId,
                    Constants.maxBannerId,
                    false,
                    true,
                    "default",
                    R.color.colorNativeBackgroundLight,
                    R.color.colorNativeBackgroundDark
                    );


        }

    }

    @Override
    public int getItemCount() {
        if (stickerPacks.size() > 0) {
            return stickerPacks.size() + Math.round(stickerPacks.size() / Constants.itemFeed) + 1;
        };
        return stickerPacks.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % Constants.itemFeed == 0) {
            return AD_VIEW;
        }
        return ITEM_VIEW;
    }

    public class StickerListViewHolder extends RecyclerView.ViewHolder {
        ItemPackBinding itemPackBinding;

        StickerListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemPackBinding = ItemPackBinding.bind(itemView);
        }

        void bind(StickerPack pack) {
            final Context context = itemView.getContext();

            itemPackBinding.stickerPackPublisher.setText(pack.publisher);
            itemPackBinding.stickerPackFilesize.setText(Formatter.formatShortFileSize(context, pack.getTotalSize()));

            itemPackBinding.packTryImage.setImageURI(StickerPackLoader.getStickerAssetUri(pack.identifier, pack.trayImageFile));
            itemPackBinding.itemPackCreated.setText(pack.username);

            itemPackBinding.stickerPackTitle.setText(pack.name);
            itemPackBinding.itemPackContainer.setOnClickListener(view -> {
                    AdsManager.getInstance().showInterstitialAd(() -> {
                        AdsManager.getInstance().detachbanner();
                        Constants.counter++;
                        Intent intent = new Intent(view.getContext(), StickerPackDetailsActivity.class);
                        intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, true);
                        intent.putExtra(EXTRA_STICKER_PACK_DATA, pack);
                        view.getContext().startActivity(intent);
                    });
            });
            itemPackBinding.stickerPacksListItemImageList.removeAllViews();
            //if this sticker pack contains less stickers than the max, then take the smaller size.
            int actualNumberOfStickersToShow = Math.min(maxNumberOfStickersInARow, pack.getStickers().size());
            for (int i = 0; i < actualNumberOfStickersToShow - 1; i++) {
                final SimpleDraweeView rowImage = (SimpleDraweeView) LayoutInflater.from(context).inflate(R.layout.sticker_packs_list_image_item, itemPackBinding.stickerPacksListItemImageList, false);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.loading);
                Glide.with(context).load(
                                StickerPackLoader.getStickerAssetUri(pack.identifier, pack.getStickers().get(i).imageFileName)
                        )
                        .apply(requestOptions)
                        .into(rowImage);

                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rowImage.getLayoutParams();
                final int marginBetweenImages = minMarginBetweenImages - lp.leftMargin - lp.rightMargin;
                if (i != actualNumberOfStickersToShow - 1 && marginBetweenImages > 0) { //do not set the margin for the last image
                    lp.setMargins(lp.leftMargin + 20, lp.topMargin, lp.rightMargin + marginBetweenImages, lp.bottomMargin);
                    rowImage.setLayoutParams(lp);
                }
                itemPackBinding.stickerPacksListItemImageList.addView(rowImage);
            }
        }
    }

    public void setImageRowSpec(int maxNumberOfStickersInARow, int minMarginBetweenImages) {
        this.minMarginBetweenImages = minMarginBetweenImages;
        if (this.maxNumberOfStickersInARow != maxNumberOfStickersInARow) {
            this.maxNumberOfStickersInARow = maxNumberOfStickersInARow;
            notifyDataSetChanged();
        }
    }

    public void setMaxNumberOfStickersInARow(int maxNumberOfStickersInARow) {
        if (this.maxNumberOfStickersInARow != maxNumberOfStickersInARow) {
            this.maxNumberOfStickersInARow = maxNumberOfStickersInARow;
            notifyDataSetChanged();
        }
    }

    public void setStickerPackList(List<StickerPack> stickerPackList) {
        this.stickerPacks = stickerPackList;
    }

    public interface OnAddButtonClickedListener {
        void onAddButtonClicked(StickerPack stickerPack);
    }
}


