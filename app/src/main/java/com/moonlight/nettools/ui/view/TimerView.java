package com.moonlight.nettools.ui.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

public class TimerView extends androidx.appcompat.widget.AppCompatTextView {
    private int seconds = 0; // 计时器的初始时间，单位为秒

    private final Handler mHandler;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 将计时器的秒数转换成小时、分钟和秒数
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int remainingSeconds = (seconds % 3600) % 60;

            // 将计时信息显示在 UI 上
            String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, remainingSeconds);
            TimerView.this.setText(timeFormatted);

            // 计时器递增
            seconds++;

            // 调度下一次更新
            mHandler.postDelayed(this, 1000); // 每隔 1 秒更新一次计时器
        }
    };

    public TimerView(@NonNull Context context) {
        super(context);
        mHandler = new Handler();
    }

    public TimerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mHandler = new Handler();
    }

    public TimerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new Handler();
    }

    public void start(){
        this.setVisibility(VISIBLE);
        mHandler.postDelayed(runnable, 1000); // 延迟 1 秒后开始计时器
    }

    public void stop(){
        this.setVisibility(GONE);
        mHandler.removeCallbacks(runnable); // 停止计时器
        seconds = 0;
    }
}
