package com.kanban.tracker.controllers;

import com.kanban.tracker.model.Task;

import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();
    private final static int HISTORY_LIMIT = 10;

    @Override
    public void add(Task task) {
        if (!history.contains(task)) {
            if (history.size() == HISTORY_LIMIT) {
                history.removeFirst();
            }
            history.add(task);
        }

    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
