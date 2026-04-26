package com.example.smarttaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.utils.SessionManager;
import com.example.smarttaskmanager.viewmodel.SignInViewModel;

public class SignInActivity extends AppCompatActivity {

    private SignInViewModel viewModel;
    private SessionManager sessionManager;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ---- Bind views ----
        emailEditText    = findViewById(R.id.signinEmailEditText);
        passwordEditText = findViewById(R.id.signinPasswordEditText);
        signInButton     = findViewById(R.id.signinButton1);
        progressBar      = findViewById(R.id.signinProgressBar);

        sessionManager = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        viewModel.getSignInState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    signInButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    signInButton.setEnabled(true);
                    break;

                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    signInButton.setEnabled(true);
                    break;

                case IDLE:
                default:
                    progressBar.setVisibility(View.GONE);
                    signInButton.setEnabled(true);
                    break;
            }
        });

        viewModel.getSignedInUser().observe(this, user -> {
            if (user != null) {
                sessionManager.saveUser(user);
                startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        TextView signUpLink = findViewById(R.id.signinTextView7);
        signUpLink.setOnClickListener(v ->
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));

        // ---- SignIn click listener ----
        signInButton.setOnClickListener(v -> {
            String email    = emailEditText.getText() != null ? emailEditText.getText().toString() : "";
            String password = passwordEditText.getText() != null ? passwordEditText.getText().toString() : "";
            viewModel.signIn(email, password);
        });
    }
}