package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Task> tasks;
    private final OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskDelete(Task task);
        void onTaskEdit(Task task, int position);
        void onTaskPickDate(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, startEndDateTextView;
        Spinner statusSpinner;
        ProgressBar progressBar;
        ImageView btnEdit, btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            startEndDateTextView = itemView.findViewById(R.id.startEndDateTextView);
            statusSpinner = itemView.findViewById(R.id.statusSpinner);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Task task, OnTaskClickListener listener) {
            taskName.setText(task.getName());
            startEndDateTextView.setText((task.getStartDate() != null ? task.getStartDate() : "-") +
                    " - " + (task.getEndDate() != null ? task.getEndDate() : "-"));

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_item, new String[]{"Not started", "In progress", "Completed"});
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusSpinner.setAdapter(spinnerAdapter);

            // Set initial spinner and progress
            int progressValue = task.getProgress();
            if (progressValue == 0) {
                statusSpinner.setSelection(0);
                progressBar.setProgress(0);
            } else if (progressValue == 100) {
                statusSpinner.setSelection(2);
                progressBar.setProgress(100);
            } else {
                statusSpinner.setSelection(1);
                progressBar.setProgress(progressValue);
            }

            // Spinner listener
            statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    task.setProgress(position == 0 ? 0 : (position == 1 ? 50 : 100));
                    progressBar.setProgress(task.getProgress());
                    new DatabaseHelper(itemView.getContext()).updateTask(task);
                }
                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });

            btnEdit.setOnClickListener(v -> listener.onTaskEdit(task, getAdapterPosition()));
            btnDelete.setOnClickListener(v -> listener.onTaskDelete(task));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TaskViewHolder) {
            ((TaskViewHolder) holder).bind(tasks.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
