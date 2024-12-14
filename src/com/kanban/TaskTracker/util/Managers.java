package com.kanban.TaskTracker.util;

import com.kanban.TaskTracker.controller.*;
public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
