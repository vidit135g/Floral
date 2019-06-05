package com.theabsolutecompany.floral.adapter.album.viewHolder;

import android.view.View;
import android.widget.ImageView;

import com.theabsolutecompany.floral.R;
import com.theabsolutecompany.floral.data.models.AlbumItem;

public class RAWImageHolder extends AlbumItemHolder {

    public RAWImageHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void loadImage(final ImageView imageView, final AlbumItem albumItem) {
        super.loadImage(imageView, albumItem);
    }

    @Override
    int getIndicatorDrawableResource() {
        return R.drawable.raw_indicator;
    }
}
