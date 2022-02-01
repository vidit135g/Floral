package com.absolute.floral.adapter.main;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.absolute.floral.adapter.AbstractRecyclerViewAdapter;
import com.absolute.floral.adapter.SelectorModeManager;
import com.absolute.floral.adapter.album.AlbumAdapter;
import com.absolute.floral.data.Settings;
import com.absolute.floral.data.models.Album;
import com.absolute.floral.data.provider.MediaProvider;
import com.absolute.floral.util.SortUtil;

public class NoFolderRecyclerViewAdapter extends AbstractRecyclerViewAdapter<ArrayList<Album>>
        implements SelectorModeManager.Callback {

    private Context context;

    private AlbumAdapter albumAdapter;
    private SelectorModeManager.Callback callback;

    public NoFolderRecyclerViewAdapter(SelectorModeManager.Callback callback, RecyclerView recyclerView, boolean pick_photos) {
        super(pick_photos);
        context = recyclerView.getContext();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        albumAdapter = new AlbumAdapter(this, recyclerView, new Album(), pick_photos);
        this.callback = callback;
    }

    @Override
    public AbstractRecyclerViewAdapter<ArrayList<Album>> setData(ArrayList<Album> data) {
        if (data == null) {
            return this;
        }
        Album album = new Album().setPath(MediaProvider.SINGLE_FOLDER_PATH);
        for (int i = 0; i < data.size(); i++) {
            album.getAlbumItems().addAll(data.get(i).getAlbumItems());
        }
        int sortBy = Settings.getInstance(context).sortAlbumsBy();
        SortUtil.sort(album.getAlbumItems(), sortBy);
        albumAdapter.setData(album);
        notifyDataSetChanged();
        return this;
    }

    @Override
    public ArrayList<Album> getData() {
        ArrayList<Album> albums = new ArrayList<>();
        albums.add(albumAdapter.getData());
        return albums;
    }

    @Override
    public int getItemViewType(int position) {
        return albumAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return albumAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        albumAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return albumAdapter.getItemCount();
    }

    @Override
    public boolean onBackPressed() {
        return albumAdapter.onBackPressed();
    }

    @Override
    public SelectorModeManager getSelectorManager() {
        return albumAdapter.getSelectorManager();
    }

    @Override
    public void setSelectorModeManager(SelectorModeManager selectorManager) {
        albumAdapter.setSelectorModeManager(selectorManager);
    }

    @Override
    public void onSelectorModeEnter() {
        if (callback != null) {
            callback.onSelectorModeEnter();
        }
    }

    @Override
    public void onSelectorModeExit() {
        if (callback != null) {
            callback.onSelectorModeExit();
        }
    }

    @Override
    public void onItemSelected(int selectedItemCount) {
        if (callback != null) {
            callback.onItemSelected(selectedItemCount);
        }
    }
}
