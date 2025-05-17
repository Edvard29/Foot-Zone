package com.example.footzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserActivity extends BaseActivity {

    private TextView emailText;
    private TextInputEditText nameEditText;
    private ImageView avatarImage;
    private Button uploadAvatarButton, saveButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference storageRef;
    private SharedPreferences prefs;
    private String username, userEmail;
    private Uri avatarUri;

    // Лаунчер для выбора изображения
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    avatarUri = uri;
                    avatarImage.setImageURI(avatarUri);
                    uploadAvatarToFirebase();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Инициализация компонентов
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emailText = findViewById(R.id.email_text);
        nameEditText = findViewById(R.id.name_edit_text);
        avatarImage = findViewById(R.id.avatar_image);
        uploadAvatarButton = findViewById(R.id.upload_avatar_button);
        saveButton = findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        storageRef = FirebaseStorage.getInstance().getReference("avatars");
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Получение данных пользователя
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        username = prefs.getString("username", user.getEmail().split("@")[0]);
        userEmail = user.getEmail();
        emailText.setText("Email: " + userEmail);

        // Загрузка существующих данных
        loadUserData();

        // Обработчики кнопок
        uploadAvatarButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        saveButton.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        usersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String savedName = dataSnapshot.child("name").getValue(String.class);
                    String avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);

                    if (savedName != null && !savedName.isEmpty()) {
                        nameEditText.setText(savedName);
                    }
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Picasso.get().load(avatarUrl).into(avatarImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserActivity", "Error loading user data: " + databaseError.getMessage());
            }
        });
    }

    private void uploadAvatarToFirebase() {
        if (avatarUri != null) {
            StorageReference avatarRef = storageRef.child(username + "_avatar.jpg");
            avatarRef.putFile(avatarUri)
                    .addOnSuccessListener(taskSnapshot -> avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String avatarUrl = uri.toString();
                        usersRef.child(username).child("avatarUrl").setValue(avatarUrl);
                        Log.d("UserActivity", "Avatar uploaded: " + avatarUrl);
                        Toast.makeText(this, "Avatar uploaded", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> {
                        Log.e("UserActivity", "Failed to upload avatar: " + e.getMessage());
                        Toast.makeText(this, "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveUserData() {
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.child(username).child("name").setValue(name)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("username", name); // Обновляем username в SharedPreferences
                        editor.apply();
                        Toast.makeText(this, "Name saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save name", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}