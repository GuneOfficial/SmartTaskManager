package com.example.smarttaskmanager.repository;

import com.example.smarttaskmanager.dto.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TaskRepository {

    private static final String BASE_URL = "https://69edac54af4ff533142bd4d7.mockapi.io/api/v1/task";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    // ---- Create Task ----

    public interface CreateTaskCallback {
        void onSuccess(Task task);
        void onFailure(String error);
    }

    public void createTask(Task task, CreateTaskCallback callback) {
        String json = gson.toJson(task);
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Task createdTask = gson.fromJson(responseBody, Task.class);
                    callback.onSuccess(createdTask);
                } else {
                    callback.onFailure("Server error: " + response.code());
                }
            }
        });
    }

    // ---- Fetch Tasks by User ----

    public interface FetchTasksCallback {
        void onSuccess(List<Task> tasks);
        void onFailure(String error);
    }

    public void fetchTasksByUser(int userId, FetchTasksCallback callback) {
        String url = BASE_URL + "?userId=" + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onFailure("Server error: " + response.code());
                    return;
                }

                String responseBody = response.body().string();
                Type listType = new TypeToken<List<Task>>() {}.getType();
                List<Task> tasks = gson.fromJson(responseBody, listType);
                callback.onSuccess(tasks);
            }
        });
    }

    // ---- Update Task ----

    public interface UpdateTaskCallback {
        void onSuccess(Task task);
        void onFailure(String error);
    }

    public void updateTask(Task task, UpdateTaskCallback callback) {
        String json = gson.toJson(task);
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "/" + task.getId())
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Task updatedTask = gson.fromJson(responseBody, Task.class);
                    callback.onSuccess(updatedTask);
                } else {
                    callback.onFailure("Server error: " + response.code());
                }
            }
        });
    }

    // ---- Delete Task ----

    public interface DeleteTaskCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void deleteTask(int taskId, DeleteTaskCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/" + taskId)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Server error: " + response.code());
                }
            }
        });
    }
}