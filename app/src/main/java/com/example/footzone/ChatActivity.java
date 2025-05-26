package com.example.footzone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.adapter.ChatAdapter;
import com.example.footzone.model.Message;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();
    private DatabaseReference chatRef;
    private String username;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize views
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        // Set up NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Set toolbar title
        int fixtureId = getIntent().getIntExtra("fixtureId", -1);
        String matchTitle = getIntent().getStringExtra("matchTitle");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chat: " + matchTitle);
        }

        // Set up RecyclerView
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setAdapter(chatAdapter);

        // Get username from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        username = prefs.getString("username", "Anonymous");

        // Initialize Firebase reference
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(String.valueOf(fixtureId));
        listenForMessages();

        // Set send button listener
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Message message = new Message(messageText, username, System.currentTimeMillis());
            chatRef.push().setValue(message);
            messageInput.setText("");
        }
    }

    private void listenForMessages() {
        this.chatRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> newMessages = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        newMessages.add(message);
                    }
                }
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() { return messageList.size(); }
                    @Override
                    public int getNewListSize() { return newMessages.size(); }
                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        return messageList.get(oldItemPosition).getTimestamp() == newMessages.get(newItemPosition).getTimestamp();
                    }
                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        return messageList.get(oldItemPosition).equals(newMessages.get(newItemPosition));
                    }
                });
                messageList.clear();
                messageList.addAll(newMessages);
                diffResult.dispatchUpdatesTo(chatAdapter);
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ChatActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
        return true;
    }
}