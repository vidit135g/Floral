package com.absolute.floral.ui;

import android.app.Application;

/**
 * Created by vamsi on 06-05-2017 for android custom font article
 */

public class CustomFontApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OverrideFonts.setDefaultFont(this, "DEFAULT", "fonts/google.ttf");
        OverrideFonts.setDefaultFont(this, "MONOSPACE", "fonts/google.ttf");
        OverrideFonts.setDefaultFont(this, "SERIF", "fonts/google.ttf");
        OverrideFonts.setDefaultFont(this, "SANS_SERIF", "fonts/google.ttf");
    }
}
