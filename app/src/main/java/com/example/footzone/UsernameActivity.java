package com.example.footzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class UsernameActivity extends AppCompatActivity {

    private EditText usernameInput;
    private Button saveButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        usernameInput = findViewById(R.id.username_input);
        saveButton = findViewById(R.id.save_button);
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Проверяем, есть ли уже сохраненное имя
        String savedUsername = prefs.getString("username", null);
        if (savedUsername != null) {
            proceedToChat();
            return;
        }

        saveButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            if (!username.isEmpty()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", username);
                editor.apply();
                proceedToChat();
            }
        });
    }

    private void proceedToChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("fixtureId", getIntent().getIntExtra("fixtureId", -1));
        intent.putExtra("matchTitle", getIntent().getStringExtra("matchTitle"));
        startActivity(intent);
        finish();
    }
}