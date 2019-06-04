package com.example.hyunju.notification_collector.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunju.notification_collector.ChattingActivity;
import com.example.hyunju.notification_collector.R;
import com.example.hyunju.notification_collector.global.GlobalApplication;
import com.example.hyunju.notification_collector.models.SendedMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<SendedMessage> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public RecyclerViewAdapter(Context context, List<SendedMessage> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    public RecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_recyclerview_item, viewGroup, false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, final int position) {
        SendedMessage msg = mData.get(position);
        
        // mail attachment
        if(msg.getAttachment_str() != null && msg.getAttachment_str().size() > 0) {
            for(int i = 0; i < msg.getAttachment_str().size(); i++) {
                final TextView textViewAttachment = new TextView(holder.layout_attachment.getContext());
                final String filename = msg.getAttachment_str().get(i);
                textViewAttachment.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                textViewAttachment.setPadding(20, 10, 0, 0);
                textViewAttachment.setTextColor(Color.parseColor("#7580c1"));
                textViewAttachment.setText(filename);
                textViewAttachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadFile(filename);
                    }
                });
                holder.layout_attachment.addView(textViewAttachment);
            }
        }

        holder.tv_platformType.setText(msg.platform);
        holder.tv_sendedMessageContent.setText(msg.message);
        holder.tv_sendedTime.setText(msg.time);
        holder.type = msg.type;
        if (holder.type.equals(SendedMessage.MESSAGE_SEND)) {
            holder.linear_layout_1.setBackgroundResource(R.drawable.outbox2);
            ((LinearLayout)holder.itemView).setGravity(Gravity.RIGHT);
        } else {
            holder.linear_layout_1.setBackgroundResource(R.drawable.inbox2);
            ((LinearLayout)holder.itemView).setGravity(Gravity.LEFT);
        }
        if(getItem(position).file!=null){
            final TextView textViewAttachment = new TextView(holder.layout_attachment.getContext());
            textViewAttachment.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textViewAttachment.setPadding(20, 10, 0, 0);
            textViewAttachment.setTextColor(Color.parseColor("#7580c1"));
            textViewAttachment.setText(getItem(position).file.getName());
            textViewAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadFile(getItem(position).file);
                }
            });
            holder.layout_attachment.addView(textViewAttachment);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void setList(ArrayList<SendedMessage> list) {
        mData = list;
        notifyDataSetChanged();
    }

    public void addList(SendedMessage message) {
        mData.add(message);
        notifyItemChanged(mData.size() - 1);


    }
    public SendedMessage getItem ( int idx){
        return mData.get(idx);
    }

    public void setClickListener (ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_platformType, tv_sendedMessageContent, tv_sendedTime;
        LinearLayout linear_layout_1;
        LinearLayout layout_attachment;
        String type;

        public ViewHolder(View itemView) {
            super(itemView);

            linear_layout_1 = itemView.findViewById(R.id.linear_layout_1);

            tv_platformType = itemView.findViewById(R.id.tv_platformType);

            tv_sendedMessageContent = itemView.findViewById(R.id.tv_sendedMessageContent);

            tv_sendedTime = itemView.findViewById(R.id.tv_sendedTime);

            layout_attachment = itemView.findViewById(R.id.layout_attachment);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    public void uploadFile(final String filename) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://notification-collector-app.appspot.com").child("files/" + filename);
        storageReference.putFile(Uri.fromFile(new File(GlobalApplication.getInstance().getFilesDir().getPath().toString() + "/" + filename)))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                GlobalApplication.filepath = uri.toString();
                                GlobalApplication.filename = filename;
                                Toast.makeText(GlobalApplication.getInstance(), filename + "을 전달하고 싶은 사용자에게 메세지를 전송하세요", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(GlobalApplication.getInstance(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void uploadFile(final File file){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://notification-collector-app.appspot.com").child("files/" + file.getName());
        storageReference.putFile(Uri.fromFile(file))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                GlobalApplication.filepath = uri.toString();
                                GlobalApplication.filename = file.getName();
                                Toast.makeText(GlobalApplication.getInstance(), file.getName() + "을 전달하고 싶은 사용자에게 메세지를 전송하세요", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(GlobalApplication.getInstance(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}