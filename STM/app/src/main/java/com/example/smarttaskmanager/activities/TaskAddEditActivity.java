package com.example.smarttaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.viewmodel.CreateTaskViewModel;
import com.google.android.material.chip.Chip;

public class TaskAddEditActivity extends AppCompatActivity {

    private CreateTaskViewModel viewModel;

    private TextView headerTextView;
    private EditText taskTitleInput;
    private EditText taskDescInput;
    private Chip taskPriorityChip1;
    private Chip taskPriorityChip2;
    private Chip taskPriorityChip3;
    private Chip taskCategoryChip1;
    private Chip taskCategoryChip2;
    private Chip taskCategoryChip3;
    private Chip taskCategoryChip4;
    private DatePicker taskDatePicker;
    private Button taskCreateButton;
    private ProgressBar progressBar;

    private String selectedPriorityChip;
    private String selectedCategoryChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_add_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ---- Bind views ----
        headerTextView = findViewById(R.id.headerTextView);
        taskTitleInput = findViewById(R.id.aeTaskTitleTextView);
        taskDescInput = findViewById(R.id.aeTaskDescTextView);
        taskPriorityChip1 = findViewById(R.id.aeTaskPrioChip1);
        taskPriorityChip2 = findViewById(R.id.aeTaskPrioChip2);
        taskPriorityChip3 = findViewById(R.id.aeTaskPrioChip3);
        taskCategoryChip1 = findViewById(R.id.aeTaskCatChip1);
        taskCategoryChip2 = findViewById(R.id.aeTaskCatChip2);
        taskCategoryChip3 = findViewById(R.id.aeTaskCatChip3);
        taskCategoryChip4 = findViewById(R.id.aeTaskCatChip4);
        taskDatePicker = findViewById(R.id.aeTaskDatePicker);
        taskCreateButton = findViewById(R.id.aeTaskCreateTaskButton);
        progressBar = findViewById(R.id.aeTaskProgressBar);

        viewModel = new ViewModelProvider(this).get(CreateTaskViewModel.class);

        updatePriorityChips("Medium");
        updateCategoryChips("Personal");

        Intent intent = getIntent();

        if (intent.hasExtra("taskData")) {
            headerTextView.setText("Edit Task");


        } else {
            headerTextView.setText("New Task");

            viewModel.getCreateTaskState().observe(this, state -> {
                switch (state) {
                    case LOADING:
                        taskCreateButton.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        break;

                    case SUCCESS:
                        progressBar.setVisibility(View.GONE);
                        taskCreateButton.setEnabled(true);
                        Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                        break;

                    case ERROR:
                        progressBar.setVisibility(View.GONE);
                        taskCreateButton.setEnabled(true);
                        break;

                    case IDLE:
                    default:
                        progressBar.setVisibility(View.GONE);
                        taskCreateButton.setEnabled(true);
                        break;
                }
            });

            viewModel.getErrorMessage().observe(this, message -> {
                if (message != null && !message.isEmpty()) {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            });

        }

        taskPriorityChip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePriorityChips(taskPriorityChip1.getText().toString());
            }
        });

        taskPriorityChip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePriorityChips(taskPriorityChip2.getText().toString());
            }
        });

        taskPriorityChip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePriorityChips(taskPriorityChip3.getText().toString());
            }
        });


//        -----------------

        taskCategoryChip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCategoryChips(taskCategoryChip1.getText().toString());
            }
        });

        taskCategoryChip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCategoryChips(taskCategoryChip2.getText().toString());
            }
        });

        taskCategoryChip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCategoryChips(taskCategoryChip3.getText().toString());
            }
        });

        taskCategoryChip4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCategoryChips(taskCategoryChip4.getText().toString());
            }
        });

        taskCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = taskTitleInput.getText() != null ? taskTitleInput.getText().toString() : "";
                String description = taskDescInput.getText() != null ? taskDescInput.getText().toString() : "";
                String priority = selectedPriorityChip != null ? selectedPriorityChip : "Medium";
                String category = selectedCategoryChip != null ? selectedCategoryChip : "Personal";

                Log.d("STM: ","Title: "+title+", Description: "+description+", Priority: "+priority+", category: "+category);

//                String dueDate = taskDatePicker != null ? taskTitleInput.getText().toString() : "";

                viewModel.createTask(title,description,priority,category,0);

            }
        });

        ImageView closeButton = findViewById(R.id.addEditCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button cancelButton = findViewById(R.id.addEditCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void updatePriorityChips(String priorityChip) {

        switch (priorityChip) {
            case "Low":
                taskPriorityChip1.setChecked(true);
                taskPriorityChip2.setChecked(false);
                taskPriorityChip3.setChecked(false);
                break;

            case "Medium":
                taskPriorityChip1.setChecked(false);
                taskPriorityChip2.setChecked(true);
                taskPriorityChip3.setChecked(false);
                break;

            case "High":
                taskPriorityChip1.setChecked(false);
                taskPriorityChip2.setChecked(false);
                taskPriorityChip3.setChecked(true);
                break;

        }

        selectedPriorityChip = priorityChip;

    }

    private void updateCategoryChips(String categoryChip) {

        switch (categoryChip) {
            case "Work":
                taskCategoryChip1.setChecked(true);
                taskCategoryChip2.setChecked(false);
                taskCategoryChip3.setChecked(false);
                taskCategoryChip4.setChecked(false);
                break;

            case "Personal":
                taskCategoryChip1.setChecked(false);
                taskCategoryChip2.setChecked(true);
                taskCategoryChip3.setChecked(false);
                taskCategoryChip4.setChecked(false);
                break;

            case "Shopping":
                taskCategoryChip1.setChecked(false);
                taskCategoryChip2.setChecked(false);
                taskCategoryChip3.setChecked(true);
                taskCategoryChip4.setChecked(false);
                break;

            case "Health":
                taskCategoryChip1.setChecked(false);
                taskCategoryChip2.setChecked(false);
                taskCategoryChip3.setChecked(false);
                taskCategoryChip4.setChecked(true);
                break;


        }

        selectedCategoryChip = categoryChip;

    }

}