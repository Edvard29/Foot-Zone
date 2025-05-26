package com.example.footzone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;


import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView emailText;
    private TextInputEditText nameEditText;
    private Button saveButton;
    private SharedPreferences prefs;
    private DatabaseReference usersRef;
    private String username;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize views
        emailText = findViewById(R.id.email_text);
        nameEditText = findViewById(R.id.name_edit_text);
        saveButton = findViewById(R.id.save_button);

        // Set up NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            // Log error for debugging
            System.out.println("NavigationView not found in layout");
        }

        // Initialize SharedPreferences and Firebase
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Load user data
        username = prefs.getString("username", null);
        String email = prefs.getString("email", "Loading...");
        emailText.setText("Email: " + email);

        if (username != null) {
            nameEditText.setText(username);
        }

        // Set save button listener
        saveButton.setOnClickListener(v -> saveUserName());
    }

    private void saveUserName() {
        String newName = nameEditText.getText().toString().trim();
        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username != null && !newName.equals(username)) {
            // Update username in SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", newName);
            editor.apply();

            // Update username in Firebase
            usersRef.child(newName).setValue(usersRef.child(username).get()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Remove old username data
                    usersRef.child(username).removeValue();
                    username = newName;
                    Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update name", Toast.LENGTH_SHORT).show();
                }
            });
        }
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