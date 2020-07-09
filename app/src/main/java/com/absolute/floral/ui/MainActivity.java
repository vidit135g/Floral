package com.absolute.floral.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.absolute.floral.R;
import com.absolute.floral.adapter.AbstractRecyclerViewAdapter;
import com.absolute.floral.adapter.SelectorModeManager;
import com.absolute.floral.adapter.main.MainAdapter;
import com.absolute.floral.adapter.main.NoFolderRecyclerViewAdapter;
import com.absolute.floral.adapter.main.viewHolder.NestedRecyclerViewAlbumHolder;
import com.absolute.floral.data.ContentObserver;
import com.absolute.floral.data.Settings;
import com.absolute.floral.data.fileOperations.FileOperation;
import com.absolute.floral.data.models.Album;
import com.absolute.floral.data.provider.MediaProvider;
import com.absolute.floral.styles.NestedRecyclerView;
import com.absolute.floral.styles.Style;
import com.absolute.floral.themes.Theme;
import com.absolute.floral.ui.widget.FastScrollerRecyclerView;
import com.absolute.floral.ui.widget.GridMarginDecoration;
import com.absolute.floral.util.SortUtil;
import com.absolute.floral.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends ThemeableActivity implements CheckRefreshClickListener{

    public static final String REFRESH_MEDIA = "REFRESH_MEDIA";
    public static final String PICK_PHOTOS = "PICK_PHOTOS";
    public static final String RESORT = "RESORT";
    public static final int PICK_PHOTOS_REQUEST_CODE = 6;
    public static final int REFRESH_PHOTOS_REQUEST_CODE = 7;
    public static final int REMOVABLE_STORAGE_PERMISSION_REQUEST_CODE = 8;
    public static final int SETTINGS_REQUEST_CODE = 9;

    //needed for sharedElement-Transition in Nested RecyclerView Style
    private NestedRecyclerViewAlbumHolder sharedElementViewHolder;
    private final SharedElementCallback mCallback
            = new SharedElementCallback() {
        @Override
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (sharedElementViewHolder == null) {
                return;
            }

            if (sharedElementViewHolder.sharedElementReturnPosition != -1
                    && sharedElementViewHolder.sharedElementReturnPosition <
                    sharedElementViewHolder.getAlbum().getAlbumItems().size()) {
                String newTransitionName = sharedElementViewHolder.getAlbum().getAlbumItems()
                        .get(sharedElementViewHolder.sharedElementReturnPosition).getPath();
                View layout = sharedElementViewHolder.nestedRecyclerView.findViewWithTag(newTransitionName);
                View newSharedElement = layout != null ? layout.findViewById(R.id.image) : null;
                if (newSharedElement != null) {
                    names.clear();
                    names.add(newTransitionName);
                    sharedElements.clear();
                    sharedElements.put(newTransitionName, newSharedElement);
                }
                sharedElementViewHolder.sharedElementReturnPosition = -1;
            } else {
                View v = sharedElementViewHolder.itemView.getRootView();
                View navigationBar = v.findViewById(android.R.id.navigationBarBackground);
                View statusBar = v.findViewById(android.R.id.statusBarBackground);
                if (navigationBar != null) {
                    names.add(navigationBar.getTransitionName());
                    sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                }
                if (statusBar != null) {
                    names.add(statusBar.getTransitionName());
                    sharedElements.put(statusBar.getTransitionName(), statusBar);
                }
            }
        }
    };

    private  Snackbar snackbar;
    private ArrayList<Album> albums;

    private RecyclerView recyclerView;
    private AbstractRecyclerViewAdapter<ArrayList<Album>> recyclerViewAdapter;


    private MediaProvider mediaProvider;

    private ContentObserver observer;

    private boolean hiddenFolders;

    private boolean pick_photos;


    private SwipeRefreshLayout mSwipeRefreshLayout;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshPhotos();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView();
        }

        pick_photos = getIntent().getAction() != null && getIntent().getAction().equals(PICK_PHOTOS);
        boolean allowMultiple = getIntent().getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

        final Settings settings = Settings.getInstance(this);
        hiddenFolders = settings.getHiddenFolders();

        //load media
        albums = MediaProvider.getAlbumsWithVirtualDirectories(this);
        if (albums == null) {
            albums = new ArrayList<>();
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.pinbk);
        toolbar.setBackgroundColor(!pick_photos ? toolbarColor : accentColor);
        toolbar.setTitleTextColor(!pick_photos ? textColorPrimary : accentTextColor);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/google.ttf");
        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                if(tv.getText().equals(toolbar.getTitle())){
                    tv.setTypeface(font);
                    break;
                }
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (pick_photos) {
            if (actionBar != null) {
                actionBar.setTitle(allowMultiple ? getString(R.string.pick_photos) : getString(R.string.pick_photo));
            }
            toolbar.setActivated(true);
            toolbar.setNavigationIcon(R.drawable.ic_clear_white);
            Drawable navIcon = toolbar.getNavigationIcon();
            if (navIcon != null) {
                navIcon = DrawableCompat.wrap(navIcon);
                DrawableCompat.setTint(navIcon.mutate(), accentTextColor);
                toolbar.setNavigationIcon(navIcon);
            }
            Util.colorToolbarOverflowMenuIcon(toolbar, accentTextColor);
            if (theme.darkStatusBarIconsInSelectorMode()) {
                Util.setDarkStatusBarIcons(findViewById(R.id.root_view));
            }
        } else {
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.toolbar_title));
            }
        }

        mSwipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        SelectorModeManager.Callback callback = new SelectorModeManager.SimpleCallback() {
            @Override
            public void onSelectorModeEnter() {
                super.onSelectorModeEnter();
                showAndHideFab(false);
            }

            @Override
            public void onSelectorModeExit() {
                super.onSelectorModeExit();
                showAndHideFab(true);
            }
        };
        int spanCount, spacing;
        if (settings.noFolderMode()) {
            spanCount = settings.getColumnCount(this);
            spacing = (int) getResources().getDimension(R.dimen.album_grid_spacing) / 2;
            recyclerView.addItemDecoration(new GridMarginDecoration(spacing + spacing));
            recyclerViewAdapter = new NoFolderRecyclerViewAdapter(callback, recyclerView, pick_photos)
                    .setData(albums);
        } else {
            Style style = settings.getStyleInstance(this, pick_photos);
            spanCount = style.getColumnCount(this);
            spacing = (int) style.getGridSpacing(this);
            recyclerViewAdapter = new MainAdapter(this, pick_photos).setData(albums);
            recyclerViewAdapter.getSelectorManager().addCallback(callback);
        }
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        if (recyclerView instanceof FastScrollerRecyclerView) {
            ((FastScrollerRecyclerView) recyclerView).addOuterGridSpacing(spacing);
        }

        //disable default change animation
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        //restore Selector mode, when needed
        if (savedInstanceState != null) {
            SelectorModeManager manager = new SelectorModeManager(savedInstanceState);
            recyclerViewAdapter.setSelectorModeManager(manager);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (pick_photos) {
                    return;
                }


            }
        });

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> fabClicked(view));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable d = ContextCompat.getDrawable(this,
                    R.drawable.camera1);
            fab.setImageDrawable(d);
        } else {
            fab.setImageResource(R.drawable.camera1);
        }
        Drawable d = fab.getDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        } else {
            d = DrawableCompat.wrap(d);
        }
        fab.setImageDrawable(d);

        if (pick_photos || !settings.getCameraShortcut()) {
            fab.setVisibility(View.GONE);
        }

        //setting window insets manually
        final ViewGroup rootView = findViewById(R.id.root_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            rootView.setOnApplyWindowInsetsListener((view, insets) -> {
                // clear this listener so insets aren't re-applied
                rootView.setOnApplyWindowInsetsListener(null);
                Log.d("MainActivity", "onApplyWindowInsets()"
                        + "[" + insets.getSystemWindowInsetLeft() + ", " +
                        insets.getSystemWindowInsetTop() + ", " +
                        insets.getSystemWindowInsetRight() + ", " +
                        insets.getSystemWindowInsetBottom() + "]");

                toolbar.setPadding(toolbar.getPaddingStart(),
                        toolbar.getPaddingTop() + insets.getSystemWindowInsetTop(),
                        toolbar.getPaddingEnd(),
                        toolbar.getPaddingBottom());

                ViewGroup.MarginLayoutParams toolbarParams
                        = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                toolbarParams.leftMargin = insets.getSystemWindowInsetLeft();
                toolbarParams.rightMargin = insets.getSystemWindowInsetRight();
                toolbar.setLayoutParams(toolbarParams);

                recyclerView.setPadding(recyclerView.getPaddingStart() + insets.getSystemWindowInsetLeft(),
                        recyclerView.getPaddingTop() + insets.getSystemWindowInsetTop(),
                        recyclerView.getPaddingEnd() + insets.getSystemWindowInsetRight(),
                        recyclerView.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                fab.setTranslationY(-insets.getSystemWindowInsetBottom());
                fab.setTranslationX(-insets.getSystemWindowInsetRight());

                return insets.consumeSystemWindowInsets();
            });
        } else {
            rootView.getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    // hacky way of getting window insets on pre-Lollipop
                                    // somewhat works...
                                    int[] screenSize = Util.getScreenSize(MainActivity.this);

                                    int[] windowInsets = new int[]{
                                            Math.abs(screenSize[0] - rootView.getLeft()),
                                            Math.abs(screenSize[1] - rootView.getTop()),
                                            Math.abs(screenSize[2] - rootView.getRight()),
                                            Math.abs(screenSize[3] - rootView.getBottom())};

                                    toolbar.setPadding(toolbar.getPaddingStart(),
                                            toolbar.getPaddingTop() + windowInsets[1],
                                            toolbar.getPaddingEnd(),
                                            toolbar.getPaddingBottom());

                                    ViewGroup.MarginLayoutParams toolbarParams
                                            = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                                    toolbarParams.leftMargin += windowInsets[0];
                                    toolbarParams.rightMargin += windowInsets[2];
                                    toolbar.setLayoutParams(toolbarParams);

                                    recyclerView.setPadding(recyclerView.getPaddingStart() + windowInsets[0],
                                            recyclerView.getPaddingTop() + windowInsets[1],
                                            recyclerView.getPaddingEnd() + windowInsets[2],
                                            recyclerView.getPaddingBottom() + windowInsets[3]);

                                    fab.setTranslationX(-windowInsets[2]);
                                    fab.setTranslationY(-windowInsets[3]);
                                }
                            });
        }

        //needed for transparent statusBar

        int c1 = getResources().getColor(R.color.gblue);
        int c2 = getResources().getColor(R.color.gred);
        int c3 = getResources().getColor(R.color.ggreen);
        int c4=getResources().getColor(R.color.gyellow);
        mSwipeRefreshLayout.setColorSchemeColors(c1,c2,c3,c4);
        mSwipeRefreshLayout.setOnRefreshListener(() -> refreshPhotos());
        setSystemUiFlags();
    }


    @Override
    public void onActivityReenter(final int resultCode, Intent intent) {
        super.onActivityReenter(resultCode, intent);

        if (intent.getAction() != null
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && intent.getAction().equals(ItemActivity.SHARED_ELEMENT_RETURN_TRANSITION)
                && Settings.getInstance(this).getStyleInstance(this, pick_photos) instanceof NestedRecyclerView) {
            //handle shared-element transition, for nested nestedRecyclerView style
            Bundle tmpReenterState = new Bundle(intent.getExtras());
            if (tmpReenterState.containsKey(AlbumActivity.ALBUM_PATH)
                    && tmpReenterState.containsKey(AlbumActivity.EXTRA_CURRENT_ALBUM_POSITION)) {

                String albumPath = tmpReenterState.getString(AlbumActivity.ALBUM_PATH);
                Log.d("MainActivity", "albumPath: " + albumPath);
                final int sharedElementReturnPosition = tmpReenterState.getInt(AlbumActivity.EXTRA_CURRENT_ALBUM_POSITION);
                int index = -1;
                ArrayList<Album> albums = MediaProvider.getAlbumsWithVirtualDirectories(this);
                for (int i = 0; i < albums.size(); i++) {
                    Log.d("MainActivity", "albums: " + albums.get(i).getPath());
                    if (albums.get(i).getPath().equals(albumPath)) {
                        index = i;
                        break;
                    }
                }

                Log.d("MainActivity", "index: " + index);

                if (index == -1) {
                    return;
                }

                //postponing transition until sharedElement is laid out
                postponeEnterTransition();
                setExitSharedElementCallback(mCallback);
                final NestedRecyclerViewAlbumHolder
                        .StartSharedElementTransitionCallback callback =
                        () -> {
                            //sharedElement is laid out --> start transition
                            MainActivity.this.startPostponedEnterTransition();
                        };

                final int finalIndex = index;
                recyclerView.scrollToPosition(index);
                //wait until ViewHolder is laid out
                recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onLayoutChange(View v, int l, int t, int r, int b,
                                               int oL, int oT, int oR, int oB) {
                        RecyclerView.ViewHolder viewHolder
                                = recyclerView.findViewHolderForAdapterPosition(finalIndex);

                        if (viewHolder != null) {
                            recyclerView.removeOnLayoutChangeListener(this);
                        } else {
                            //viewHolder hasn't been laid out yet --> wait
                            recyclerView.scrollToPosition(finalIndex);
                        }

                        if (viewHolder instanceof NestedRecyclerViewAlbumHolder) {
                            //found ViewHolder
                            sharedElementViewHolder = (NestedRecyclerViewAlbumHolder) viewHolder;
                            ((NestedRecyclerViewAlbumHolder) viewHolder)
                                    .onSharedElement(sharedElementReturnPosition, callback);
                        }
                    }
                });
            }
        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent == null || intent.getAction() == null) {
            return;
        }

        switch (intent.getAction()) {
            case REFRESH_MEDIA:
                refreshPhotos();
                break;
            case RESORT:
                resortAlbums();
                break;
            default:
                break;
        }
    }

    public void refreshPhotos() {
        if (mediaProvider != null) {
            mediaProvider.onDestroy();
            mediaProvider = null;
        }


        final MediaProvider.OnMediaLoadedCallback callback
                = new MediaProvider.OnMediaLoadedCallback() {
            @Override
            public void onMediaLoaded(final ArrayList<Album> albums) {
                final ArrayList<Album> albumsWithVirtualDirs =
                        MediaProvider.getAlbumsWithVirtualDirectories(MainActivity.this);
                if (albums != null) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.albums = albumsWithVirtualDirs;
                            recyclerViewAdapter.setData(albumsWithVirtualDirs);

                            if (mediaProvider != null) {
                                mediaProvider.onDestroy();
                            }
                            mediaProvider = null;
                            new Handler().postDelayed(new Runnable() {
                                @Override public void run() {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            }, 6000);

                        }
                    });
                }
            }

            @Override
            public void timeout() {
                //handle timeout
                mSwipeRefreshLayout.setRefreshing(false);

                if (mediaProvider != null) {
                    mediaProvider.onDestroy();
                }
                mediaProvider = null;
            }

            @Override
            public void needPermission() {
            }
        };

        mediaProvider = new MediaProvider(this);
        mediaProvider.loadAlbums(MainActivity.this, hiddenFolders, callback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshPhotos();
    }

    @Override
    public void OnShareClick() {
        shareApp(this);
    }

    public static void shareApp(Context context) {
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check this cool Gallery app at: https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("unchecked")
      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
           case android.R.id.home:
               final Intent intent=new Intent(MainActivity.this,PinningActivity.class);
               startActivity(intent);
               break;
           case R.id.bottommenu:
               ShowRoundDialogFragment showRoundDialogFragment =
                       ShowRoundDialogFragment.newInstance();
               showRoundDialogFragment.show(getSupportFragmentManager(),
                       "add_menu_fragment");
               break;
               default:
                   break;
       }
        return super.onOptionsItemSelected(item);
      }

    private void resortAlbums() {
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.root_view),
                "Sorting...", Snackbar.LENGTH_INDEFINITE);
        Util.showSnackbar(snackbar);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //SortUtil.sortAlbums(MainActivity.this, MediaProvider.getAlbums());
                final ArrayList<Album> albums = MediaProvider.getAlbumsWithVirtualDirectories(MainActivity.this);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.albums = albums;
                        recyclerViewAdapter.setData(albums);
                        snackbar.dismiss();
                    }
                });
            }
        });
    }

    public void fabClicked(View v) {
        if (v instanceof FloatingActionButton) {
            FloatingActionButton fab = (FloatingActionButton) v;
            Drawable drawable = fab.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent();
                i.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        }, (int) (500 * Util.getAnimatorSpeed(this)));
    }

    public void showAndHideFab(boolean show) {
        if (pick_photos || !Settings.getInstance(this).getCameraShortcut()) {
            return;
        }

        findViewById(R.id.fab).animate()
                .scaleX(show ? 1.0f : 0.0f)
                .scaleY(show ? 1.0f : 0.0f)
                .alpha(show ? 1.0f : 0.0f)
                .setDuration(250)
                .start();
    }

    @Override
    public void onPermissionGranted() {
        super.onPermissionGranted();
        refreshPhotos();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_PHOTOS_REQUEST_CODE:
                if (resultCode != RESULT_CANCELED) {
                    setResult(RESULT_OK, data);
                    this.finish();
                }
                break;
            case REFRESH_PHOTOS_REQUEST_CODE:
                if (data != null
                        && data.getAction() != null
                        && (data.getAction().equals(AlbumActivity.ALBUM_ITEM_REMOVED)
                        || data.getAction().equals(REFRESH_MEDIA))) {
                    refreshPhotos();
                }
                break;
            case AlbumActivity.FILE_OP_DIALOG_REQUEST:
                if (resultCode == RESULT_OK) {
                    refreshPhotos();
                }
                break;
            case SETTINGS_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // StatusBar is no longer translucent after recreate() + 2x sharedElementTransition in NestedRecyclerView-Style
                    //this.recreate();
                    Intent intent = getIntent();
                    this.finish();
                    startActivity(intent);
                }
                break;
            case ItemActivity.VIEW_IMAGE:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        observer = new ContentObserver(new Handler());
        observer.setListener(new ContentObserver.Listener() {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                Log.d("MainActivity", "onChange()");
                MediaProvider.dataChanged = true;
                //observer.unregister(MainActivity.this);
                //observer = null;
            }
        });
        observer.register(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //not able to save albums in Bundle, --> TransactionTooLargeException
        //outState.putParcelableArrayList(ALBUMS, albums);

        recyclerViewAdapter.saveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (!recyclerViewAdapter.onBackPressed()) {

            moveTaskToBack(true);
        }
        moveTaskToBack(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaProvider != null) {
            mediaProvider.onDestroy();
        }

        if (observer != null) {
            observer.unregister(this);
        }
    }

    @Override
    public int getDarkThemeRes() {
        return R.style.CameraRoll_Theme_Main;
    }

    @Override
    public int getLightThemeRes() {
        return R.style.CameraRoll_Theme_Light_Main;
    }

    @Override
    public void onThemeApplied(Theme theme) {
        if (pick_photos) {
            return;
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(toolbarColor);
        toolbar.setTitleTextColor(textColorPrimary);

        if (theme.darkStatusBarIcons()) {
            Util.setDarkStatusBarIcons(findViewById(R.id.root_view));
        } else {
            Util.setLightStatusBarIcons(findViewById(R.id.root_view));
        }

        if (theme.statusBarOverlay()) {
            addStatusBarOverlay(toolbar);
        }
    }

    @Override
    public void onSortClick() {
        showDialog();
    }

    @Override
    public void onHiddenClick() {
        hiddenFolders = Settings.getInstance(this)
                        .setHiddenFolders(this, !hiddenFolders);
                refreshPhotos();
    }

    @Override
    public void onExplorerClick() {
        startActivity(new Intent(this, FileExplorerActivity.class),
        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }


    @Override
    public void onAboutClick() {
        AboutDialogFragment ab=new AboutDialogFragment();
        ab.show(getSupportFragmentManager(),"SHOWN");
    }

    public void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.PauseDialog);
        final String[] items ={"Name","Size","Date"};
        builder.setSingleChoiceItems(items, -1,
                (dialog, which) -> {

                    int choice = 0;
                    switch (which) {
                        case 0:
                            choice = SortUtil.BY_NAME;
                            break;
                        case 1:
                            choice = SortUtil.BY_SIZE;
                            break;
                        case 2:
                            choice = SortUtil.BY_DATE;
                            break;
                            default:
                                Toast.makeText(MainActivity.this, "Nothing is happening!", Toast.LENGTH_SHORT).show();
                                break;
                    }
                            Settings.getInstance(getApplicationContext()).sortAlbumsBy(getApplicationContext(), choice);
                            resortAlbums();
                            dialog.dismiss();


                });
        AlertDialog dialog = builder.create();
        dialog.show();
        //dialog.getWindow().setLayout(1000, 600);
    }

    @Override
    public void onSettingsClick() {
        startActivityForResult(new Intent(this, SettingsActivity.class),
                      SETTINGS_REQUEST_CODE);
    }





    @Override
    public BroadcastReceiver getDefaultLocalBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                switch (intent.getAction()) {
                    case FileOperation.RESULT_DONE:
                    case FileOperation.FAILED:
                        refreshPhotos();
                        break;
                    case RESORT:
                        resortAlbums();
                        break;
                    case DATA_CHANGED:
                        albums = MediaProvider.getAlbums();
                        recyclerViewAdapter.setData(albums);
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public IntentFilter getBroadcastIntentFilter() {
        IntentFilter filter = FileOperation.Util.getIntentFilter(super.getBroadcastIntentFilter());
        filter.addAction(RESORT);
        filter.addAction(DATA_CHANGED);
        return filter;
    }
}
