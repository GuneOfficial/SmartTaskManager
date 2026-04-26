package com.example.smarttaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.dto.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

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

        TextView signInButton = findViewById(R.id.signupTextButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        Button signUpButton = findViewById(R.id.signupCreateAccountButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText fullName = findViewById(R.id.signupFullNameInput);
                TextInputEditText email = findViewById(R.id.signupEmailInput);
                TextInputEditText password = findViewById(R.id.signupPasswordInput);
                TextInputEditText passwordConf = findViewById(R.id.signupPasswordConfirmInput);
                CheckBox agreementCheckbox = findViewById(R.id.signupAgreement);

                long createdAt = System.currentTimeMillis();

                Log.d("STM-SignUp: fullname",fullName.getText().toString());
                Log.d("STM-SignUp: email",email.getText().toString());
                Log.d("STM-SignUp: password",password.getText().toString());
                Log.d("STM-SignUp: passwordConf",passwordConf.getText().toString());
                Log.d("STM-SignUp: agreementCheckbox",String.valueOf(agreementCheckbox.isChecked()));
                Log.d("STM-SignUp: createdAt",String.valueOf(createdAt));
                Log.d("STM-SignUp: createdAt-date",String.valueOf(new Date(createdAt)));

                User user = new User(fullName.getText().toString(),email.getText().toString(),password.getText().toString(),agreementCheckbox.isChecked(),createdAt);

                Gson gson = new Gson();
                Log.d("STM-SignUp: createdAt",gson.toJson(user));

            }
        });

    }
}