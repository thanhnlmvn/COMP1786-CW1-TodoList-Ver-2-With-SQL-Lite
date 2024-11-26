package com.example.todolist;

public class Task {
    private int id;
    private String name;
    private boolean isCompleted;
    private String startDate;
    private String endDate;
    private int progress;

    public Task(int id, String name, boolean isCompleted, String startDate, String endDate, int progress) {
        this.id = id;
        this.name = name;
        this.isCompleted = isCompleted;
        this.startDate = startDate;
        this.endDate = endDate;
        this.progress = progress;
    }

    public Task(String name, String startDate, String endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = false;
        this.progress = 0;
    }

    public Task(String name) {
        this.name = name;
        this.isCompleted = false;
        this.progress = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}
