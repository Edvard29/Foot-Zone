package com.example.footzone;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.example.footzone.network.ApiResponseCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.footzone.model.Prediction;
import com.example.footzone.network.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PredictionActivity extends AppCompatActivity {

    private TextView matchTitle;
    private EditText homeGoals, awayGoals;
    private Spinner homePlayer1, homePlayer2, awayPlayer1, awayPlayer2;
    private Button submitButton;
    private DatabaseReference predictionsRef;
    private SharedPreferences prefs;
    private int fixtureId;
    private String matchTitleText, matchDate, matchStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        matchTitle = findViewById(R.id.match_title);
        homeGoals = findViewById(R.id.home_goals);
        awayGoals = findViewById(R.id.away_goals);
        homePlayer1 = findViewById(R.id.home_player_1);
        homePlayer2 = findViewById(R.id.home_player_2);
        awayPlayer1 = findViewById(R.id.away_player_1);
        awayPlayer2 = findViewById(R.id.away_player_2);
        submitButton = findViewById(R.id.submit_prediction);

        fixtureId = getIntent().getIntExtra("fixtureId", -1);
        matchTitleText = getIntent().getStringExtra("matchTitle");
        matchDate = getIntent().getStringExtra("matchDate");
        matchStatus = getIntent().getStringExtra("matchStatus");
        matchTitle.setText(matchTitleText);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        predictionsRef = FirebaseDatabase.getInstance().getReference("predictions").child(String.valueOf(fixtureId));

        if (!canMakePrediction()) {
            Toast.makeText(this, "Predictions are only available before the match starts!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        checkPredictionLimit();
        checkIfPredictionMade();

        // Загрузка составов по fixtureId
        loadSquadsByFixture(fixtureId);

        submitButton.setOnClickListener(v -> submitPrediction());
    }

    private boolean canMakePrediction() {
        if (!"NS".equals(matchStatus)) {
            return false;
        }
        try {
            Log.d("PredictionActivity", "matchDate: " + matchDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date matchTime = sdf.parse(matchDate);
            Date currentTime = new Date();
            return currentTime.before(matchTime);
        } catch (Exception e) {
            Log.e("PredictionActivity", "Date parse error: " + e.getMessage());
            return false;
        }
    }

    private void checkPredictionLimit() {
        int predictionCount = prefs.getInt("predictionCount", 0);
        if (predictionCount >= 5) {
            Toast.makeText(this, "You have reached the prediction limit of 5 matches!", Toast.LENGTH_LONG).show();
            submitButton.setEnabled(false);
            finish();
        }
    }

    private void checkIfPredictionMade() {
        String userId = prefs.getString("username", "Anonymous");
        predictionsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(PredictionActivity.this, "You have already made your choice!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PredictionActivity", "Error checking prediction: " + databaseError.getMessage());
            }
        });
    }

    private void loadSquadsByFixture(int fixtureId) {
        List<String> homePlayers = new ArrayList<>();
        List<String> awayPlayers = new ArrayList<>();

        Log.d("PredictionActivity", "Loading squads for fixtureId: " + fixtureId);
        ApiClient.getLineups(fixtureId, new ApiResponseCaлllback() {
            @Override
            public void onSuccess(String jsonData) {
                Log.d("PredictionActivity", "Lineups data received: " + jsonData.substring(0, Math.min(jsonData.length(), 100)));
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray responseArray = jsonObject.getJSONArray("response");
                    Log.d("PredictionActivity", "Response array length: " + responseArray.length());

                    if (responseArray.length() >= 2) {
                        JSONObject homeTeam = responseArray.getJSONObject(0);
                        JSONObject awayTeam = responseArray.getJSONObject(1);

                        JSONArray homeStartXI = homeTeam.getJSONArray("startXI");
                        Log.d("PredictionActivity", "Home team players count: " + homeStartXI.length());
                        for (int i = 0; i < homeStartXI.length(); i++) {
                            JSONObject player = homeStartXI.getJSONObject(i).getJSONObject("player");
                            String playerName = player.getString("name");
                            homePlayers.add(playerName);
                            Log.d("PredictionActivity", "Added home player: " + playerName);
                        }

                        JSONArray awayStartXI = awayTeam.getJSONArray("startXI");
                        Log.d("PredictionActivity", "Away team players count: " + awayStartXI.length());
                        for (int i = 0; i < awayStartXI.length(); i++) {
                            JSONObject player = awayStartXI.getJSONObject(i).getJSONObject("player");
                            String playerName = player.getString("name");
                            awayPlayers.add(playerName);
                            Log.d("PredictionActivity", "Added away player: " + playerName);
                        }

                        runOnUiThread(() -> {
                            if (homePlayers.isEmpty()) {
                                homePlayers.add("No players available");
                            }
                            if (awayPlayers.isEmpty()) {
                                awayPlayers.add("No players available");
                            }

                            ArrayAdapter<String> homeAdapter = new ArrayAdapter<>(PredictionActivity.this, android.R.layout.simple_spinner_item, homePlayers);
                            homeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            homePlayer1.setAdapter(homeAdapter);
                            homePlayer2.setAdapter(homeAdapter);
                            Log.d("PredictionActivity", "Home adapter set with " + homePlayers.size() + " players");

                            ArrayAdapter<String> awayAdapter = new ArrayAdapter<>(PredictionActivity.this, android.R.layout.simple_spinner_item, awayPlayers);
                            awayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            awayPlayer1.setAdapter(awayAdapter);
                            awayPlayer2.setAdapter(awayAdapter);
                            Log.d("PredictionActivity", "Away adapter set with " + awayPlayers.size() + " players");
                        });
                    } else {
                        Log.w("PredictionActivity", "Not enough teams in response");
                        runOnUiThread(() -> Toast.makeText(PredictionActivity.this, "Squads not available for this match", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    Log.e("PredictionActivity", "Error parsing lineups: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(PredictionActivity.this, "Failed to load squads", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("PredictionActivity", "Error fetching lineups: " + errorMessage);
                runOnUiThread(() -> {
                    Toast.makeText(PredictionActivity.this, "Error fetching squads", Toast.LENGTH_SHORT).show();
                    // Устанавливаем заглушку, если данные не загрузились
                    homePlayers.add("No players available");
                    awayPlayers.add("No players available");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PredictionActivity.this, android.R.layout.simple_spinner_item, homePlayers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    homePlayer1.setAdapter(adapter);
                    homePlayer2.setAdapter(adapter);
                    awayPlayer1.setAdapter(adapter);
                    awayPlayer2.setAdapter(adapter);
                });
            }
        });
    }

    private void submitPrediction() {
        String userId = prefs.getString("username", "Anonymous");
        String homeGoalsText = homeGoals.getText().toString().trim();
        String awayGoalsText = awayGoals.getText().toString().trim();

        if (homeGoalsText.isEmpty() || awayGoalsText.isEmpty()) {
            Toast.makeText(this, "Please predict the score", Toast.LENGTH_SHORT).show();
            return;
        }

        int homeGoalsValue = Integer.parseInt(homeGoalsText);
        int awayGoalsValue = Integer.parseInt(awayGoalsText);
        String homeLineup = homePlayer1.getSelectedItem() + "\n" + homePlayer2.getSelectedItem();
        String awayLineup = awayPlayer1.getSelectedItem() + "\n" + awayPlayer2.getSelectedItem();

        Prediction prediction = new Prediction(userId, fixtureId, homeGoalsValue, awayGoalsValue, homeLineup, awayLineup, System.currentTimeMillis());
        predictionsRef.child(userId).setValue(prediction).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                incrementPredictionCount();
                Toast.makeText(this, "Prediction submitted!", Toast.LENGTH_SHORT).show();
                listenForMatchResult();
                finish();
            } else {
                Toast.makeText(this, "Failed to submit prediction", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void incrementPredictionCount() {
        int predictionCount = prefs.getInt("predictionCount", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("predictionCount", predictionCount + 1);
        editor.apply();
    }

    private void listenForMatchResult() {
        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("matches").child(String.valueOf(fixtureId));
        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer actualHomeGoals = dataSnapshot.child("homeGoals").getValue(Integer.class);
                    Integer actualAwayGoals = dataSnapshot.child("awayGoals").getValue(Integer.class);
                    String actualHomeLineup = dataSnapshot.child("homeLineup").getValue(String.class);
                    String actualAwayLineup = dataSnapshot.child("awayLineup").getValue(String.class);

                    if (actualHomeGoals != null && actualAwayGoals != null) {
                        checkPredictionResult(actualHomeGoals, actualAwayGoals, actualHomeLineup, actualAwayLineup);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PredictionActivity", "Error listening for match result: " + databaseError.getMessage());
            }
        });
    }

    private void checkPredictionResult(int actualHomeGoals, int actualAwayGoals, String actualHomeLineup, String actualAwayLineup) {
        String userId = prefs.getString("username", "Anonymous");
        predictionsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Prediction prediction = dataSnapshot.getValue(Prediction.class);
                if (prediction != null) {
                    boolean scoreCorrect = prediction.getHomeGoals() == actualHomeGoals && prediction.getAwayGoals() == actualAwayGoals;
                    boolean lineupCorrect = prediction.getHomeLineup().equals(actualHomeLineup) && prediction.getAwayLineup().equals(actualAwayLineup);

                    int points = 0;
                    if (scoreCorrect) points += 10;
                    if (lineupCorrect) points += 20;

                    updateUserPoints(points);
                    sendNotification(scoreCorrect, lineupCorrect, points);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PredictionActivity", "Error checking prediction result: " + databaseError.getMessage());
            }
        });
    }

    private void updateUserPoints(int points) {
        if (points > 0) {
            DatabaseReference userPointsRef = FirebaseDatabase.getInstance().getReference("users").child(prefs.getString("username", "Anonymous")).child("points");
            userPointsRef.setValue(points + prefs.getInt("points", 0));
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("points", points + prefs.getInt("points", 0));
            editor.apply();
        }
    }

    private void sendNotification(boolean scoreCorrect, boolean lineupCorrect, int points) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "prediction_results";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Prediction Results", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        String message = "Match: " + matchTitleText + "\n";
        message += "Score Prediction: " + (scoreCorrect ? "Correct" : "Incorrect") + "\n";
        message += "Lineup Prediction: " + (lineupCorrect ? "Correct" : "Incorrect") + "\n";
        message += "Points Earned: " + points;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Prediction Result")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(fixtureId, builder.build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}