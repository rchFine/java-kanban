package com.kanban.TaskTracker.model;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(int id, String taskName, String description, int epicId) {
        super(id, taskName, description);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "com.kanban.TaskTracker.model.SubTask{" +
                "epicId=" + epicId +
                '}';
    }

    public int getEpicId() {
        return epicId;
    }
}
