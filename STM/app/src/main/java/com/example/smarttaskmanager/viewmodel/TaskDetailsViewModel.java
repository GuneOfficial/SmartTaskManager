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

import java.util.List;

public class TaskDetailsViewModel extends AndroidViewModel {

    public enum TaskDetailsState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR,
        DELETED
    }

    private final MutableLiveData<TaskDetailsState> state = new MutableLiveData<>(TaskDetailsState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Task> task = new MutableLiveData<>();

    private final TaskRepository taskRepository = new TaskRepository();
    private final SessionManager sessionManager;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public TaskDetailsViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<TaskDetailsState> getState() { return state; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Task> getTask() { return task; }

    public void loadTask(String taskId) {
        if (!sessionManager.isLoggedIn()) {
            errorMessage.setValue("Not logged in.");
            return;
        }

        state.setValue(TaskDetailsState.LOADING);
        int userId = sessionManager.getUser().getId();

        taskRepository.fetchTasksByUser(userId, new TaskRepository.FetchTasksCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                Task found = null;
                for (Task t : tasks) {
                    if (taskId.equals(t.getId())) {
                        found = t;
                        break;
                    }
                }

                Task finalFound = found;
                mainHandler.post(() -> {
                    if (finalFound != null) {
                        task.setValue(finalFound);
                        state.setValue(TaskDetailsState.SUCCESS);
                    } else {
                        errorMessage.setValue("Task not found.");
                        state.setValue(TaskDetailsState.ERROR);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                mainHandler.post(() -> {
                    errorMessage.setValue(error);
                    state.setValue(TaskDetailsState.ERROR);
                });
            }
        });
    }

    public void markAsCompleted() {
        Task current = task.getValue();
        if (current == null) return;

        current.setStatus(CreateTaskViewModel.TaskStatus.COMPLETED);
        state.setValue(TaskDetailsState.LOADING);

        taskRepository.updateTask(current, new TaskRepository.UpdateTaskCallback() {
            @Override
            public void onSuccess(Task updatedTask) {
                mainHandler.post(() -> {
                    task.setValue(updatedTask);
                    state.setValue(TaskDetailsState.SUCCESS);
                });
            }

            @Override
            public void onFailure(String error) {
                mainHandler.post(() -> {
                    current.setStatus(CreateTaskViewModel.TaskStatus.ACTIVE);
                    errorMessage.setValue(error);
                    state.setValue(TaskDetailsState.ERROR);
                });
            }
        });
    }

    public void deleteTask() {
        Task current = task.getValue();
        if (current == null) return;

        state.setValue(TaskDetailsState.LOADING);

        taskRepository.deleteTask(current.getId(), new TaskRepository.DeleteTaskCallback() {
            @Override
            public void onSuccess() {
                mainHandler.post(() -> state.setValue(TaskDetailsState.DELETED));
            }

            @Override
            public void onFailure(String error) {
                mainHandler.post(() -> {
                    errorMessage.setValue(error);
                    state.setValue(TaskDetailsState.ERROR);
                });
            }
        });
    }
}