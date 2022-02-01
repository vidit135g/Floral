package com.absolute.floral.adapter.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.absolute.floral.R;
import com.absolute.floral.adapter.AbstractRecyclerViewAdapter;
import com.absolute.floral.styles.Style;
import com.absolute.floral.themes.Theme;
import com.absolute.floral.adapter.SelectorModeManager;
import com.absolute.floral.adapter.main.viewHolder.AlbumHolder;
import com.absolute.floral.adapter.main.viewHolder.NestedRecyclerViewAlbumHolder;
import com.absolute.floral.data.models.Album;
import com.absolute.floral.data.Settings;
import com.absolute.floral.ui.AlbumActivity;
import com.absolute.floral.ui.MainActivity;
import com.absolute.floral.ui.ThemeableActivity;

public class MainAdapter extends AbstractRecyclerViewAdapter<ArrayList<Album>> {

    private Style style;

    public MainAdapter(Context context, boolean pick_photos) {
        super(pick_photos);

        Settings settings = Settings.getInstance(context);

        style = settings.getStyleInstance(context, pick_photos);

        setSelectorModeManager(new SelectorModeManager());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        viewHolder = style.createViewHolderInstance(parent);
        if (viewHolder instanceof NestedRecyclerViewAlbumHolder) {
            ((NestedRecyclerViewAlbumHolder) viewHolder).setSelectorModeManager(getSelectorManager());
        }

        Context context = viewHolder.itemView.getContext();
        Theme theme = Settings.getInstance(context).getThemeInstance(context);
        ThemeableActivity.checkTags((ViewGroup) viewHolder.itemView, theme);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Album album = getData().get(position);

        ((AlbumHolder) holder).setAlbum(album);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), AlbumActivity.class);

                //intent.putExtra(AlbumActivity.ALBUM, album);
                intent.putExtra(AlbumActivity.ALBUM_PATH, album.getPath());

                if (pickPhotos()) {
                    Context c = holder.itemView.getContext();
                    boolean allowMultiple = false;
                    if (c instanceof Activity) {
                        Activity a = (Activity) c;
                        allowMultiple = a.getIntent()
                                .getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    }
                    intent.setAction(MainActivity.PICK_PHOTOS);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple);
                } else {
                    intent.setAction(AlbumActivity.VIEW_ALBUM);
                }

                ActivityOptionsCompat options;
                Activity context = (Activity) holder.itemView.getContext();
                if (!pickPhotos()) {
                    //noinspection unchecked
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(context);
                    context.startActivityForResult(intent,
                            MainActivity.REFRESH_PHOTOS_REQUEST_CODE, options.toBundle());
                } else {
                    View toolbar = context.findViewById(R.id.toolbar);
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context, toolbar, context.getString(R.string.toolbar_transition_name));
                    context.startActivityForResult(intent,
                            MainActivity.PICK_PHOTOS_REQUEST_CODE, options.toBundle());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return getData() != null ? getData().size() : 0;
    }

    public boolean onBackPressed() {
        return getSelectorManager().onBackPressed();
    }

    @Override
    public void setSelectorModeManager(SelectorModeManager selectorManager) {
        super.setSelectorModeManager(selectorManager);
        notifyItemRangeChanged(0, getItemCount());
    }
}
