package com.example.smarttaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.adapters.TaskAdapter;
import com.example.smarttaskmanager.dto.Task;
import com.example.smarttaskmanager.utils.SessionManager;
import com.example.smarttaskmanager.viewmodel.CreateTaskViewModel;
import com.example.smarttaskmanager.viewmodel.DashboardViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private DashboardViewModel viewModel;
    private TaskAdapter taskAdapter;
    private SessionManager sessionManager;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView greetingTextView;
    private TextView emptyTextView;
    private TextView activeCountTextView;
    private TextView activeSubTextView;

    private ImageView dashSettingButton;

    private Chip activeChip;
    private Chip completedChip;

    private ActivityResultLauncher<Intent> taskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> viewModel.loadTasks()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        recyclerView = findViewById(R.id.dashboardRecyclerView);
        progressBar = findViewById(R.id.dashboardProgressBar);
        greetingTextView = findViewById(R.id.textView5);
        emptyTextView = findViewById(R.id.dashboardEmptyText);
        activeCountTextView = findViewById(R.id.textView3);
        activeSubTextView = findViewById(R.id.textView4);
        activeChip = findViewById(R.id.dashActiveChip);
        completedChip = findViewById(R.id.dashCompletedChip);
        dashSettingButton = findViewById(R.id.dashSettingButton);

        if (sessionManager.isLoggedIn() && sessionManager.getUser() != null) {
            String name = sessionManager.getUser().getFullName();
            greetingTextView.setText("Hello, " + name);
        }

        dashSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));

            }
        });

        taskAdapter = new TaskAdapter(task -> {
            Intent intent = new Intent(DashboardActivity.this, TaskDetailsActivity.class);
            intent.putExtra("taskId", task.getId()); // String now
            taskLauncher.launch(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        viewModel.getLoadState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        });

        viewModel.getFilteredTasks().observe(this, tasks -> {
            taskAdapter.setTasks(tasks);

            if (tasks == null || tasks.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            }

            updateHeaderCounts();
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        activeChip.setOnClickListener(v -> {
            activeChip.setChecked(true);
            completedChip.setChecked(false);
            viewModel.setStatusFilter("active");
        });

        completedChip.setOnClickListener(v -> {
            completedChip.setChecked(true);
            activeChip.setChecked(false);
            viewModel.setStatusFilter("completed");
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchQuery(newText);
                return true;
            }
        });

        Chip moreChip = findViewById(R.id.dashMoreChip);
        moreChip.setOnClickListener(v -> showFilterBottomSheet());

        FloatingActionButton addTaskButton = findViewById(R.id.fabAddTask);
        addTaskButton.setOnClickListener(v ->
                taskLauncher.launch(new Intent(DashboardActivity.this, TaskAddEditActivity.class))
        );

        viewModel.loadTasks();
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        dialog.setContentView(sheetView);

        CheckBox highCb = sheetView.findViewById(R.id.checkBox);
        CheckBox mediumCb = sheetView.findViewById(R.id.checkBox3);
        CheckBox lowCb = sheetView.findViewById(R.id.checkBox4);
        CheckBox workCb = sheetView.findViewById(R.id.checkBox5);
        CheckBox personalCb = sheetView.findViewById(R.id.checkBox6);
        CheckBox shoppingCb = sheetView.findViewById(R.id.checkBox7);
        CheckBox healthCb = sheetView.findViewById(R.id.checkBox8);

        dialog.setOnDismissListener(d -> {
            List<String> priorities = new ArrayList<>();
            if (highCb.isChecked()) priorities.add("High");
            if (mediumCb.isChecked()) priorities.add("Medium");
            if (lowCb.isChecked()) priorities.add("Low");

            List<String> categories = new ArrayList<>();
            if (workCb.isChecked()) categories.add("Work");
            if (personalCb.isChecked()) categories.add("Personal");
            if (shoppingCb.isChecked()) categories.add("Shopping");
            if (healthCb.isChecked()) categories.add("Health");

            viewModel.setPriorityFilters(priorities);
            viewModel.setCategoryFilters(categories);
        });

        dialog.show();
    }

    private void updateHeaderCounts() {
        List<Task> all = viewModel.getAllTasks();
        if (all == null) return;

        int active = 0, completed = 0, urgent = 0;
        for (Task t : all) {
            if (t.getStatus() == CreateTaskViewModel.TaskStatus.COMPLETED) {
                completed++;
            } else {
                active++;
            }
            if ("High".equals(t.getPriority())) urgent++;
        }

        activeCountTextView.setText(active + " Active");
        activeSubTextView.setText(completed + " completed, " + urgent + " urgent");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}