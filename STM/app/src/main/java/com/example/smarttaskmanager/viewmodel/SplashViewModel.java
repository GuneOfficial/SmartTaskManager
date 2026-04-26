package com.example.smarttaskmanager.viewmodel;

import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smarttaskmanager.utils.SessionManager;

public class SplashViewModel extends AndroidViewModel {

    public enum SplashDestination {
        SIGN_IN,
        DASHBOARD
    }

    private final MutableLiveData<SplashDestination> navigateTo = new MutableLiveData<>();
    private final SessionManager sessionManager;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<SplashDestination> getNavigateTo() {
        return navigateTo;
    }

    public void startTimer() {
        new Handler().postDelayed(() -> {
            if (sessionManager.isLoggedIn()) {
                navigateTo.setValue(SplashDestination.DASHBOARD);
            } else {
                navigateTo.setValue(SplashDestination.SIGN_IN);
            }
        }, 3000);
    }
}