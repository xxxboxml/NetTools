package com.moonlight.nettools.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.moonlight.nettools.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExceptionDialog extends BaseDialog {
    private final String mMessage;

    public ExceptionDialog(@NonNull Context context, String message) {
        super(context);
        mMessage = message;
        initView();
    }

    private void initView() {
        TextView tvMessage = mRootView.findViewById(R.id.tvMessage);
        tvMessage.setText(mMessage);

        tvMessage.setClickable(true);
        Spannable spannable = (Spannable) tvMessage.getText();
        ClickableSpan[] clickableSpans = spannable.getSpans(0, spannable.length(), ClickableSpan.class);
        for (ClickableSpan clickableSpan : clickableSpans) {
            int start = spannable.getSpanStart(clickableSpan);
            int end = spannable.getSpanEnd(clickableSpan);
            int flags = spannable.getSpanFlags(clickableSpan);
            // 为每个链接设置点击事件
            final String link = spannable.subSequence(start, end).toString();
            ClickableSpan newClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    // 处理链接点击事件
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    mContext.startActivity(intent);
                    dismiss();
                }
            };

            spannable.setSpan(newClickableSpan, start, end, flags);
        }
        tvMessage.setText(spannable);

        mRootView.findViewById(R.id.ivClose).setOnClickListener(v -> dismiss());

        Button btnNext = mRootView.findViewById(R.id.btnGo);
        btnNext.setOnClickListener(view -> {
            Pattern pattern = Patterns.WEB_URL;
            Matcher matcher = pattern.matcher(mMessage);
            String url = null;
            while (matcher.find()) {
                url = matcher.group();
                break;
            }
            if (!TextUtils.isEmpty(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(intent);
            }
            dismiss();
        });
        Button btnCancel = mRootView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> dismiss());
    }

    @Override
    protected void setContentViewId() {
        mLayoutRoot = R.layout.dialog_exception_alert;
    }
}
