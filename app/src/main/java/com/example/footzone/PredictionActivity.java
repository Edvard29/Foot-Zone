package com.example.footzone;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.footzone.adapter.PredPlayerAdapter;
import com.example.footzone.model.PredPlayer;
import com.example.footzone.model.Prediction;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.ApiResponseCallback;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PredictionActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView matchTitle;
    private EditText homeGoals, awayGoals;
    private RecyclerView homeLineupRecyclerView, awayLineupRecyclerView;
    private Button submitButton;
    private ProgressBar progressBar;
    private ImageView homeTeamLogo, awayTeamLogo;
    private DatabaseReference predictionsRef, usersRef;
    private SharedPreferences prefs;
    private int fixtureId;
    private String matchTitleText, matchDate, matchStatus, username, userEmail;
    private List<PredPlayer> homePlayers = new ArrayList<>();
    private List<PredPlayer> awayPlayers = new ArrayList<>();
    private List<Integer> selectedHomePlayers = new ArrayList<>();
    private List<Integer> selectedAwayPlayers = new ArrayList<>();
    private int homeTeamId, awayTeamId;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_prediction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UI elements
        matchTitle = findViewById(R.id.match_title);
        homeGoals = findViewById(R.id.home_goals);
        awayGoals = findViewById(R.id.away_goals);
        homeLineupRecyclerView = findViewById(R.id.home_lineup_recycler_view);
        awayLineupRecyclerView = findViewById(R.id.away_lineup_recycler_view);
        submitButton = findViewById(R.id.submit_prediction);
        progressBar = findViewById(R.id.progress_bar);
        homeTeamLogo = findViewById(R.id.home_team_logo);
        awayTeamLogo = findViewById(R.id.away_team_logo);

        // Set up NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Log.e("PredictionActivity", "NavigationView not found in layout");
        }

        // Get data from Intent
        fixtureId = getIntent().getIntExtra("fixtureId", -1);
        matchTitleText = getIntent().getStringExtra("matchTitle");
        matchDate = getIntent().getStringExtra("matchDate");
        matchStatus = getIntent().getStringExtra("matchStatus");
        homeTeamId = getIntent().getIntExtra("homeTeamId", -1);
        awayTeamId = getIntent().getIntExtra("awayTeamId", -1);

        // Validate fixtureId
        if (fixtureId == -1) {
            Log.e("PredictionActivity", "Invalid fixtureId received in Intent");
            Toast.makeText(this, "Invalid match data. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        matchTitle.setText(matchTitleText != null ? matchTitleText : "Match");

        // Initialize SharedPreferences and Firebase
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        predictionsRef = FirebaseDatabase.getInstance().getReference("predictions").child(String.valueOf(fixtureId));
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        username = prefs.getString("username", null);
        if (username == null) {
            showUsernameDialog();
        } else {
            loadUserEmailAndInitialize();
        }
    }

    private void showUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String enteredUsername = input.getText().toString().trim();
            if (enteredUsername.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                showUsernameDialog();
            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", enteredUsername);
                editor.apply();
                username = enteredUsername;
                usersRef.child(username).child("registered").setValue(true);
                loadUserEmailAndInitialize();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void loadUserEmailAndInitialize() {
        userEmail = prefs.getString("email", null);
        if (userEmail != null && !userEmail.isEmpty()) {
            initializeActivity();
        } else {
            usersRef.child(username).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userEmail = dataSnapshot.getValue(String.class);
                    if (userEmail == null || userEmail.isEmpty()) {
                        Toast.makeText(PredictionActivity.this, "Email not found. Please log in again.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("email", userEmail);
                        editor.apply();
                        initializeActivity();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("PredictionActivity", "Error loading email: " + databaseError.getMessage());
                    Toast.makeText(PredictionActivity.this, "Error loading email", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void initializeActivity() {
        if (!canMakePrediction()) {
            matchTitle.setText("This match has already started or finished!");
            homeGoals.setVisibility(View.GONE);
            awayGoals.setVisibility(View.GONE);
            homeLineupRecyclerView.setVisibility(View.GONE);
            awayLineupRecyclerView.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
            homeTeamLogo.setVisibility(View.GONE);
            awayTeamLogo.setVisibility(View.GONE);
            return;
        }

        checkPredictionLimit();
        checkIfPredictionMade();

        homeLineupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        awayLineupRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        PredPlayerAdapter homeAdapter = new PredPlayerAdapter(homePlayers, selectedHomePlayers,
                (playerId, isSelected) -> {
                    if (isSelected) {
                        if (selectedHomePlayers.size() < 11) {
                            selectedHomePlayers.add(playerId);
                        } else {
                            Toast.makeText(this, "Max 11 players for home team", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        selectedHomePlayers.remove(Integer.valueOf(playerId));
                    }
                });
        PredPlayerAdapter awayAdapter = new PredPlayerAdapter(awayPlayers, selectedAwayPlayers,
                (playerId, isSelected) -> {
                    if (isSelected) {
                        if (selectedAwayPlayers.size() < 11) {
                            selectedAwayPlayers.add(playerId);
                        } else {
                            Toast.makeText(this, "Max 11 players for away team", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        selectedAwayPlayers.remove(Integer.valueOf(playerId));
                    }
                });

        homeLineupRecyclerView.setAdapter(homeAdapter);
        awayLineupRecyclerView.setAdapter(awayAdapter);

        submitButton.setOnClickListener(v -> submitPrediction());

        loadSquads();
        loadTeamLogos();
    }

    private boolean canMakePrediction() {
        if (!"NS".equals(matchStatus)) {
            return false;
        }
        try {
            @SuppressLint({"NewApi", "LocalSuppress"}) SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            Date matchTime = sdf.parse(matchDate);
            Date currentTime = new Date();
            return currentTime.before(matchTime);
        } catch (Exception e) {
            Log.e("PredictionActivity", "Date parse error: " + e.getMessage());
            return false;
        }
    }

    private void checkPredictionLimit() {
        usersRef.child(username).child("predictionCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long predictionCount = dataSnapshot.getValue(Long.class);
                int count = predictionCount != null ? predictionCount.intValue() : 0;
                if (count >= 15) {
                    matchTitle.setText("You have reached the prediction limit of 15 matches!");
                    homeGoals.setVisibility(View.GONE);
                    awayGoals.setVisibility(View.GONE);
                    homeLineupRecyclerView.setVisibility(View.GONE);
                    awayLineupRecyclerView.setVisibility(View.GONE);
                    submitButton.setVisibility(View.GONE);
                    homeTeamLogo.setVisibility(View.GONE);
                    awayTeamLogo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PredictionActivity", "Error checking prediction limit: " + databaseError.getMessage());
            }
        });
    }

    private void checkIfPredictionMade() {
        predictionsRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    matchTitle.setText("You have already made your choice!");
                    homeGoals.setVisibility(View.GONE);
                    awayGoals.setVisibility(View.GONE);
                    homeLineupRecyclerView.setVisibility(View.GONE);
                    awayLineupRecyclerView.setVisibility(View.GONE);
                    submitButton.setVisibility(View.GONE);
                    homeTeamLogo.setVisibility(View.GONE);
                    awayTeamLogo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PredictionActivity", "Error checking prediction: " + databaseError.getMessage());
            }
        });
    }

    private void loadTeamLogos() {
        ApiClient.getFixtureEvents(fixtureId, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject fixture = jsonObject.getJSONArray("response").getJSONObject(0);
                    JSONObject teams = fixture.getJSONObject("teams");
                    String homeLogoUrl = teams.getJSONObject("home").getString("logo");
                    String awayLogoUrl = teams.getJSONObject("away").getString("logo");

                    runOnUiThread(() -> {
                        Glide.with(PredictionActivity.this)
                                .load(homeLogoUrl)
                                .placeholder(R.drawable.ic_default_team_logo)
                                .error(R.drawable.ic_default_team_logo)
                                .into(homeTeamLogo);
                        Glide.with(PredictionActivity.this)
                                .load(awayLogoUrl)
                                .placeholder(R.drawable.ic_default_team_logo)
                                .error(R.drawable.ic_default_team_logo)
                                .into(awayTeamLogo);
                    });
                } catch (Exception e) {
                    Log.e("PredictionActivity", "Error parsing team logos: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(PredictionActivity.this, "Failed to load team logos", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("PredictionActivity", "Error fetching team logos: " + errorMessage);
                runOnUiThread(() -> Toast.makeText(PredictionActivity.this, "Error fetching team logos", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadSquads() {
        progressBar.setVisibility(View.VISIBLE);

        ApiClient.getSquad(homeTeamId, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray playersArray = jsonObject.getJSONArray("response").getJSONObject(0).getJSONArray("players");
                    homePlayers.clear();
                    for (int i = 0; i < playersArray.length(); i++) {
                        JSONObject playerObj = playersArray.getJSONObject(i);
                        int id = playerObj.getInt("id");
                        String name = playerObj.getString("name");
                        String imageUrl = playerObj.optString("photo", "");
                        String position = playerObj.optString("position", "Unknown");
                        homePlayers.add(new PredPlayer(id, name, imageUrl, position));
                    }
                    runOnUiThread(() -> homeLineupRecyclerView.getAdapter().notifyDataSetChanged());
                } catch (Exception e) {
                    Log.e("PredictionActivity", "Error parsing home squad: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(PredictionActivity.this, "Failed to load home squad", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("PredictionActivity", "Error fetching home squad: " + errorMessage);
                runOnUiThread(() -> Toast.makeText(PredictionActivity.this, "Error fetching home squad", Toast.LENGTH_SHORT).show());
            }
        });

        ApiClient.getSquad(awayTeamId, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray playersArray = jsonObject.getJSONArray("response").getJSONObject(0).getJSONArray("players");
                    awayPlayers.clear();
                    for (int i = 0; i < playersArray.length(); i++) {
                        JSONObject playerObj = playersArray.getJSONObject(i);
                        int id = playerObj.getInt("id");
                        String name = playerObj.getString("name");
                        String imageUrl = playerObj.optString("photo", "");
                        String position = playerObj.optString("position", "Unknown");
                        awayPlayers.add(new PredPlayer(id, name, imageUrl, position));
                    }
                    runOnUiThread(() -> {
                        awayLineupRecyclerView.getAdapter().notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    });
                } catch (Exception e) {
                    Log.e("PredictionActivity", "Error parsing away squad: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(PredictionActivity.this, "Failed to load away squad", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("PredictionActivity", "Error fetching away squad: " + errorMessage);
                runOnUiThread(() -> {
                    Toast.makeText(PredictionActivity.this, "Error fetching away squad", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void submitPrediction() {
        String homeGoalsText = homeGoals.getText().toString().trim();
        String awayGoalsText = awayGoals.getText().toString().trim();

        if (homeGoalsText.isEmpty() || awayGoalsText.isEmpty()) {
            Toast.makeText(this, "Please predict the score", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedHomePlayers.size() != 11 || selectedAwayPlayers.size() != 11) {
            Toast.makeText(this, "Please select exactly 11 players for each team", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for exactly one goalkeeper in each team
        int homeGoalkeeperCount = 0;
        int awayGoalkeeperCount = 0;

        for (Integer playerId : selectedHomePlayers) {
            for (PredPlayer player : homePlayers) {
                if (player.getId() == playerId && "Goalkeeper".equals(player.getPosition())) {
                    homeGoalkeeperCount++;
                    break;
                }
            }
        }

        for (Integer playerId : selectedAwayPlayers) {
            for (PredPlayer player : awayPlayers) {
                if (player.getId() == playerId && "Goalkeeper".equals(player.getPosition())) {
                    awayGoalkeeperCount++;
                    break;
                }
            }
        }

        if (homeGoalkeeperCount != 1) {
            Toast.makeText(this, "Please select exactly one goalkeeper for the home team", Toast.LENGTH_SHORT).show();
            return;
        }

        if (awayGoalkeeperCount != 1) {
            Toast.makeText(this, "Please select exactly one goalkeeper for the away team", Toast.LENGTH_SHORT).show();
            return;
        }

        int homeGoalsValue = Integer.parseInt(homeGoalsText);
        int awayGoalsValue = Integer.parseInt(awayGoalsText);
        String homeLineupJson = new JSONArray(selectedHomePlayers).toString();
        String awayLineupJson = new JSONArray(selectedAwayPlayers).toString();

        Prediction prediction = new Prediction(username, fixtureId, homeGoalsValue, awayGoalsValue, homeLineupJson, awayLineupJson, System.currentTimeMillis());
        predictionsRef.child(username).setValue(prediction).addOnCompleteListener(task -> {
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
        usersRef.child(username).child("predictionCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long predictionCount = dataSnapshot.getValue(Long.class);
                int count = predictionCount != null ? predictionCount.intValue() : 0;
                usersRef.child(username).child("predictionCount").setValue(count + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PredictionActivity", "Error incrementing prediction count: " + databaseError.getMessage());
            }
        });
    }

    private void listenForMatchResult() {
        ApiClient.getMatchResult(fixtureId, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray responseArray = jsonObject.getJSONArray("response");

                    if (responseArray.length() >= 2) {
                        JSONObject homeTeamObj = responseArray.getJSONObject(0);
                        JSONObject awayTeamObj = responseArray.getJSONObject(1);
                        JSONObject goals = jsonObject.optJSONObject("goals");
                        int actualHomeGoals = goals != null && goals.has("home") ? goals.getJSONObject("home").getInt("total") : -1;
                        int actualAwayGoals = goals != null && goals.has("away") ? goals.getJSONObject("away").getInt("total") : -1;

                        List<Integer> actualHomeLineup = new ArrayList<>();
                        JSONArray homeStartXI = homeTeamObj.getJSONArray("startXI");
                        for (int i = 0; i < homeStartXI.length(); i++) {
                            actualHomeLineup.add(homeStartXI.getJSONObject(i).getJSONObject("player").getInt("id"));
                        }

                        List<Integer> actualAwayLineup = new ArrayList<>();
                        JSONArray awayStartXI = awayTeamObj.getJSONArray("startXI");
                        for (int i = 0; i < awayStartXI.length(); i++) {
                            actualAwayLineup.add(awayStartXI.getJSONObject(i).getJSONObject("player").getInt("id"));
                        }

                        checkPredictionResult(actualHomeGoals, actualAwayGoals, actualHomeLineup, actualAwayLineup);
                    }
                } catch (Exception e) {
                    Log.e("PredictionActivity", "Error parsing match result: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("PredictionActivity", "Error fetching match result: " + errorMessage);
            }
        });
    }

    private void checkPredictionResult(int actualHomeGoals, int actualAwayGoals, List<Integer> actualHomeLineup, List<Integer> actualAwayLineup) {
        predictionsRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Prediction prediction = dataSnapshot.getValue(Prediction.class);
                if (prediction != null) {
                    boolean scoreCorrect = actualHomeGoals != -1 && actualAwayGoals != -1 &&
                            prediction.getHomeGoals() == actualHomeGoals && prediction.getAwayGoals() == actualAwayGoals;

                    JSONArray predHomeJson;
                    try {
                        predHomeJson = new JSONArray(prediction.getHomeLineup());
                    } catch (JSONException e) {
                        Log.e("PredictionActivity", "Error parsing home lineup JSON: " + e.getMessage());
                        return;
                    }
                    JSONArray predAwayJson;
                    try {
                        predAwayJson = new JSONArray(prediction.getAwayLineup());
                    } catch (JSONException e) {
                        Log.e("PredictionActivity", "Error parsing away lineup JSON: " + e.getMessage());
                        return;
                    }
                    List<Integer> predHomeLineup = new ArrayList<>();
                    List<Integer> predAwayLineup = new ArrayList<>();
                    for (int i = 0; i < predHomeJson.length(); i++) {
                        try {
                            predHomeLineup.add(predHomeJson.getInt(i));
                        } catch (JSONException e) {
                            Log.e("PredictionActivity", "Error extracting home lineup: " + e.getMessage());
                        }
                    }
                    for (int i = 0; i < predAwayJson.length(); i++) {
                        try {
                            predAwayLineup.add(predAwayJson.getInt(i));
                        } catch (JSONException e) {
                            Log.e("PredictionActivity", "Error extracting away lineup: " + e.getMessage());
                        }
                    }

                    int correctHomePlayers = 0;
                    for (Integer playerId : predHomeLineup) {
                        if (actualHomeLineup.contains(playerId)) correctHomePlayers++;
                    }
                    int correctAwayPlayers = 0;
                    for (Integer playerId : predAwayLineup) {
                        if (actualAwayLineup.contains(playerId)) correctAwayPlayers++;
                    }

                    int points = 0;
                    if (scoreCorrect) points += 3;
                    points += correctHomePlayers + correctAwayPlayers;

                    updateUserPoints(points);
                    sendEmailNotification(scoreCorrect, correctHomePlayers, correctAwayPlayers, points);
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
            usersRef.child(username).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long currentPoints = dataSnapshot.getValue(Long.class);
                    int totalPoints = currentPoints != null ? currentPoints.intValue() : 0;
                    usersRef.child(username).child("points").setValue(totalPoints + points);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("points", totalPoints + points);
                    editor.apply();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("PredictionActivity", "Error updating points: " + databaseError.getMessage());
                }
            });
        }
    }

    private void sendEmailNotification(boolean scoreCorrect, int correctHomePlayers, int correctAwayPlayers, int points) {
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e("PredictionActivity", "User email is not available");
            return;
        }

        String subject = "Prediction Result: " + matchTitleText;
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(username).append(",\n\n");
        message.append("Match: ").append(matchTitleText).append("\n");
        message.append("Score Prediction: ").append(scoreCorrect ? "Correct (+3 points)" : "Incorrect").append("\n");
        message.append("Home Lineup: ").append(correctHomePlayers).append("/11 correct (+").append(correctHomePlayers).append(" points)\n");
        message.append("Away Lineup: ").append(correctAwayPlayers).append("/11 correct (+").append(correctAwayPlayers).append(" points)\n");
        message.append("Total Points Earned: ").append(points).append("\n\n");
        message.append("Thank you for playing!\nFootZone Team");

        EmailSender.sendPredictionResult(userEmail, subject, message.toString());
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