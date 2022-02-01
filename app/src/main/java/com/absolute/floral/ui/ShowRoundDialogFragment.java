package com.absolute.floral.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.absolute.floral.R;


public class ShowRoundDialogFragment extends RoundedBottomSheet {


    TextView sortext, hiddenfolder, filexplorer, settings, about,share;
    private CheckRefreshClickListener mCheckSharingListener;
    private CheckRefreshClickListener mCheckHiddenListener;
    private CheckRefreshClickListener mCheckExplorerListener;
    private CheckRefreshClickListener mCheckSettingsListener;
    private CheckRefreshClickListener mCheckAboutListener;
    private CheckRefreshClickListener mCheckSortClickListener;

    public static ShowRoundDialogFragment newInstance() {
        return new ShowRoundDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCheckHiddenListener = (CheckRefreshClickListener) context;
        mCheckExplorerListener = (CheckRefreshClickListener) context;
        mCheckSettingsListener = (CheckRefreshClickListener) context;
        mCheckAboutListener = (CheckRefreshClickListener) context;
        mCheckSortClickListener = (CheckRefreshClickListener) context;
        mCheckSharingListener=(CheckRefreshClickListener)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_show_round_dialog, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sortext = getView().findViewById(R.id.sort);
        hiddenfolder = getView().findViewById(R.id.hidden);
        filexplorer = getView().findViewById(R.id.filesexp);
        settings = getView().findViewById(R.id.settings);
        about = getView().findViewById(R.id.about);
        share = getView().findViewById(R.id.sharing);

        sortext.setOnClickListener(v -> mCheckSortClickListener.onSortClick());
        hiddenfolder.setOnClickListener(v -> mCheckHiddenListener.onHiddenClick());
        filexplorer.setOnClickListener(v -> mCheckExplorerListener.onExplorerClick());
        settings.setOnClickListener(v -> mCheckSettingsListener.onSettingsClick());
        about.setOnClickListener(v -> mCheckAboutListener.onAboutClick());
        share.setOnClickListener(v -> mCheckSharingListener.OnShareClick());
        super.onViewCreated(view, savedInstanceState);

    }
}


interface CheckRefreshClickListener {
    void onSortClick();
    void OnShareClick();
    void onHiddenClick();
    void onExplorerClick();
    void onSettingsClick();
    void onAboutClick();
}