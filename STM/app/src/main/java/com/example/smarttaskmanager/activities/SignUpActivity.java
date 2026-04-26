package com.example.smarttaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.smarttaskmanager.dto.User;
import com.example.smarttaskmanager.viewmodel.SignUpViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel viewModel;

    private TextInputEditText fullNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText passwordConfirmInput;
    private CheckBox agreementCheckbox;
    private Button createAccountButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind views
        fullNameInput        = findViewById(R.id.signupFullNameInput);
        emailInput           = findViewById(R.id.signupEmailInput);
        passwordInput        = findViewById(R.id.signupPasswordInput);
        passwordConfirmInput = findViewById(R.id.signupPasswordConfirmInput);
        agreementCheckbox    = findViewById(R.id.signupAgreement);
        createAccountButton  = findViewById(R.id.signupCreateAccountButton);
        progressBar          = findViewById(R.id.signupProgressBar);

        // Init ViewModel
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // Observe state
        viewModel.getSignUpState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    createAccountButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    createAccountButton.setEnabled(true);
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                    finish();
                    break;

                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    createAccountButton.setEnabled(true);
                    break;

                case IDLE:
                default:
                    progressBar.setVisibility(View.GONE);
                    createAccountButton.setEnabled(true);
                    break;
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        // Navigate back to Sign In
        TextView signInButton = findViewById(R.id.signupTextButton);
        signInButton.setOnClickListener(v -> finish());

        // Sign Up button
        createAccountButton.setOnClickListener(v -> {
            String fullName    = fullNameInput.getText() != null ? fullNameInput.getText().toString() : "";
            String email       = emailInput.getText() != null ? emailInput.getText().toString() : "";
            String password    = passwordInput.getText() != null ? passwordInput.getText().toString() : "";
            String passwordConf = passwordConfirmInput.getText() != null ? passwordConfirmInput.getText().toString() : "";
            boolean agreement  = agreementCheckbox.isChecked();

            viewModel.signUp(fullName, email, password, passwordConf, agreement);
        });

    }
}