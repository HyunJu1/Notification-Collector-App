package com.example.hyunju.notification_collector.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hyunju.notification_collector.R;
import com.example.hyunju.notification_collector.models.SendedMessage;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<SendedMessage> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public RecyclerViewAdapter(Context context, List<SendedMessage> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.activity_recyclerview_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        SendedMessage msg = mData.get(position);
        holder.tv_platformType.setText(msg.getPlatfrom());
        holder.tv_sendedMessageContent.setText(msg.getMessage());
        holder.tv_sendedTime.setText(msg.getTime());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_platformType, tv_sendedMessageContent,tv_sendedTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_platformType = itemView.findViewById(R.id.tv_platformType);
            tv_sendedMessageContent = itemView.findViewById(R.id.tv_sendedMessageContent);
            tv_sendedTime=itemView.findViewById(R.id.tv_sendedTime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public SendedMessage getItem(int idx) {
        return mData.get(idx);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
