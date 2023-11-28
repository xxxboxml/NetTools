package com.moonlight.nettools.ui.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public abstract class BaseDialog extends AlertDialog {
    protected Context mContext;
    protected int mLayoutRoot;
    protected View mRootView;
    protected BaseDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        setContentViewId();
        initContentView();
    }

    protected abstract void setContentViewId();
    protected void initContentView(){
        Window window = getWindow();
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        show();
        WindowManager.LayoutParams lp = window.getAttributes();
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        lp.width = (int) (width * 0.8);// 调整该值可以设置对话框显示的宽度
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        window.setBackgroundDrawable(new BitmapDrawable());
        mRootView = View.inflate(mContext, mLayoutRoot, null);
        window.setContentView(mRootView);
    }
}
