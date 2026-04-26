package com.example.smarttaskmanager.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.dto.Task;
import com.example.smarttaskmanager.viewmodel.CreateTaskViewModel;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;

    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> newTasks) {
        tasks = newTasks != null ? newTasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_component, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
        holder.itemView.setOnClickListener(v -> listener.onTaskClick(task));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView descTextView;
        Chip priorityChip;
        Chip categoryChip;
        TextView dueDateTextView;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitleTextView);
            descTextView = itemView.findViewById(R.id.taskDescTextView);
            priorityChip = itemView.findViewById(R.id.taskPriorityChip);
            categoryChip = itemView.findViewById(R.id.taskCategoryChip);
            dueDateTextView = itemView.findViewById(R.id.taskDueDateTextView);
        }

        void bind(Task task) {
            titleTextView.setText(task.getTitle());

            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                descTextView.setText(task.getDescription());
                descTextView.setVisibility(View.VISIBLE);
            } else {
                descTextView.setVisibility(View.GONE);
            }

            priorityChip.setText(task.getPriority());
            setPriorityColor(task.getPriority());

            categoryChip.setText(task.getCategory());

            if (task.getDueDate() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                dueDateTextView.setText("Due: " + sdf.format(new Date(task.getDueDate())));
                dueDateTextView.setVisibility(View.VISIBLE);
            } else {
                dueDateTextView.setVisibility(View.GONE);
            }

            if (task.getStatus() == CreateTaskViewModel.TaskStatus.COMPLETED) {
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        private void setPriorityColor(String priority) {
            if (priority == null) return;
            switch (priority) {
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
        }
    }
}