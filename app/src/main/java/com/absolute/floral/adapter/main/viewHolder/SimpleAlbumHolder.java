package com.absolute.floral.adapter.main.viewHolder;

import android.view.View;
import android.widget.ImageView;

import com.absolute.floral.R;
import com.absolute.floral.data.models.Album;
import com.absolute.floral.ui.widget.ParallaxImageView;

public class SimpleAlbumHolder extends AlbumHolder {

    public SimpleAlbumHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setAlbum(Album album) {
        super.setAlbum(album);
        final ImageView image = itemView.findViewById(R.id.image);
        if (image instanceof ParallaxImageView) {
            ((ParallaxImageView) image).setParallaxTranslation();
        }
        loadImage(image);
    }
}
