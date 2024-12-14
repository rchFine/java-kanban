package com.kanban.TaskTracker.model;

import java.util.ArrayList;

public class EpicTask extends Task {

    private final ArrayList<SubTask> subTasks;

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

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask sub) {
        subTasks.add(sub);
    }

    public void removeSubTask(SubTask sub) {
        subTasks.remove(sub);
    }
}
