package com.absolute.floral.themes;

import android.content.Context;
import androidx.core.content.ContextCompat;

public abstract class Theme {

    static final int BASE_DARK = 1;
    static final int BASE_LIGHT = 0;

    public abstract int getBaseTheme();

    public boolean isBaseLight() {
        return getBaseTheme() == BASE_LIGHT;
    }

    /*flags*/
    public abstract boolean darkStatusBarIcons();

    public abstract boolean elevatedToolbar();

    public abstract boolean statusBarOverlay();

    public abstract boolean darkStatusBarIconsInSelectorMode();

    /*colors*/
    public abstract int getBackgroundColorRes();

    public abstract int getToolbarColorRes();

    public abstract int getTextColorPrimaryRes();

    public abstract int getTextColorSecondaryRes();

    public abstract int getAccentColorRes();

    public abstract int getAccentColorLightRes();

    public abstract int getAccentTextColorRes();


    public int getBackgroundColor(Context context) {
        return getColor(context, getBackgroundColorRes());
    }

    public int getToolbarColor(Context context) {
        return getColor(context, getToolbarColorRes());
    }

    public int getTextColorPrimary(Context context) {
        return getColor(context, getTextColorPrimaryRes());
    }

    public int getTextColorSecondary(Context context) {
        return getColor(context, getTextColorSecondaryRes());
    }

    public int getAccentColor(Context context) {
        return getColor(context, getAccentColorRes());
    }

    public int getAccentColorLight(Context context) {
        return getColor(context, getAccentColorLightRes());
    }

    public int getAccentTextColor(Context context) {
        return getColor(context, getAccentTextColorRes());
    }

    private static int getColor(Context context, int res) {
        return ContextCompat.getColor(context, res);
    }


    /*Dialog theme*/
    public abstract int getDialogThemeRes();

    @Override
    public boolean equals(Object obj) {
        Class c = this.getClass();
        return c.isInstance(obj);
    }
}
