package com.absolute.floral.adapter.album.viewHolder;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.absolute.floral.R;
import com.absolute.floral.data.models.AlbumItem;
import com.absolute.floral.util.Util;
import com.absolute.floral.util.animators.ColorFade;

public abstract class AlbumItemHolder extends RecyclerView.ViewHolder {

    public AlbumItem albumItem;
    private boolean selected = false;
    private Drawable selectorOverlay;

    AlbumItemHolder(View itemView) {
        super(itemView);
        addIndicatorDrawable(itemView);
    }

    public AlbumItem getAlbumItem() {
        return albumItem;
    }

    public void setAlbumItem(AlbumItem albumItem) {
        if (this.albumItem == albumItem) {
            return;
        }

        this.albumItem = albumItem;
        ImageView imageView = itemView.findViewById(R.id.image);
        loadImage(imageView, albumItem);
    }

    private void addIndicatorDrawable(View itemView) {
        int indicatorRes = getIndicatorDrawableResource();
        if (indicatorRes != -1) {
            final ImageView imageView = itemView.findViewById(R.id.image);
            final Drawable indicatorOverlay
                    = ContextCompat.getDrawable(itemView.getContext(), indicatorRes);
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    final int overlayPadding = (int) (imageView.getWidth() * 0.05f);
                    final int overlayDimens = (int) (imageView.getWidth() * 0.3f);
                    indicatorOverlay.setBounds(
                            imageView.getWidth() - overlayDimens - overlayPadding,
                            imageView.getHeight() - overlayDimens,
                            imageView.getWidth() - overlayPadding,
                            imageView.getHeight());
                    imageView.getOverlay().add(indicatorOverlay);
                }
            });
        }
    }

    int getIndicatorDrawableResource() {
        return -1;
    }

    public void loadImage(final ImageView imageView, final AlbumItem albumItem) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(albumItem.getPath())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        albumItem.error = true;
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        if (!albumItem.hasFadedIn) {
                            fadeIn();
                        } else {
                            imageView.clearColorFilter();
                        }
                        return false;
                    }
                })
                .apply(albumItem.getGlideRequestOptions(imageView.getContext()))
                .into(imageView);
    }

    void fadeIn() {
        albumItem.hasFadedIn = true;
        ColorFade.fadeSaturation((ImageView) itemView.findViewById(R.id.image));
    }

    public void setSelected(boolean selected) {
        boolean animate = this.selected != selected;
        this.selected = selected;
        if (animate) {
            animateSelected();
        }
    }

    private void animateSelected() {
        final View imageView = itemView.findViewById(R.id.image);

        float scale = selected ? 0.8f : 1.0f;
        imageView.animate()
                .scaleX(scale)
                .scaleY(scale)
                .start();

        if (selectorOverlay == null) {
            selectorOverlay = Util.getAlbumItemSelectorOverlay(imageView.getContext());
        }
        if (selected) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.getOverlay().remove(selectorOverlay);
                    selectorOverlay.setBounds(0, 0,
                            imageView.getWidth(),
                            imageView.getHeight());
                    imageView.getOverlay().add(selectorOverlay);
                }
            });
        } else {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.getOverlay().remove(selectorOverlay);
                }
            });
        }
    }
}
