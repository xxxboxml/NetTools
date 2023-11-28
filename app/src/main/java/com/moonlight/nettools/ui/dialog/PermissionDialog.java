package com.moonlight.nettools.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.moonlight.nettools.R;
import com.moonlight.nettools.ui.listener.OnDialogClickListener;


public class PermissionDialog extends BaseDialog {
    private final OnDialogClickListener mDialogListener;

    public PermissionDialog(@NonNull Context context, OnDialogClickListener dialogListener) {
        super(context);
        initView();
        mDialogListener = dialogListener;
    }

    private void initView() {
        mRootView.findViewById(R.id.ivClose).setOnClickListener(v -> {
            dismiss();
            mDialogListener.onCancel();
        });

        Button btnNext = mRootView.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
            intent.setData(uri);
            mContext.startActivity(intent);
            dismiss();
        });
    }

    @Override
    protected void setContentViewId() {
        mLayoutRoot = R.layout.dialog_go_setting_permissions;
    }
}
