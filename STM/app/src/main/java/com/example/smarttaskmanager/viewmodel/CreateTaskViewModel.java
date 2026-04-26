package com.example.smarttaskmanager.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smarttaskmanager.dto.Task;
import com.example.smarttaskmanager.repository.TaskRepository;
import com.example.smarttaskmanager.utils.SessionManager;

public class CreateTaskViewModel extends AndroidViewModel {

    public enum CreateTaskState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    public enum TaskStatus{
        ACTIVE,
        COMPLETED
    }

    private final MutableLiveData<CreateTaskState> createTaskState = new MutableLiveData<>(CreateTaskState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Task> createdTask = new MutableLiveData<>();

    private final TaskRepository taskRepository = new TaskRepository();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final SessionManager sessionManager;

    public CreateTaskViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<CreateTaskState> getCreateTaskState() {
        return createTaskState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Task> getCreatedTask() {
        return createdTask;
    }

    public void createTask(String title, String description, String priority, String category, long dueDate){

        // --- Validation ---
        if (title.trim().isEmpty()) {
            errorMessage.setValue("Task Title is required.");
            createTaskState.setValue(CreateTaskState.ERROR);
            return;
        }
        if (priority.trim().isEmpty()) {
            errorMessage.setValue("Task Priority is required.");
            createTaskState.setValue(CreateTaskState.ERROR);
            return;
        }
        if (category.isEmpty()) {
            errorMessage.setValue("Task Category is required.");
            createTaskState.setValue(CreateTaskState.ERROR);
            return;
        }
        if (!sessionManager.isLoggedIn()) {
            errorMessage.setValue("No user found!");
            createTaskState.setValue(CreateTaskState.ERROR);
            return;
        }

        createTaskState.setValue(CreateTaskState.LOADING);
        long createdAt = System.currentTimeMillis();
        Task task = new Task(sessionManager.getUser().getId(),description.trim(),createdAt,title.trim(),category.trim(),priority.trim(), TaskStatus.ACTIVE, dueDate);

        taskRepository.createTask(task, new TaskRepository.CreateTaskCallback() {
            @Override
            public void onSuccess(Task task) {
                mainHandler.post(() -> {
                    createdTask.setValue(task);
                    createTaskState.setValue(CreateTaskState.SUCCESS);
                });
            }

            @Override
            public void onFailure(String error) {
                mainHandler.post(() -> {
                    errorMessage.setValue(error);
                    createTaskState.setValue(CreateTaskState.ERROR);
                });
            }
        });

    }

}
