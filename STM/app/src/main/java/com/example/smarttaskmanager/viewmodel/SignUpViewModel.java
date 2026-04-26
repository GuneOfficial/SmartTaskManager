package com.example.smarttaskmanager.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smarttaskmanager.dto.User;
import com.example.smarttaskmanager.repository.UserRepository;

public class SignUpViewModel extends ViewModel {

    public enum SignUpState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final MutableLiveData<SignUpState> signUpState = new MutableLiveData<>(SignUpState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> createdUser = new MutableLiveData<>();

    private final UserRepository userRepository = new UserRepository();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public LiveData<SignUpState> getSignUpState() {
        return signUpState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getCreatedUser() {
        return createdUser;
    }

    public void signUp(String fullName, String email, String password,
                       String confirmPassword, boolean agreement) {

        // --- Validation ---
        if (fullName.trim().isEmpty()) {
            errorMessage.setValue("Full name is required.");
            signUpState.setValue(SignUpState.ERROR);
            return;
        }
        if (email.trim().isEmpty()) {
            errorMessage.setValue("Email is required.");
            signUpState.setValue(SignUpState.ERROR);
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Please enter a valid email address.");
            signUpState.setValue(SignUpState.ERROR);
            return;
        }
        if (password.isEmpty()) {
            errorMessage.setValue("Password is required.");
            signUpState.setValue(SignUpState.ERROR);
            return;
        }
        if (password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters.");
            signUpState.setValue(SignUpState.ERROR);
            return;
        }
        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Passwords do not match.");
            signUpState.setValue(SignUpState.ERROR);
            return;
        }
        if (!agreement) {
            errorMessage.setValue("You must agree to the Terms of Service and Privacy Policy.");
            signUpState.setValue(SignUpState.ERROR);
            return;
        }

        signUpState.setValue(SignUpState.LOADING);

        long createdAt = System.currentTimeMillis();
        User user = new User(fullName.trim(), email.trim(), password, agreement, createdAt);

        userRepository.registerUser(user, new UserRepository.SignUpCallback() {
            @Override
            public void onSuccess(User user) {
                mainHandler.post(() -> {
                    createdUser.setValue(user);
                    signUpState.setValue(SignUpState.SUCCESS);
                });
            }

            @Override
            public void onFailure(String error) {
                mainHandler.post(() -> {
                    errorMessage.setValue(error);
                    signUpState.setValue(SignUpState.ERROR);
                });
            }
        });
    }
}