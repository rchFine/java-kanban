package com.kanban.tracker.controllers;

import com.kanban.tracker.model.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
