package com.absolute.floral.adapter.album.viewHolder;

import android.view.View;
import android.widget.ImageView;

import com.absolute.floral.R;
import com.absolute.floral.data.models.AlbumItem;

public class VideoViewHolder extends AlbumItemHolder {

    public VideoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    int getIndicatorDrawableResource() {
        return R.drawable.video_indicator;
    }

    @Override
    public void loadImage(final ImageView imageView, final AlbumItem albumItem) {
        super.loadImage(imageView, albumItem);
    }
}
