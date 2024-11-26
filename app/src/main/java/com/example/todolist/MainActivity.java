package com.example.todolist;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView listTask;
    private TaskAdapter taskAdapter;
    private List<Task> tasks = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        taskAdapter = new TaskAdapter(tasks, this);
        listTask = findViewById(R.id.listTask);
        listTask.setLayoutManager(new LinearLayoutManager(this));
        listTask.setAdapter(taskAdapter);

        tasks.addAll(dbHelper.getAllTasks());
        taskAdapter.notifyDataSetChanged();

        Button buttonAddTask = findViewById(R.id.btnAddNewTask);
        buttonAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("New Task");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);

        EditText inputTaskName = dialogView.findViewById(R.id.inputTaskName);
        ImageView calendarIcon = dialogView.findViewById(R.id.dialogCalendarIcon);

        Task newTask = new Task("", "", "");

        dialogBuilder.setView(dialogView);

        dialogBuilder.setPositiveButton("Add New Task", (dialog, which) -> {
            String taskName = inputTaskName.getText().toString();

            if (!taskName.isEmpty()) {
                newTask.setName(taskName);
                long taskId = dbHelper.addTask(newTask);
                newTask.setId((int) taskId);
                tasks.add(newTask);
                taskAdapter.notifyDataSetChanged();
            }
            dialog.dismiss();
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        calendarIcon.setOnClickListener(v -> onTaskPickDate(newTask));

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onTaskClick(Task task) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Task Details");

        String detailsMessage = "Task Name: " + task.getName() +
                "\nStart Date: " + task.getStartDate() +
                "\nEnd Date: " + task.getEndDate() +
                "\nProgress: " + task.isCompleted();

        dialogBuilder.setMessage(detailsMessage);
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onTaskEdit(Task task, int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Edit Task");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_task, null);
        dialogBuilder.setView(dialogView);

        EditText inputTaskName = dialogView.findViewById(R.id.inputTaskName);
        ImageView dialogCalendarIcon = dialogView.findViewById(R.id.dialogCalendarIcon);
        TextView setTimeTextView = dialogView.findViewById(R.id.setTimeTextView);

        inputTaskName.setText(task.getName());
        setTimeTextView.setText(task.getStartDate() + " - " + task.getEndDate());

        View.OnClickListener onClickListener = v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog startDatePickerDialog = new DatePickerDialog(
                    this, (view, year, month, dayOfMonth) -> {
                String selectedStartDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                task.setStartDate(selectedStartDate);

                DatePickerDialog endDatePickerDialog = new DatePickerDialog(
                        this, (endView, endYear, endMonth, endDayOfMonth) -> {
                    String selectedEndDate = endDayOfMonth + "/" + (endMonth + 1) + "/" + endYear;
                    task.setEndDate(selectedEndDate);
                    setTimeTextView.setText(selectedStartDate + " - " + selectedEndDate);
                }, year, month, dayOfMonth);

                endDatePickerDialog.show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            startDatePickerDialog.show();
        };

        dialogCalendarIcon.setOnClickListener(onClickListener);
        setTimeTextView.setOnClickListener(onClickListener);

        dialogBuilder.setPositiveButton("Update", (dialog, which) -> {
            String updatedName = inputTaskName.getText().toString();
            if (!updatedName.isEmpty()) {
                task.setName(updatedName);
                dbHelper.updateTask(task);
                tasks.set(position, task);
                taskAdapter.notifyItemChanged(position);
            }
            dialog.dismiss();
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onTaskDelete(Task task) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Do you want delete Task?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteTask(task);
                    tasks.remove(task);
                    taskAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onTaskPickDate(Task task) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog startDatePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedStartDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            task.setStartDate(selectedStartDate);

            DatePickerDialog endDatePickerDialog = new DatePickerDialog(this, (endView, endYear, endMonth, endDayOfMonth) -> {
                String selectedEndDate = endDayOfMonth + "/" + (endMonth + 1) + "/" + endYear;
                task.setEndDate(selectedEndDate);
                taskAdapter.notifyDataSetChanged();
            }, year, month, dayOfMonth);

            endDatePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        startDatePickerDialog.show();
    }
}
