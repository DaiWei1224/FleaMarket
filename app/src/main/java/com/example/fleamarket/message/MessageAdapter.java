package com.example.fleamarket.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fleamarket.MyApplication;
import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.database.DatabaseHelper;
import com.example.fleamarket.message.chat.ChatMessage;
import com.example.fleamarket.message.chat.ChatWindowActivity;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.utils.MyUtil;
import com.example.fleamarket.utils.PictureUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        holder.mMessageView.setOnLongClickListener((v) -> {
            int position = holder.getAdapterPosition();
            showChatOptionDialog(position, mMessageList.get(position).getUserID(), mMessageList.get(position).getUserName());
            return true;
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
        holder.mTime.setText(MyUtil.handleDate(chatMessage.getSendTime()));
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public List<ChatMessage> getData() {
        return mMessageList;
    }

    private void showChatOptionDialog(int position, String userID, String userName) {
        String[] items = {"删除与 " + userName + " 的聊天"};
        new AlertDialog.Builder(mFragment.getContext()).setItems(items, (i, dialog) ->
                    deleteChatDialog(position, userID, userName)
        ).create().show();
    }

    private void deleteChatDialog(int position, String userID, String userName) {
        new AlertDialog.Builder(mFragment.getContext())
                .setMessage("删除聊天会同时删除本地聊天记录，确认删除吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    mMessageList.remove(position);
                    this.notifyItemRemoved(position);
                    // 删除对应的聊天记录数据表
                    DatabaseHelper dbHelper = new DatabaseHelper(MyApplication.getContext(), "chat_" + User.getId(), null, 1);
                    dbHelper.deleteTable(dbHelper.getWritableDatabase(), "chatto_" + userID);
                    dbHelper.close();
                }).setNegativeButton("取消", (dialog, which) ->
            dialog.dismiss() ).create().show();
    }

}
