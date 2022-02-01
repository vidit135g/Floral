package com.absolute.floral.adapter;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractRecyclerViewAdapter<T> extends RecyclerView.Adapter {

    private T data;

    private SelectorModeManager selectorManager;
    private boolean pick_photos;

    public AbstractRecyclerViewAdapter(boolean pick_photos) {
        this.pick_photos = pick_photos;
    }

    public AbstractRecyclerViewAdapter<T> setData(T data) {
        this.data = data;
        notifyDataSetChanged();
        return this;
    }

    public T getData() {
        return data;
    }

    public abstract boolean onBackPressed();

    public SelectorModeManager getSelectorManager() {
        return selectorManager;
    }

    public void setSelectorModeManager(SelectorModeManager selectorManager) {
        this.selectorManager = selectorManager;
    }

    public void saveInstanceState(Bundle state) {
        getSelectorManager().saveInstanceState(state);
    }

    public boolean pickPhotos() {
        return pick_photos;
    }
}
