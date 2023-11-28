package com.moonlight.nettools.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moonlight.nettools.R;
import com.moonlight.nettools.bean.FunctionItem;
import com.moonlight.nettools.databinding.ActivityHomeBinding;
import com.moonlight.nettools.ui.adapter.FunctionsAdapter;
import com.moonlight.nettools.ui.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> implements OnItemClickListener {

    private RecyclerView mRecyclerView;

    @Override
    protected ActivityHomeBinding inflateBinding(LayoutInflater layoutInflater) {
        return ActivityHomeBinding.inflate(layoutInflater);
    }

    @Override
    protected void initView() {
        mHideActionBar = false;
        mRecyclerView = mViewBinding.recyclerView;
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = mBasePadding;
                outRect.top = mBasePadding;
                outRect.left = mBasePadding;
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void initData() {
        List<FunctionItem> functionItems = buildData();
        FunctionsAdapter myAdapter = new FunctionsAdapter(this);
        myAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setDataSet(functionItems);
    }

    @Override
    public void setTitle() {

    }

    private void startActivity(Class activityToStart, String... permissions) {
//        XXPermissions.with(HomeActivity.this).permission(permissions).request(new OnPermissionCallback() {
//            @Override
//            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
//                if (allGranted) {
//                    Intent intent = new Intent(HomeActivity.this, activityToStart);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
//                showAlertDialog();
//            }
//        });
    }

    private List<FunctionItem> buildData() {
        List<FunctionItem> dataList = new ArrayList<>();
        FunctionItem item1 = new FunctionItem();
        item1.resId = R.drawable.no_network;
        item1.title = "Ping";
        dataList.add(item1);
        return dataList;
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        switch (position){
            case 0:
                Intent intent = new Intent(this, PingActivity.class);
                startActivity(intent);
                break;
            case 1:
                break;
            default:
                break;
        }
    }
}