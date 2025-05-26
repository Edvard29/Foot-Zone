package com.example.footzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonGoToRegister, buttonTestUser;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoToRegister = findViewById(R.id.buttonGoToRegister);
        buttonTestUser = findViewById(R.id.buttonTestUser);

        buttonLogin.setOnClickListener(v -> loginUser());
        buttonGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        buttonTestUser.setOnClickListener(v -> registerTestUser());
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                saveUserDataAndProceed(user);
                            } else {
                                Toast.makeText(this, "Подтвердите email перед входом!", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                            }
                        } else {
                            Toast.makeText(this, "Пользователь не найден!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void registerTestUser() {
        // Генерируем уникальный email и пароль для тестового пользователя
        String testEmail = "individualproject2025@gmail.com" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        String testPassword = "Samsung2025";
        String testUsername = "Samsung2025" + UUID.randomUUID().toString().substring(0, 8);

        mAuth.createUserWithEmailAndPassword(testEmail, testPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Автоматически подтверждаем email для тестового пользователя
                            user.updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(testUsername)
                                    .build());

                            // Сохраняем данные и переходим в приложение
                            saveUserDataAndProceed(user);
                            Toast.makeText(this, "Тестовый пользователь создан!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Ошибка создания тестового пользователя: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserDataAndProceed(FirebaseUser user) {
        String username = user.getDisplayName() != null && !user.getDisplayName().isEmpty()
                ? user.getDisplayName()
                : user.getEmail().split("@")[0];
        String userEmail = user.getEmail();

        // Сохраняем данные в SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putString("email", userEmail);
        editor.apply();
        Log.d("LoginActivity", "Saved to SharedPreferences - Username: " + username + ", Email: " + userEmail);

        // Сохраняем данные в Firebase Realtime Database
        usersRef.child(username).child("email").setValue(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LoginActivity", "Email saved to Firebase: " + userEmail);
                    } else {
                        Log.e("LoginActivity", "Failed to save email to Firebase: " + task.getException().getMessage());
                    }
                });

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}