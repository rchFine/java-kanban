package com.kanban.test.controllers;

import com.kanban.tracker.controllers.HistoryManager;
import com.kanban.tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class HistoryManagerTest {

    protected HistoryManager historyManager;

    protected abstract HistoryManager createHistoryManager();

    @BeforeEach
    public void setUp() {
        historyManager = createHistoryManager();
    }

    @Test
    public void shouldReturnEmptyHistoryInitially() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    public void shouldAddTaskToHistory() {
        Task task = new Task(1, "Task1", "Desc", LocalDateTime.now(), Duration.ofMinutes(15));
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void shouldNotDuplicateTasksInHistory() {
        Task task = new Task(1, "Task1", "Desc", LocalDateTime.now(), Duration.ofMinutes(15));
        historyManager.add(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не должна содержать дубликатов");
    }

    @Test
    public void shouldRemoveTaskFromStartOfHistory() {
        Task task1 = new Task(1, "Task1", "Desc", LocalDateTime.now(), Duration.ofMinutes(15));
        Task task2 = new Task(2, "Task2", "Desc", LocalDateTime.now(), Duration.ofMinutes(20));
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    public void shouldRemoveTaskFromMiddleOfHistory() {
        Task task1 = new Task(1, "Task1", "Desc", LocalDateTime.now(), Duration.ofMinutes(15));
        Task task2 = new Task(2, "Task2", "Desc", LocalDateTime.now(), Duration.ofMinutes(20));
        Task task3 = new Task(3, "Task3", "Desc", LocalDateTime.now(), Duration.ofMinutes(10));

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task3));
    }

    @Test
    public void shouldRemoveTaskFromEndOfHistory() {
        Task task1 = new Task(1, "Task1", "Description", LocalDateTime.now(), Duration.ofMinutes(15));
        Task task2 = new Task(2, "Task2", "Description ", LocalDateTime.now(), Duration.ofMinutes(20));

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

}