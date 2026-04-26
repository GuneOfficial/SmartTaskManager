package com.example.smarttaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.viewmodel.SplashViewModel;

public class SplashActivity extends AppCompatActivity {

    private SplashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        viewModel.getNavigateTo().observe(this, destination -> {
            if (destination == null) return;
            Intent intent;
            if (destination == SplashViewModel.SplashDestination.DASHBOARD) {
                intent = new Intent(SplashActivity.this, DashboardActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, SignInActivity.class);
            }
            startActivity(intent);
            finish();
        });

        viewModel.startTimer();
    }
}