package com.absolute.floral.preferences;

import android.content.Context;
import androidx.preference.DialogPreference;
import android.util.AttributeSet;

import com.absolute.floral.R;
import com.absolute.floral.data.Settings;

public class ColumnCountPreference extends DialogPreference {

    private int columnCount;
    private int mDialogLayoutResId = R.layout.pref_dialog_style;

    @SuppressWarnings("unused")
    public ColumnCountPreference(Context context) {
        super(context);
    }

    @SuppressWarnings("unused")
    public ColumnCountPreference(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.dialogPreferenceStyle);
    }

    @SuppressWarnings("unused")
    public ColumnCountPreference(Context context, AttributeSet attrs,
                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public ColumnCountPreference(Context context, AttributeSet attrs,
                                 int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setDialogLayoutResource(mDialogLayoutResId);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        columnCount = getPersistedInt(Settings.DEFAULT_COLUMN_COUNT);
    }

    int getColumnCount() {
        return columnCount;
    }

    void setColumnCount(int columnCount) {
        this.columnCount = columnCount;

        // Save to Shared Preferences
        persistInt(columnCount);

        //update summary
        setSummary(String.valueOf(columnCount));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
                                     Object defaultValue) {
        // Read the value. Use the default value if it is not possible.
        setColumnCount(restorePersistedValue ?
                getPersistedInt(columnCount) : (int) defaultValue);
    }

    @Override
    public int getDialogLayoutResource() {
        return mDialogLayoutResId;
    }
}
