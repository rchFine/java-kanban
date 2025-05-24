package com.kanban.test.controllers;

import com.kanban.tracker.controllers.InMemoryTaskManager;
import com.kanban.tracker.model.EpicTask;
import com.kanban.tracker.model.SubTask;
import com.kanban.tracker.model.Task;
import com.kanban.tracker.util.Managers;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    public void shouldAddAndFindTasksById() {

        Task task = new Task(manager.generateId(), "Task", "Task Description", null, Duration.ZERO);
        EpicTask epic = new EpicTask(manager.generateId(), "Epic", "Epic Description");

        manager.createTask(task);
        manager.createEpicTask(epic);

        SubTask subTask = new SubTask(manager.generateId(), "SubTask", "Sub Description",
                epic.getId(), null, Duration.ZERO);
        manager.createSubTask(subTask);

        Task findTask = manager.getTaskById(task.getId());
        EpicTask findEpic = manager.getEpicTaskById(epic.getId());
        SubTask findSub = manager.getSubTaskById(subTask.getId());

        assertNotNull(findTask, "Задача должна существовать");
        assertEquals(task.getId(), findTask.getId(), "Id задач должны совпадать");

        assertNotNull(findEpic, "Эпик должен существовать");
        assertEquals(epic.getId(), findEpic.getId(), "Id эпиков должны совпадать");

        assertNotNull(subTask, "Подзадача должна существовать");
        assertEquals(subTask.getId(), findSub.getId(), "Id подзадач должны совпадать");
    }
}
