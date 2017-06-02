package com.ingenic.recyclerviewdemo;

import android.content.Context;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    private List<String> mDatas;
    private List<Integer> mHeights;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private int mOrientation;
    private int mSize;

    RecyclerAdapter(Context context, List<String> datas, List<Integer> heights,
                    int orientation) {
        mInflater = LayoutInflater.from(context);

        mDatas = datas;
        mHeights = heights;
        mOrientation = orientation;
        mSize = (int) (context.getResources().getDisplayMetrics().density * 100);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    void addData(int position) {
        if (mHeights != null && mHeights.size() == mDatas.size()) {
            mHeights.add( (int) (100 + Math.random() * 300));
        }
        notifyItemInserted(position);
        mDatas.add(position, "Insert One");
    }

    void removeData(int position) {
        if (mHeights != null && mHeights.size() == mDatas.size()) {
            mHeights.remove(position);
        }
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(
                mOrientation == OrientationHelper.VERTICAL ?
                        R.layout.list_item_vertical : R.layout.list_item_horizontal,
                parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.text.getLayoutParams();
        if (mDatas.size() == mHeights.size()) {
            if (mOrientation == OrientationHelper.VERTICAL)
                params.height = mHeights.get(position);
            else
                params.width = mHeights.get(position);
        } else {
            params.width = mSize;
            params.height = mSize;
        }
        holder.text.setLayoutParams(params);

        holder.text.setText(mDatas.get(position));

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(v, holder.getLayoutPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        RecyclerViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.id_num);
        }
    }
}