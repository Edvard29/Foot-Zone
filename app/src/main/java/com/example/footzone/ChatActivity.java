package com.example.footzone;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.adapter.ChatAdapter;
import com.example.footzone.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageInput;
    private Button sendButton;
    private List<ChatMessage> messageList = new ArrayList<>();

    private DatabaseReference chatRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.chat_recyclerview);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);

        // Получение текущего пользователя Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatRef = FirebaseDatabase.getInstance().getReference("chat_messages");

        loadMessages(); // Загружаем сообщения из Firebase

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty() || currentUser == null) return;

        String userId = currentUser.getUid();
        String userName = currentUser.getEmail(); // Можно использовать другое поле, если есть
        long timestamp = System.currentTimeMillis();

        ChatMessage newMessage = new ChatMessage(userId, userName, messageText, timestamp);
        chatRef.push().setValue(newMessage);

        messageInput.setText(""); // Очищаем поле ввода
    }

    private void loadMessages() {
        chatRef.orderByChild("timestamp").limitToLast(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessage message = snapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Ошибка загрузки сообщений", databaseError.toException());
            }
        });
    }
}
