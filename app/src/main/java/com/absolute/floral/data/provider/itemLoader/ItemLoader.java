package com.absolute.floral.data.provider.itemLoader;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import com.absolute.floral.data.models.Album;
import com.absolute.floral.data.models.File_POJO;

public abstract class ItemLoader {

    public class Result {
        public ArrayList<Album> albums;
        public File_POJO files;
    }

    ItemLoader() {

    }

    @SuppressWarnings("unused")
    public abstract ItemLoader newInstance();

    public abstract void onNewDir(Context context, File dir);

    public abstract void onFile(Context context, File file);

    public abstract void onDirDone(Context context);

    public abstract Result getResult();
}
