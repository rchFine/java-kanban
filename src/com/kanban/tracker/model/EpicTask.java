package com.kanban.tracker.model;

import com.kanban.tracker.util.TaskType;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private final List<SubTask> subTasks;

    public EpicTask(int id, String taskName, String description) {
        super(id, taskName, description);
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
}
