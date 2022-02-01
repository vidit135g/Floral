package com.absolute.floral.adapter.item;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.absolute.floral.adapter.item.viewHolder.GifViewHolder;
import com.absolute.floral.adapter.item.viewHolder.PhotoViewHolder;
import com.absolute.floral.adapter.item.viewHolder.RAWImageViewHolder;
import com.absolute.floral.adapter.item.viewHolder.VideoViewHolder;
import com.absolute.floral.adapter.item.viewHolder.ViewHolder;
import com.absolute.floral.data.models.Album;
import com.absolute.floral.data.models.AlbumItem;
import com.absolute.floral.data.models.Gif;
import com.absolute.floral.data.models.RAWImage;
import com.absolute.floral.data.models.Video;
import com.absolute.floral.ui.ItemActivity;

public class ItemAdapter extends PagerAdapter {

    private Album album;

    private ArrayList<ViewHolder> viewHolders;

    private ItemActivity.ViewPagerOnInstantiateItemCallback callback;

    public ItemAdapter(Album album) {
        this.album = album;
        this.viewHolders = new ArrayList<>();
    }

    public void addOnInstantiateItemCallback(
            ItemActivity.ViewPagerOnInstantiateItemCallback callback) {
        this.callback = callback;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    public int getCount() {
        return album.getAlbumItems().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        AlbumItem albumItem = album.getAlbumItems().get(position);

        ViewHolder viewHolder;
        if (albumItem instanceof Video) {
            viewHolder = new VideoViewHolder(albumItem, position);
        } else if (albumItem instanceof Gif) {
            viewHolder = new GifViewHolder(albumItem, position);
        } else if (albumItem instanceof RAWImage) {
            viewHolder = new RAWImageViewHolder(albumItem, position);
        } else {
            viewHolder = new PhotoViewHolder(albumItem, position);
        }
        viewHolders.add(viewHolder);

        View v = viewHolder.getView(container);
        container.addView(v);

        if (callback != null) {
            boolean b = callback.onInstantiateItem(viewHolder);
            if (!b) {
                callback = null;
            }
        }

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        ViewHolder viewHolder = findViewHolderByPosition(position);
        if (viewHolder != null) {
            viewHolder.onDestroy();
            viewHolders.remove(viewHolder);
        }
    }

    private ViewHolder findViewHolderByPosition(int position) {
        for (int i = 0; i < viewHolders.size(); i++) {
            if (position == viewHolders.get(i).getPosition()) {
                return viewHolders.get(i);
            }
        }
        return null;
    }

    public ViewHolder findViewHolderByTag(String tag) {
        for (int i = 0; i < viewHolders.size(); i++) {
            if (viewHolders.get(i).getTag().equals(tag)) {
                return viewHolders.get(i);
            }
        }
        return null;
    }

    //for deleting items from the list
    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
