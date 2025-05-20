package com.kanban.tracker.model;

import com.kanban.tracker.util.TaskStatus;
import java.util.Objects;

public class Task {

    private final String taskName;
    private final String description;
    private int id;
    private TaskStatus status;

    public Task(int id, String name, String description) {
        this.id = id;
        this.taskName = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        Task otherTask = (Task) obj;

        return otherTask.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "taskName='" + taskName + '\'';

        if (description != null) {
            result = result + ", description.length='" + description.length();
        } else {
            result = result + ", description=null";
        }
        result = result + ", id=" + id + '\'' +
                ", status= " + status.toString() + '\'';

        return result + "}";
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
