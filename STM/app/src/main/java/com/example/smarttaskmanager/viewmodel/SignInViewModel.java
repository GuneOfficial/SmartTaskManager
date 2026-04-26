package com.example.smarttaskmanager.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smarttaskmanager.dto.User;
import com.example.smarttaskmanager.repository.UserRepository;

public class SignInViewModel extends ViewModel {

    public enum SignInState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final MutableLiveData<SignInState> signInState = new MutableLiveData<>(SignInState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> signedInUser = new MutableLiveData<>();

    private final UserRepository userRepository = new UserRepository();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public LiveData<SignInState> getSignInState() {
        return signInState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getSignedInUser() {
        return signedInUser;
    }

    public void signIn(String email, String password) {

        // --- Validation ---
        if (email.trim().isEmpty()) {
            errorMessage.setValue("Email is required.");
            signInState.setValue(SignInState.ERROR);
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMessage.setValue("Please enter a valid email address.");
            signInState.setValue(SignInState.ERROR);
            return;
        }
        if (password.isEmpty()) {
            errorMessage.setValue("Password is required.");
            signInState.setValue(SignInState.ERROR);
            return;
        }

        signInState.setValue(SignInState.LOADING);

        userRepository.loginUser(email.trim(), password, new UserRepository.SignInCallback() {
            @Override
            public void onSuccess(User user) {
                mainHandler.post(() -> {
                    signedInUser.setValue(user);
                    signInState.setValue(SignInState.SUCCESS);
                });
            }

            @Override
            public void onFailure(String error) {
                mainHandler.post(() -> {
                    errorMessage.setValue(error);
                    signInState.setValue(SignInState.ERROR);
                });
            }
        });
    }
}