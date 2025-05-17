package com.example.footzone;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.adapter.TeamSelectionAdapter;
import com.example.footzone.model.Team;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectTeamActivity extends BaseActivity {

    private static final String TAG = "SelectTeamActivity";
    private static final String PREFS_NAME = "FavoriteTeams";
    private static final String CHANNEL_ID = "team_notifications";

    private RecyclerView teamRecyclerView;
    private Button saveButton;
    private TextView selectTeamTitle;
    private TeamSelectionAdapter adapter;
    private List<Team> teams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Favorite Teams");
        }

        // Initialize views
        teamRecyclerView = findViewById(R.id.team_recycler_view);
        saveButton = findViewById(R.id.save_button);
        selectTeamTitle = findViewById(R.id.select_team_title);

        // Initialize team data
        teams = new ArrayList<>();
        teams.add(new Team("Барселона", "https://via.placeholder.com/150/0288D1/FFFFFF?text=Barcelona"));
        teams.add(new Team("Реал Мадрид", "https://via.placeholder.com/150/0288D1/FFFFFF?text=RealMadrid"));
        teams.add(new Team("Атлетико Мадрид", "https://via.placeholder.com/150/0288D1/FFFFFF?text=Atletico"));
        teams.add(new Team("Манчестер Сити", "https://via.placeholder.com/150/0288D1/FFFFFF?text=ManCity"));
        teams.add(new Team("Манчестер Юнайтед", "https://via.placeholder.com/150/0288D1/FFFFFF?text=ManUtd"));
        teams.add(new Team("Арсенал", "https://via.placeholder.com/150/0288D1/FFFFFF?text=Arsenal"));
        teams.add(new Team("Ливерпуль", "https://via.placeholder.com/150/0288D1/FFFFFF?text=Liverpool"));
        teams.add(new Team("Челси", "https://via.placeholder.com/150/0288D1/FFFFFF?text=Chelsea"));
        teams.add(new Team("Бавария", "https://via.placeholder.com/150/0288D1/FFFFFF?text=Bayern"));
        teams.add(new Team("Интер", "https://via.placeholder.com/150/0288D1/FFFFFF?text=Inter"));
        teams.add(new Team("Ювентус", "https://via.placeholder.com/150/0288D1/FFFFFF?text=Juventus"));
        teams.add(new Team("Рома", "https://via.placeholder.com/150/0288D1/FFFFFF?text=Roma"));
        teams.add(new Team("ПСЖ", "https://via.placeholder.com/150/0288D1/FFFFFF?text=PSG"));

        // Load previously selected teams
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> favoriteTeams = prefs.getStringSet("favorites", new HashSet<>());
        for (Team team : teams) {
            team.setSelected(favoriteTeams.contains(team.getName()));
        }

        // Set up RecyclerView
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeamSelectionAdapter(teams);
        teamRecyclerView.setAdapter(adapter);

        // Save button
        saveButton.setOnClickListener(v -> {
            List<String> selectedTeams = new ArrayList<>();
            for (Team team : teams) {
                if (team.isSelected()) {
                    selectedTeams.add(team.getName());
                    subscribeToTeamNotifications(team.getName());
                } else {
                    unsubscribeFromTeamNotifications(team.getName());
                }
            }

            // Save to SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet("favorites", new HashSet<>(selectedTeams));
            editor.apply();

            Log.d(TAG, "Selected teams saved: " + selectedTeams);

            // Send test notification for first selected team
            if (!selectedTeams.isEmpty()) {
                sendTestNotification(selectedTeams.get(0), "Match starting soon!");
            }
        });

        // Set up notification channel
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Team Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for favorite team events");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void subscribeToTeamNotifications(String teamName) {
        FirebaseMessaging.getInstance().subscribeToTopic(teamName.replace(" ", "_"))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to " + teamName);
                    } else {
                        Log.w(TAG, "Failed to subscribe to " + teamName, task.getException());
                    }
                });
    }

    private void unsubscribeFromTeamNotifications(String teamName) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(teamName.replace(" ", "_"))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Unsubscribed from " + teamName);
                    } else {
                        Log.w(TAG, "Failed to unsubscribe from " + teamName, task.getException());
                    }
                });
    }

    private void sendTestNotification(String teamName, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_default_team_logo)
                .setContentTitle(teamName + " Update")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
        Log.d(TAG, "Sent test notification for " + teamName + ": " + message);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_team;
    }
}