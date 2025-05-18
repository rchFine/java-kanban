package com.kanban.test.controllers;

import com.kanban.tracker.controllers.HistoryManager;
import com.kanban.tracker.controllers.InMemoryHistoryManager;
import com.kanban.tracker.controllers.InMemoryTaskManager;
import com.kanban.tracker.model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    @Test
    public void shouldAddTasksToHistoryInCorrectOrder() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task(1, "Task 1", "Description 1");
        Task task2 = new Task(2, "Task 2", "Description 2");
        Task task3 = new Task(3, "Task 3", "Description 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "История должна содержать 3 задачи");
        assertEquals(task1, history.get(0), "Первая задача должна быть task1");
        assertEquals(task2, history.get(1), "Вторая задача должна быть task2");
        assertEquals(task3, history.get(2), "Третья задача должна быть task3");
    }

    @Test
    public void shouldCorrectlyRemoveFirstAndLastTasks() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task(1, "Task 1", "Description 1");
        Task task2 = new Task(2, "Task 2", "Description 2");
        Task task3 = new Task(3, "Task 3", "Description 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);
        historyManager.remove(3);

        List<Task> expected = List.of(task2);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "Остаться должна только задача между");
    }

    @Test
    public void shouldMoveReaddedTaskToEnd() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task(1, "Task 1", "Description 1");
        Task task2 = new Task(2, "Task 2", "Description 2");
        Task task3 = new Task(3, "Task 3", "Description 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2);

        List<Task> expected = List.of(task1, task3, task2);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "Повторно добавленная задача должна переместиться в конец");
    }
}
