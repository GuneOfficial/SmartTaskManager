package com.example.smarttaskmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.dto.Task;
import com.example.smarttaskmanager.viewmodel.CreateTaskViewModel;
import com.example.smarttaskmanager.viewmodel.TaskDetailsViewModel;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskDetailsActivity extends AppCompatActivity {

    private TaskDetailsViewModel viewModel;

    private TextView taskTitleTextView;
    private TextView taskDescTextView;
    private TextView taskDueDateTextView;
    private TextView taskCreatedOnTextView;
    private Chip priorityChip;
    private Chip categoryChip;
    private CheckBox markCompleteCheckBox;
    private Button editTaskButton;
    private Button deleteTaskButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // bind views
        taskTitleTextView   = findViewById(R.id.textView14);
        taskDescTextView    = findViewById(R.id.textView16);
        taskDueDateTextView = findViewById(R.id.taskDetailsDueDateValue);
        taskCreatedOnTextView = findViewById(R.id.taskDetailsCreatedOnValue);
        priorityChip        = findViewById(R.id.chip8);
        categoryChip        = findViewById(R.id.chip7);
        markCompleteCheckBox = findViewById(R.id.checkBox2);
        editTaskButton      = findViewById(R.id.aeTaskCreateTaskButton);
        deleteTaskButton    = findViewById(R.id.taskDetailsCancelButton);
        progressBar         = findViewById(R.id.taskDetailsProgressBar);

        viewModel = new ViewModelProvider(this).get(TaskDetailsViewModel.class);

        ImageView closeButton = findViewById(R.id.taskDetailsCloseButton);
        closeButton.setOnClickListener(v -> finish());

        // observe state
        viewModel.getState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    editTaskButton.setEnabled(false);
                    deleteTaskButton.setEnabled(false);
                    break;

                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    editTaskButton.setEnabled(true);
                    deleteTaskButton.setEnabled(true);
                    break;

                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    editTaskButton.setEnabled(true);
                    deleteTaskButton.setEnabled(true);
                    break;

                case DELETED:
                    Toast.makeText(this, "Task deleted.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                    break;

                default:
                    break;
            }
        });

        viewModel.getTask().observe(this, task -> {
            if (task == null) return;
            populateTask(task);
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });

        // mark as complete checkbox
        markCompleteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Task current = viewModel.getTask().getValue();
            if (current == null) return;

            boolean alreadyCompleted = current.getStatus() == CreateTaskViewModel.TaskStatus.COMPLETED;

            if (isChecked && !alreadyCompleted) {
                viewModel.markAsCompleted();
            } else if (!isChecked && alreadyCompleted) {
                // keep it checked if already completed (don't allow unchecking)
                markCompleteCheckBox.setChecked(true);
            }
        });

        // edit task button
        editTaskButton.setOnClickListener(v -> {
            Task current = viewModel.getTask().getValue();
            if (current == null) return;
            Intent intent = new Intent(TaskDetailsActivity.this, TaskAddEditActivity.class);
            intent.putExtra("taskId", current.getId());
            startActivity(intent);
        });

        // delete task button
        deleteTaskButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteTask())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // load task

        String taskId = getIntent().getStringExtra("taskId");
        if (taskId == null) {
            Toast.makeText(this, "Task not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        viewModel.loadTask(taskId);
    }

    private void populateTask(Task task) {
        taskTitleTextView.setText(task.getTitle());

        // description
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            taskDescTextView.setText(task.getDescription());
        } else {
            taskDescTextView.setText("No description.");
        }

        // priority chip
        priorityChip.setText(task.getPriority());
        switch (task.getPriority()) {
            case "High":
                priorityChip.setChipBackgroundColorResource(android.R.color.holo_red_light);
                priorityChip.setTextColor(Color.WHITE);
                break;
            case "Low":
                priorityChip.setChipBackgroundColorResource(android.R.color.holo_green_light);
                priorityChip.setTextColor(Color.WHITE);
                break;
            default:
                priorityChip.setChipBackgroundColorResource(android.R.color.holo_orange_light);
                priorityChip.setTextColor(Color.WHITE);
                break;
        }

        // category chip
        categoryChip.setText(task.getCategory());

        // due date
        if (task.getDueDate() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            taskDueDateTextView.setText(sdf.format(new Date(task.getDueDate())));
        } else {
            taskDueDateTextView.setText("No due date");
        }

        // created on
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        taskCreatedOnTextView.setText(sdf.format(new Date(task.getCreatedAt())));

        // mark complete checkbox
        boolean completed = task.getStatus() == CreateTaskViewModel.TaskStatus.COMPLETED;
        markCompleteCheckBox.setOnCheckedChangeListener(null);
        markCompleteCheckBox.setChecked(completed);
        markCompleteCheckBox.setEnabled(!completed);
        markCompleteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.markAsCompleted();
            }
        });
    }
}