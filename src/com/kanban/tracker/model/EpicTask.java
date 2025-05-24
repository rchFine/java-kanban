package com.kanban.tracker.model;

import com.kanban.tracker.util.TaskType;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

public class EpicTask extends Task {

    private final List<SubTask> subTasks;

    public EpicTask(int id, String taskName, String description) {
        super(id, taskName, description, null, Duration.ZERO);
        subTasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasks=" + subTasks +
                '}';
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask sub) {
        subTasks.add(sub);
    }

    public void removeSubTask(SubTask sub) {
        subTasks.remove(sub);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toCSVString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s", getId(), getType(), getTaskName(), getStatus(), getDescription(),
                startTime != null ? startTime.toString() : "",
                duration != null ? String.valueOf(duration.toMinutes()) : "");
    }

    public void updateTimeAndDuration() {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime start = null;
        LocalDateTime end = null;

        for (SubTask subTask : getSubTasks()) {
            if (subTask.getStartTime() != null) {
                if (start == null || subTask.getStartTime().isBefore(start)) {
                    start = subTask.getStartTime();
                }
                LocalDateTime subEnd = subTask.getEndTime();
                if (subEnd != null && (end == null || subEnd.isAfter(end))) {
                    end = subEnd;
                }
            }
            if (subTask.getDuration() != null) {
                totalDuration = totalDuration.plus(subTask.getDuration());
            }
        }

        this.startTime = start;
        this.duration = totalDuration;
    }
}
