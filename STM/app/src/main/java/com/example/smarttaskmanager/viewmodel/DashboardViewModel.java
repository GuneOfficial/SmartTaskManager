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

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    public enum LoadState {
        IDLE, LOADING, SUCCESS, ERROR
    }

    private final MutableLiveData<LoadState> loadState = new MutableLiveData<>(LoadState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Task>> filteredTasks = new MutableLiveData<>();

    private List<Task> allTasks = new ArrayList<>();

    private String searchQuery = "";
    private String statusFilter = "active"; // "active" or "completed"
    private List<String> priorityFilters = new ArrayList<>();
    private List<String> categoryFilters = new ArrayList<>();

    private final TaskRepository taskRepository;
    private final SessionManager sessionManager;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository();
        sessionManager = new SessionManager(application);
    }

    public LiveData<LoadState> getLoadState() { return loadState; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<Task>> getFilteredTasks() { return filteredTasks; }

    public void loadTasks() {
        if (!sessionManager.isLoggedIn()) {
            errorMessage.setValue("Not logged in.");
            return;
        }

        int userId = sessionManager.getUser().getId();
        loadState.setValue(LoadState.LOADING);

        taskRepository.fetchTasksByUser(userId, new TaskRepository.FetchTasksCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                mainHandler.post(() -> {
                    allTasks = tasks;
                    loadState.setValue(LoadState.SUCCESS);
                    applyFilters();
                });
            }

            @Override
            public void onFailure(String error) {
                mainHandler.post(() -> {
                    errorMessage.setValue(error);
                    loadState.setValue(LoadState.ERROR);
                });
            }
        });
    }

    public void setSearchQuery(String query) {
        searchQuery = query == null ? "" : query.toLowerCase().trim();
        applyFilters();
    }

    public void setStatusFilter(String status) {
        statusFilter = status;
        applyFilters();
    }

    public void setPriorityFilters(List<String> priorities) {
        priorityFilters = priorities;
        applyFilters();
    }

    public void setCategoryFilters(List<String> categories) {
        categoryFilters = categories;
        applyFilters();
    }

    private void applyFilters() {
        List<Task> result = new ArrayList<>();

        for (Task task : allTasks) {

            // status filter
            if (statusFilter.equals("active")) {
                if (task.getStatus() == CreateTaskViewModel.TaskStatus.COMPLETED) continue;
            } else {
                if (task.getStatus() != CreateTaskViewModel.TaskStatus.COMPLETED) continue;
            }

            // search filter
            if (!searchQuery.isEmpty()) {
                String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
                if (!title.contains(searchQuery)) continue;
            }

            // priority filter
            if (!priorityFilters.isEmpty()) {
                if (!priorityFilters.contains(task.getPriority())) continue;
            }

            // category filter
            if (!categoryFilters.isEmpty()) {
                if (!categoryFilters.contains(task.getCategory())) continue;
            }

            result.add(task);
        }

        filteredTasks.setValue(result);
    }

    public List<Task> getAllTasks() {
        return allTasks;
    }

    public void refreshTaskInList(Task updatedTask) {
        for (int i = 0; i < allTasks.size(); i++) {
            if (allTasks.get(i).getId() == updatedTask.getId()) {
                allTasks.set(i, updatedTask);
                break;
            }
        }
        applyFilters();
    }

    public void removeTaskFromList(int taskId) {
        for (int i = 0; i < allTasks.size(); i++) {
            if (allTasks.get(i).getId() == taskId) {
                allTasks.remove(i);
                break;
            }
        }
        applyFilters();
    }
}