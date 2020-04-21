package com.example.fleamarket.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fleamarket.R;
import com.example.fleamarket.message.chat.ChatMessage;
import com.example.fleamarket.message.chat.ChatWindowActivity;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.utils.PictureUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<ChatMessage> mMessageList = new ArrayList<>();
    private MsgFragment mFragment;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mMessageView;
        CircleImageView mAvatar;
        TextView mName;
        TextView mContent;
        TextView mTime;

        public ViewHolder(View view){
            super(view);
            mMessageView = view;
            mAvatar = view.findViewById(R.id.avatar);
            mName = view.findViewById(R.id.nick_name);
            mContent = view.findViewById(R.id.content);
            mTime = view.findViewById(R.id.time);
        }
    }

    public MessageAdapter(MsgFragment fragment) {
        mFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mMessageView.setOnClickListener((v) -> {
                int position = holder.getAdapterPosition();
                Commodity commodity = new Commodity();
                commodity.setSellerID(mMessageList.get(position).getUserID());
                commodity.setSellerName(mMessageList.get(position).getUserName());
                Intent intent = new Intent(mFragment.getActivity(), ChatWindowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodity", commodity);
                intent.putExtras(bundle);
                mFragment.startActivity(intent);
            });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = mMessageList.get(position);
        PictureUtils.displayImage(holder.mAvatar, mFragment.getActivity().getExternalCacheDir().getAbsolutePath() +
                "/avatar/avatar_" + chatMessage.getUserID() + ".jpg");
        holder.mName.setText(chatMessage.getUserName());
        holder.mContent.setText(chatMessage.getContent());
        holder.mTime.setText(chatMessage.getSendTime());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public List<ChatMessage> getData() {
        return mMessageList;
    }
}
