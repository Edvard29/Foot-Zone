package com.example.footzone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base); // Используем общий layout

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            Log.e("BaseActivity", "Toolbar not found in layout");
            throw new IllegalStateException("Toolbar with id 'toolbar' not found");
        }
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            Log.e("BaseActivity", "DrawerLayout not found in layout");
            throw new IllegalStateException("DrawerLayout with id 'drawer_layout' not found");
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView == null) {
            Log.e("BaseActivity", "NavigationView not found in layout");
            throw new IllegalStateException("NavigationView with id 'nav_view' not found");
        }
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Устанавливаем содержимое FrameLayout
        getLayoutInflater().inflate(getLayoutResourceId(), findViewById(R.id.content_frame));
    }

    protected int getLayoutResourceId() {
        return R.layout.activity_main; // По умолчанию
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        }else if (id == R.id.nav_user) {
            startActivity(new Intent(this, UserActivity.class));
        }
        else if (id == R.id.nav_squad) {
            startActivity(new Intent(this, SquadActivity.class));
        } else if (id == R.id.nav_standigs) {
            startActivity(new Intent(this, StandingsActivity.class));
        }
        else if (id == R.id.nav_select_team) {
            startActivity(new Intent(this, SelectTeamActivity.class));
        }else if (id == R.id.nav_scorers) {
            startActivity(new Intent(this, TopScorersActivity.class));
        }else if (id == R.id.nav_assists) {
            startActivity(new Intent(this, AssistLeadersActivity.class));
        }else if (id == R.id.nav_leader) {
            startActivity(new Intent(this, LeaderboardActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}