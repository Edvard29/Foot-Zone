

package com.example.footzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    private TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private MaterialButton buttonRegister;
    private MaterialCardView registerCard;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private SharedPreferences prefs;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Register");
        }

        // Initialize UI
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        registerCard = findViewById(R.id.register_card);

        // Initialize Firebase and SharedPreferences
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Apply animation
        registerCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        buttonRegister.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // Set up button listener
        buttonRegister.setOnClickListener(v -> registerUser());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_register;
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email address");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError("Please confirm your password");
            return;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Register with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Send email verification
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (!verificationTask.isSuccessful()) {
                                            Toast.makeText(this, "Error sending verification email: " + verificationTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                            // Store user data
                            String username = email.split("@")[0]; // Use email prefix as username
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("username", username);
                            editor.putString("email", email);
                            editor.apply();

                            usersRef.child(username).child("email").setValue(email);
                            usersRef.child(username).child("registered").setValue(true);
                            usersRef.child(username).child("points").setValue(0L);
                            usersRef.child(username).child("predictionCount").setValue(0L);

                        }
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }



    private void navigateToLogin() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}