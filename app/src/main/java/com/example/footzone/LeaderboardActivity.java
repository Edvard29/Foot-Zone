/*
 * LeaderboardActivity.java
 * Displays a leaderboard of users sorted by points, with the current user's points highlighted at the top.
 * Features:
 * - Fetches user data from Firebase Realtime Database (users/username/points).
 * - Uses RecyclerView to display users sorted by points (descending).
 * - Highlights user's points in a CardView with animation.
 * - Sends FCM push notification when leaderboard updates (e.g., points change).
 * - Modern design with Material Components, gradient background, and smooth animations.
 *
 * Dependencies:
 * - Firebase Database: implementation 'com.google.firebase:firebase-database:21.0.0'
 * - Firebase Messaging: implementation 'com.google.firebase:firebase-messaging:24.0.3'
 * - Material Components: implementation 'com.google.android.material:material:1.12.0'
 * - RecyclerView: implementation 'androidx.recyclerview:recyclerview:1.3.2'
 *
 * Firebase Setup:
 * - Ensure google-services.json is in app/ directory.
 * - Store FCM tokens in users/username/fcmToken.
 * - Backend endpoint for FCM: POST /sendNotification with JSON { "to": "fcmToken", "title": "title", "body": "body" }
 *
 * Notes:
 * - Replace "https://your-backend.com/sendNotification" with your FCM server endpoint.
 * - Requires activity_leaderboard.xml layout.
 * - Assumes BaseActivity provides setContentView with getLayoutResourceId().
 */

package com.example.footzone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderboardActivity extends BaseActivity {
    private static final String TAG = "LeaderboardActivity";
    private TextView userPointsTextView;
    private MaterialCardView userPointsCard;
    private RecyclerView leaderboardRecyclerView;
    private DatabaseReference usersRef;
    private SharedPreferences prefs;
    private String username;
    private LeaderboardAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Leaderboard");
        }

        // Initialize UI
        userPointsTextView = findViewById(R.id.user_points_text);
        userPointsCard = findViewById(R.id.user_points_card);
        leaderboardRecyclerView = findViewById(R.id.leaderboard_recycler_view);

        // Initialize Firebase and SharedPreferences
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        username = prefs.getString("username", null);

        if (username == null) {
            Toast.makeText(this, "Please log in to view the leaderboard", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set up RecyclerView
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LeaderboardAdapter(userList);
        leaderboardRecyclerView.setAdapter(adapter);

        // Load data and animate card
        loadLeaderboardData();
        userPointsCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_leaderboard;
    }

    private void loadLeaderboardData() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                long userPoints = 0;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userName = userSnapshot.getKey();
                    Long points = userSnapshot.child("points").getValue(Long.class);
                    if (points == null) points = 0L;

                    User user = new User(userName, points);
                    userList.add(user);

                    if (userName.equals(username)) {
                        userPoints = points;
                    }
                }

                // Sort users by points (descending)
                Collections.sort(userList, (u1, u2) -> Long.compare(u2.points, u1.points));

                // Update UI
                userPointsTextView.setText(getString(R.string.your_points, userPoints));
                adapter.notifyDataSetChanged();

                // Send notification if points changed
                sendLeaderboardUpdateNotification(userPoints);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading leaderboard: " + error.getMessage());
                Toast.makeText(LeaderboardActivity.this, "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendLeaderboardUpdateNotification(long userPoints) {
        usersRef.child(username).child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fcmToken = snapshot.getValue(String.class);
                if (fcmToken == null || fcmToken.isEmpty()) {
                    Log.e(TAG, "FCM token not found for user: " + username);
                    return;
                }

                String title = "Leaderboard Update";
                String body = "Your points: " + userPoints + ". Check your rank on the leaderboard!";

                executorService.execute(() -> {
                    try {
                        URL url = new URL("https://your-backend.com/sendNotification");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);

                        JSONObject json = new JSONObject();
                        json.put("to", fcmToken);
                        json.put("title", title);
                        json.put("body", body);

                        OutputStream os = conn.getOutputStream();
                        os.write(json.toString().getBytes());
                        os.flush();
                        os.close();

                        int responseCode = conn.getResponseCode();
                        if (responseCode != 200) {
                            Log.e(TAG, "Failed to send FCM notification, response code: " + responseCode);
                        }
                        conn.disconnect();
                    } catch (Exception e) {
                        Log.e(TAG, "Error sending FCM notification: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching FCM token: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    // User model class
    private static class User {
        String username;
        long points;

        User(String username, long points) {
            this.username = username;
            this.points = points;
        }
    }

    // RecyclerView Adapter
    private class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
        private final List<User> users;

        LeaderboardAdapter(List<User> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_leaderboard, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = users.get(position);
            holder.rankTextView.setText(String.valueOf(position + 1));
            holder.usernameTextView.setText(user.username);
            holder.pointsTextView.setText(String.valueOf(user.points));

            // Highlight current user
            if (user.username.equals(username)) {
                holder.itemView.setBackgroundResource(R.drawable.leaderboard_item_highlight);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.leaderboard_item_background);
            }
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView rankTextView, usernameTextView, pointsTextView;

            ViewHolder(View itemView) {
                super(itemView);
                rankTextView = itemView.findViewById(R.id.rank_text);
                usernameTextView = itemView.findViewById(R.id.username_text);
                pointsTextView = itemView.findViewById(R.id.points_text);
            }
        }
    }
}