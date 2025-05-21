package com.kanban.tracker.model;

import com.kanban.tracker.util.TaskType;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(int id, String taskName, String description, int epicId) {
        super(id, taskName, description);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                '}';
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toCSVString() {
        return String.format("%d,%s,%s,%s,%s,%d", getId(), getType(), getTaskName(), getStatus(), getDescription(), getEpicId());
    }
}
