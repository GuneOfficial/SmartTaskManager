package com.example.smarttaskmanager.dto;

import com.example.smarttaskmanager.viewmodel.CreateTaskViewModel;

public class Task {

    private int id;
    private int userId;
    private long createdAt;
    private String title;
    private String description;
    private String category;
    private String priority;
    private CreateTaskViewModel.TaskStatus status;
    private long dueDate;

    public Task(int userId, String description, long createdAt, String title, String category, String priority, CreateTaskViewModel.TaskStatus status, long dueDate) {
        this.userId = userId;
        this.description = description;
        this.createdAt = createdAt;
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public CreateTaskViewModel.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(CreateTaskViewModel.TaskStatus status) {
        this.status = status;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }
}
