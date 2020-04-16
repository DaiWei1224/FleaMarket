package com.example.fleamarket.message.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fleamarket.R;
import com.example.fleamarket.utils.PictureUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private List<ChatMessage> dataList = null;

    public ChatAdapter(Context context, List<ChatMessage> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    //ViewHolder内部类，它的实例用于缓存View控件
    private static class ViewHolder{
        TextView content;
        CircleImageView avatar;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = dataList.get(position);
        if (chatMessage.isMe()) {
            return 0;
        } else {
            return 1;
        }
    }

    //使ListView的item不可点击
    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = null;
        ChatMessage chatMessage = dataList.get(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            if (chatMessage.isMe()) {
                rootView = inflater.inflate(R.layout.chat_me, parent, false);
            } else {
                rootView = inflater.inflate(R.layout.chat_other, parent, false);
            }
            viewHolder = new ViewHolder();
            viewHolder.content =  rootView.findViewById(R.id.content);
            viewHolder.avatar = rootView.findViewById(R.id.avatar);
            rootView.setTag(viewHolder);
        }else{
            rootView = convertView;
            viewHolder = (ViewHolder)rootView.getTag();
        }
        PictureUtils.displayImage(viewHolder.avatar, context.getExternalCacheDir().getAbsolutePath() +
                "/avatar/avatar_" + chatMessage.getUserID() + ".jpg");
        viewHolder.content.setText(chatMessage.getContent());
        return rootView;
    }

}
