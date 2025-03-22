package com.example.footzone;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.ChatAdapter;
import com.example.footzone.model.ChatMessage;


import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageInput;
    private Button sendButton;
    private List<ChatMessage> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.chat_recyclerview);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString();
            if (!messageText.isEmpty()) {
                // Добавить новое сообщение в список
                ChatMessage newMessage = new ChatMessage("Пользователь", messageText);
                messageList.add(newMessage);
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                messageInput.setText(""); // Очистить поле ввода
            }
        });
    }
}
