package com.theabsolutecompany.floral.data.provider;

import android.app.Activity;
import android.content.Context;

import com.theabsolutecompany.floral.data.models.File_POJO;
import com.theabsolutecompany.floral.data.provider.retriever.Retriever;
import com.theabsolutecompany.floral.data.provider.retriever.StorageRetriever;
import com.theabsolutecompany.floral.data.models.StorageRoot;
import com.theabsolutecompany.floral.util.StorageUtil;

public class FilesProvider extends Provider {

    private Retriever retriever;

    public abstract static class Callback implements Provider.Callback {
        public abstract void onDirLoaded(File_POJO dir);
    }

    public FilesProvider(Context context) {
        super(context);
        retriever = new StorageRetriever();
    }

    public void loadDir(final Activity context, String dirPath,
                        FilesProvider.Callback callback) {

        setCallback(callback);

        ((StorageRetriever) retriever).loadFilesForDir(context, dirPath,
                new Callback() {
                    @Override
                    public void onDirLoaded(File_POJO dir) {
                        Callback callback = getCallback();
                        if (callback != null) {
                            callback.onDirLoaded(dir);
                        }
                    }

                    @Override
                    public void timeout() {
                        Callback callback = getCallback();
                        if (callback != null) {
                            callback.timeout();
                        }
                    }

                    @Override
                    public void needPermission() {
                        Callback callback = getCallback();
                        if (callback != null) {
                            callback.needPermission();
                        }
                    }
                });
    }

    public static StorageRoot[] getRoots(Activity context) {
        return StorageUtil.loadRoots(context);
    }

}
