package com.kanban.test.controllers;

import static org.junit.jupiter.api.Assertions.*;

import com.kanban.tracker.controllers.*;
import com.kanban.tracker.model.*;
import com.kanban.tracker.util.*;
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
    public void shouldAddTaskToHistoryWithoutDuplicates() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task(manager.generateId(), "Test Task", "Task Description");
        manager.createTask(task);

        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну задачу без дубликатов");
        assertEquals(task, history.getFirst(), "Задача в истории должна быть та, которая была просмотрена");
    }

    @Test
    public void shouldNotLoseTaskWhenIdChangedBySetter() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task(manager.generateId(), "Task", "Task Description");

        manager.createTask(task);
        task.setId(32);

        assertNull(manager.getTaskById(task.getId()), "Менеджер не должен находить задачу с новым id");
        assertNull(manager.getTaskById(32), "Менеджер не должен находить с новым id");
    }
}