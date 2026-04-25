package com.example.smarttaskmanager.viewmodel;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SplashViewModel extends ViewModel {

    private MutableLiveData<Boolean> navigateToNext = new MutableLiveData<>();

    public LiveData<Boolean> getNavigateToNext() {
        return navigateToNext;
    }

    public void startTimer() {
        new Handler().postDelayed(() -> {
            navigateToNext.setValue(true);
        }, 3000);
    }

}
