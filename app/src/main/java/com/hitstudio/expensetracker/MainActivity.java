package com.hitstudio.expensetracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hitstudio.expensetracker.notify.NotificationHelper;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_OPEN_LOGGER = "com.hitstudio.expensetracker.action.OPEN_LOGGER";

    private ActivityResultLauncher<String> notificationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (!granted) {
                        Toast.makeText(
                                this,
                                "Notifications are disabled. Turn them on to receive expense reminders.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            handleLaunchIntent(getIntent(), navController);
        }

        requestNotificationPermissionIfNeeded();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            handleLaunchIntent(intent, navHostFragment.getNavController());
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void handleLaunchIntent(Intent intent, NavController navController) {
        if (intent == null || navController == null) {
            return;
        }
        if (!intent.getBooleanExtra(NotificationHelper.EXTRA_OPEN_LOGGER, false)) {
            return;
        }
        if (navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == R.id.loggerFragment) {
            return;
        }
        navController.navigate(R.id.loggerFragment);
    }
}
