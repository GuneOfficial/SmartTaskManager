package com.example.smarttaskmanager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttaskmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

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

        LinearLayout container = findViewById(R.id.dashboardTaskLinerLayout);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View nview = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        Chip moreChip = findViewById(R.id.dashMoreChip);
        moreChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(nview);
                dialog.show();
            }
        });

        int x = 0;

        while (x < 10){
            View inflated = getLayoutInflater().inflate(R.layout.task_component, container, false);
            inflated.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(DashboardActivity.this, TaskDetailsActivity.class));

                }
            });
            container.addView(inflated);
            x++;
        }

        FloatingActionButton addTaskButton = findViewById(R.id.fabAddTask);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(DashboardActivity.this, TaskAddEditActivity.class));

            }
        });

    }
}