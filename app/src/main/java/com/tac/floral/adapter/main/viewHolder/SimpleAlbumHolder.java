package com.theabsolutecompany.floral.adapter.main.viewHolder;

import android.view.View;
import android.widget.ImageView;

import com.theabsolutecompany.floral.R;
import com.theabsolutecompany.floral.data.models.Album;
import com.theabsolutecompany.floral.ui.widget.ParallaxImageView;

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
