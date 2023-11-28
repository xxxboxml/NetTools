package com.moonlight.nettools.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.moonlight.nettools.R;
import com.moonlight.nettools.bean.FunctionItem;
import com.moonlight.nettools.ui.listener.OnItemClickListener;

import java.util.List;


public class FunctionsAdapter extends RecyclerView.Adapter<FunctionsAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private List<FunctionItem> mList;
    private OnItemClickListener mListener;

    public FunctionsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setDataSet(List<FunctionItem> list) {
        mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_function, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvTitle.setText(mList.get(position).title);
        holder.ivIcon.setImageResource(mList.get(position).resId);
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvTitle;
        private final View layoutItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            layoutItem = itemView.findViewById(R.id.layoutItem);
        }
    }
}