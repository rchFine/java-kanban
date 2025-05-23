package com.kanban.test.controllers;

import com.kanban.tracker.controllers.InMemoryHistoryManager;
import com.kanban.tracker.controllers.InMemoryTaskManager;
import com.kanban.tracker.model.EpicTask;
import com.kanban.tracker.model.SubTask;
import com.kanban.tracker.util.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class EpicCalculationTests {

    private InMemoryTaskManager manager;
    private EpicTask epic;

    @BeforeEach
    public void setup() {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        epic = new EpicTask(manager.generateId(), "Epic", "Epic Description");
        manager.createEpicTask(epic);
    }

    @Test
    public void shouldBeNewWhenAllSubTasksNew() {
        SubTask sub1 = new SubTask(manager.generateId(), "Sub1", "Description 1", epic.getId(),null, Duration.ZERO);
        SubTask sub2 = new SubTask(manager.generateId(), "Sub2", "Description 2", epic.getId(), null, Duration.ZERO);

        sub1.setStatus(TaskStatus.NEW);
        sub2.setStatus(TaskStatus.NEW);

        manager.createSubTask(sub1);
        manager.createSubTask(sub2);

        assertEquals(TaskStatus.NEW, manager.getEpicTaskById(epic.getId()).getStatus());
    }

    @Test
    public void shouldBeDoneWhenAllSubTasksDone() {
        SubTask sub1 = new SubTask(manager.generateId(), "Sub1", "Description 1", epic.getId(), null, Duration.ZERO);
        SubTask sub2 = new SubTask(manager.generateId(), "Sub2", "Description 2", epic.getId(), null, Duration.ZERO);

        sub1.setStatus(TaskStatus.DONE);
        sub2.setStatus(TaskStatus.DONE);

        manager.createSubTask(sub1);
        manager.createSubTask(sub2);

        assertEquals(TaskStatus.DONE, manager.getEpicTaskById(epic.getId()).getStatus());
    }

    @Test
    public void shouldBeInProgressWhenSubTasksNewAndDone() {
        SubTask sub1 = new SubTask(manager.generateId(), "Sub1", "Description 1", epic.getId(), null, Duration.ZERO);
        SubTask sub2 = new SubTask(manager.generateId(), "Sub2", "Description 2", epic.getId(), null, Duration.ZERO);

        sub1.setStatus(TaskStatus.NEW);
        sub2.setStatus(TaskStatus.DONE);

        manager.createSubTask(sub1);
        manager.createSubTask(sub2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicTaskById(epic.getId()).getStatus());
    }

    @Test
    public void shouldBeInProgressWhenAnySubTaskInProgress() {
        SubTask sub1 = new SubTask(manager.generateId(), "Sub1", "Description 1", epic.getId(), null, Duration.ZERO);
        SubTask sub2 = new SubTask(manager.generateId(), "Sub2", "Description 2", epic.getId(), null, Duration.ZERO);

        sub1.setStatus(TaskStatus.IN_PROGRESS);
        sub2.setStatus(TaskStatus.NEW);

        manager.createSubTask(sub1);
        manager.createSubTask(sub2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicTaskById(epic.getId()).getStatus());
    }

    @Test
    public void shouldBeNewWhenNoSubTasks() {
        assertEquals(TaskStatus.NEW, manager.getEpicTaskById(epic.getId()).getStatus());
    }
}