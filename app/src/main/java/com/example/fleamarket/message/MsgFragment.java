package com.example.fleamarket.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fleamarket.R;
import com.example.fleamarket.home.recyclerview.SpaceItemDecoration;
import com.example.fleamarket.message.chat.ChatMessage;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MsgFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View layout = inflater.inflate(R.layout.fragment_msg, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(2, 2));

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage("床前这明月光 疑似那地上霜 举头我望明月 低头他思故乡",
                "昨天", "66666", "FANTASY");
        messages.add(chatMessage);
        chatMessage = new ChatMessage("春眠不觉晓处处闻啼鸟",
                "18:05", "23333", "Jsss");
        messages.add(chatMessage);

        MessageAdapter adapter = new MessageAdapter(messages, this);
        recyclerView.setAdapter(adapter);

        return layout;
    }
}
