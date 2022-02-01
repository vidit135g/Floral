package com.absolute.floral.preferences;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.absolute.floral.R;
import com.absolute.floral.themes.Theme;
import com.absolute.floral.data.Settings;

public class ColumnCountPreferenceDialogFragment
        extends DialogFragment implements DialogInterface.OnClickListener {

    private int columnCount = Settings.DEFAULT_COLUMN_COUNT;
    private int whichButtonClicked;
    private Preference preference;

    public static ColumnCountPreferenceDialogFragment newInstance(Preference preference) {
        ColumnCountPreferenceDialogFragment fragment = new ColumnCountPreferenceDialogFragment();
        fragment.setPreference(preference);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get initial value from pref
        if (preference instanceof ColumnCountPreference) {
            columnCount = ((ColumnCountPreference) preference).getColumnCount();
            if (columnCount == 0) {
                columnCount = Settings.DEFAULT_COLUMN_COUNT;
            }
        }

        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext())
                .inflate(R.layout.pref_dialog_column_count, null);

        final TextView textView = view.findViewById(R.id.column_count);
        textView.setText(String.valueOf(columnCount));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.minus) {
                    if (columnCount > 1) {
                        columnCount--;
                    }
                } else {
                    columnCount++;
                }
                textView.setText(String.valueOf(columnCount));
            }
        };

        Theme theme = Settings.getInstance(getContext()).getThemeInstance(getContext());
        int textColorSec = theme.getTextColorSecondary(getContext());

        ImageButton minus = view.findViewById(R.id.minus);
        minus.setOnClickListener(onClickListener);

        ImageButton plus = view.findViewById(R.id.plus);
        plus.setOnClickListener(onClickListener);

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.column_count)
                .setView(view)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        whichButtonClicked = i;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (whichButtonClicked == DialogInterface.BUTTON_POSITIVE
                && preference instanceof ColumnCountPreference) {
            ColumnCountPreference columnCountPreference =
                    ((ColumnCountPreference) preference);
            columnCountPreference.setColumnCount(columnCount);

            Settings.getInstance(getActivity())
                    .setColumnCount(columnCount);
        }
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }
}
