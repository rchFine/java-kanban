package com.kanban.test.controllers;

import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.model.*;
import com.kanban.tracker.util.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManager manager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        manager = createTaskManager();
    }

    @Test
    public void shouldCreateAndGetTask() {
        Task task = new Task(0, "Task 1", "Description", LocalDateTime.now(), Duration.ofMinutes(15));
        int id = manager.createTask(task);
        Task savedTask = manager.getTaskById(id);
        assertNotNull(savedTask);
        assertEquals(task.getTaskName(), savedTask.getTaskName());
        assertEquals(TaskStatus.NEW, savedTask.getStatus());
    }

    @Test
    public void shouldCalculateEpicStatusCorrectly() {
        EpicTask epic = new EpicTask(0, "Epic", "Epic Description");
        int epicId = manager.createEpicTask(epic);

        SubTask sub1 = new SubTask(manager.generateId(), "Sub1", "Description 1", epicId,
                LocalDateTime.now(), Duration.ofMinutes(10));
        SubTask sub2 = new SubTask(manager.generateId(), "Sub2", "Description 2", epicId,
                LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(10));
        manager.createSubTask(sub1);
        manager.createSubTask(sub2);

        assertEquals(TaskStatus.NEW, manager.getEpicTaskById(epicId).getStatus());

        manager.getSubTaskById(sub1.getId()).setStatus(TaskStatus.DONE);
        manager.getSubTaskById(sub2.getId()).setStatus(TaskStatus.DONE);

        EpicTask epicToUpdate = manager.getEpicTaskById(epicId);
        manager.updateEpicTask(epicToUpdate);

        assertEquals(TaskStatus.DONE, manager.getEpicTaskById(epicId).getStatus());

        manager.getSubTaskById(sub1.getId()).setStatus(TaskStatus.NEW);

        epicToUpdate = manager.getEpicTaskById(epicId);
        manager.updateEpicTask(epicToUpdate);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicTaskById(epicId).getStatus());

        manager.getSubTaskById(sub1.getId()).setStatus(TaskStatus.IN_PROGRESS);

        epicToUpdate = manager.getEpicTaskById(epicId);
        manager.updateEpicTask(epicToUpdate);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicTaskById(epicId).getStatus());
    }

    @Test
    public void shouldHaveLinkedEpicForSubTask() {
        EpicTask epic = new EpicTask(0, "Epic", "Description");
        int epicId = manager.createEpicTask(epic);

        SubTask sub = new SubTask(0, "Sub", "Description", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        int subId = manager.createSubTask(sub);
        SubTask savedSub = manager.getSubTaskById(subId);

        assertNotNull(savedSub);
        assertEquals(epicId, savedSub.getEpicId());
        assertNotNull(manager.getEpicTaskById(epicId));
    }

    @Test
    public void shouldDetectTimeIntersections() {
        LocalDateTime start = LocalDateTime.of(2025,5,23,10,0);

        Task task1 = new Task(0, "Task1", "Description", start, Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task(0, "Task2", "Description", start.plusMinutes(10), Duration.ofMinutes(20));

        assertThrows(IllegalArgumentException.class, () -> {
            manager.createTask(task2);
        });

        Task task3 = new Task(0, "Task3", "Description ", start.plusMinutes(40), Duration.ofMinutes(10));
        assertDoesNotThrow(() -> {
            manager.createTask(task3);
        });
    }

    @Test
    public void shouldAllowTaskStartingExactlyWhenAnotherEnds() {
        LocalDateTime start = LocalDateTime.now();

        Task task1 = new Task(manager.generateId(), "Task1", "Description", start, Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task(manager.generateId(), "Task2", "Description", start.plusMinutes(30), Duration.ofMinutes(30));
        assertDoesNotThrow(() -> {
            manager.createTask(task2);
        });
    }
}