# Smart Task Manager

A task management Android application built for the Software Engineer Intern assignment.

## Overview

Smart Task Manager is an Android app that lets users register, log in, and manage their daily tasks. Users can create, view, edit, and delete tasks, and can search or filter them by priority and category.

## Technology

- **Platform:** Android
- **Language:** Java
- **UI:** XML layouts (ConstraintLayout, RecyclerView, CardView, Material Chips)
- **Architecture:** MVVM (ViewModel + LiveData + Repository)
- **Networking:** OkHttp + Gson
- **Session Management:** SharedPreferences

## Features

- User registration and login
- Session persistence (stays logged in after closing the app)
- Logout with session clear
- Create, view, edit, and delete tasks
- Search tasks by title (real-time)
- Filter tasks by priority (High, Medium, Low) and category (Work, Personal, Shopping, Health)
- Switch between Active and Completed task views
- Task details screen with mark-as-complete option
- Logged-in user info displayed on Settings screen
- Input validation and error messages throughout the app
- Loading indicators and empty state messages

## API Endpoints

Base URL: `https://69edac54af4ff533142bd4d7.mockapi.io/api/v1`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users?email={email}` | Find user by email (login) |
| POST | `/users` | Register a new user |
| GET | `/tasks?userId={id}` | Get all tasks for a user |
| POST | `/tasks` | Create a new task |
| PUT | `/tasks/{id}` | Update an existing task |
| DELETE | `/users/{id}/task/{id}` | Delete a task |

## Setup and Run Instructions

1. Clone the repository
   ```
   git clone GuneOfficial/SmartTaskManager
   ```
2. Open the project in Android Studio
3. Wait for Gradle to sync
4. Run on an emulator or physical device (minimum SDK 24 / Android 7.0)

No API keys or extra configuration needed — the MockAPI base URL is already in the code.

## Screens

1. Splash Screen
2. Sign In Screen
3. Sign Up Screen
4. Dashboard (task list with search and filter)
5. Add / Edit Task Screen
6. Task Details Screen
7. Settings Screen (user info + logout)
