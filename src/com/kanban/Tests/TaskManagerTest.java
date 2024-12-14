package com.kanban.Tests;

import static org.junit.jupiter.api.Assertions.*;

import com.kanban.TaskTracker.controller.*;
import com.kanban.TaskTracker.model.*;
import com.kanban.TaskTracker.util.*;
import org.junit.jupiter.api.Test;


import java.util.List;

class TaskManagerTest {

    @Test
    public void utilityClassShouldReturnInitializedManagers() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager, "TaskManager должен быть проинициализирован");
        assertNotNull(historyManager, "HistoryManager должен быть проинициализирован");
    }
    @Test
    public void shouldAddTaskToHistoryAfterRetrieval() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager manager = new InMemoryTaskManager(historyManager);

        Task task = new Task(manager.generateId(), "Test Task", "Task Description");
        manager.createTask(task);

        Task retrievedTask = manager.getTaskById(task.getId());
        assertNotNull(retrievedTask, "Задача должна быть найдена");

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после получения");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с полученной");
    }
}