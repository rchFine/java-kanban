package com.kanban.tracker.model;

import com.kanban.tracker.util.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(int id, String taskName, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(id, taskName, description, startTime, duration);
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
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s", getId(), getType(), getTaskName(), getStatus(), getDescription(), getEpicId(),
                startTime != null ? startTime.toString() : "",
                duration != null ? String.valueOf(duration.toMinutes()) : "");
    }
}
