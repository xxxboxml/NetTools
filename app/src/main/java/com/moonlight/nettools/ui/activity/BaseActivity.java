package com.moonlight.nettools.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.moonlight.nettools.R;
import com.moonlight.nettools.ui.dialog.PermissionDialog;
import com.moonlight.nettools.ui.listener.OnDialogClickListener;
import com.moonlight.nettools.ui.listener.OnNetworkRetryListener;
import com.moonlight.nettools.utils.NetUtils;

public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {
    protected TextView mTvTitle;

    protected T mViewBinding;

    protected int mBasePadding = 40;
    protected Context mContext;

    private OnNetworkRetryListener mListener;

    private NetworkStateReceiver mNetReceiver;
    private View mLayoutNoNetwork;
    protected boolean mHideActionBar = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        mContext = this;
        initContentView();
        initView();
        initCommonTitleView();
        initCommonNetworkView();
        setTitle();
        initData();
        hideActionBar();
    }

    private void initContentView() {
        mViewBinding = inflateBinding(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
    }

    private void hideActionBar(){
//        if (mHideActionBar && getSupportActionBar() != null) {//隐藏标题栏
//            getSupportActionBar().hide();
//        }
    }
    protected abstract T inflateBinding(LayoutInflater layoutInflater);

    protected abstract void initView();

    protected abstract void initData();

    public void setNetworkListener(OnNetworkRetryListener listener) {
        mListener = listener;
    }

    void initCommonTitleView() {
        View layoutTitle = mViewBinding.getRoot().findViewById(R.id.layoutTitle);
        if (layoutTitle != null) {
            mTvTitle = layoutTitle.findViewById(R.id.tvTitle);
            layoutTitle.findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        }
    }

    void initCommonNetworkView() {
        mLayoutNoNetwork = mViewBinding.getRoot().findViewById(R.id.layoutNoNetwork);
        if (mLayoutNoNetwork != null) {
            mLayoutNoNetwork.findViewById(R.id.btnRefresh).setOnClickListener(v -> {
                refreshNetworkView();
            });
            refreshNetworkView();
            mNetReceiver = new NetworkStateReceiver();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetReceiver, filter);
        }
    }

    private void refreshNetworkView() {
        if (mLayoutNoNetwork == null) {
            return;
        }
        boolean isNetworkAvailable = NetUtils.isNetworkAvailable(mContext);
        mLayoutNoNetwork.setVisibility(isNetworkAvailable ? View.GONE : View.VISIBLE);
        if (mListener != null) {
            mListener.onNetworkChanged(isNetworkAvailable);
        }
    }

    protected abstract void setTitle();

    public void showAlertDialog() {
        new PermissionDialog(this, new OnDialogClickListener() {

            @Override
            public void onPositiveClick() {

            }

            @Override
            public void onCancel() {
                finish();
            }
        }).show();
    }

    class NetworkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mLayoutNoNetwork != null) {
                refreshNetworkView();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mViewBinding = null;
            if (mNetReceiver != null) {
                unregisterReceiver(mNetReceiver);
                mNetReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}