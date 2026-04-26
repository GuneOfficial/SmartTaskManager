package com.example.smarttaskmanager.repository;

import com.example.smarttaskmanager.dto.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class UserRepository {

    private static final String BASE_URL = "https://69edac54af4ff533142bd4d7.mockapi.io/api/v1/users";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface SignUpCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public void registerUser(User user, SignUpCallback callback) {
        String json = gson.toJson(user);

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
                    User createdUser = gson.fromJson(responseBody, User.class);
                    callback.onSuccess(createdUser);
                } else {
                    callback.onFailure("Server error: " + response.code());
                }
            }
        });
    }

    public interface SignInCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public void loginUser(String email, String password, SignInCallback callback) {

        String url = BASE_URL + "?email=" + email;

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

                Type listType = new TypeToken<List<User>>() {}.getType();
                List<User> users = gson.fromJson(responseBody, listType);

                if (users == null || users.isEmpty()) {
                    callback.onFailure("No account found with that email.");
                    return;
                }

                User matched = null;
                for (User u : users) {
                    if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)
                            && u.getPassword() != null && u.getPassword().equals(password)) {
                        matched = u;
                        break;
                    }
                }

                if (matched != null) {
                    callback.onSuccess(matched);
                } else {
                    callback.onFailure("Incorrect password. Please try again.");
                }
            }
        });
    }

}