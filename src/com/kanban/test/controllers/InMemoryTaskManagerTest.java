package com.kanban.test.controllers;

import com.kanban.tracker.controllers.InMemoryTaskManager;
import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.model.*;
import com.kanban.tracker.util.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    public void shouldAddAndFindTasksById() {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task(taskManager.generateId(), "Task", "Task Description");
        EpicTask epic = new EpicTask(taskManager.generateId(), "Epic", "Epic Description");
        SubTask subTask = new SubTask(taskManager.generateId(), "SubTask", "Sub Description",
                epic.getId());

        taskManager.createTask(task);
        taskManager.createEpicTask(epic);
        taskManager.createSubTask(subTask);

        Task findTask = taskManager.getTaskById(task.getId());
        EpicTask findEpic = taskManager.getEpicTaskById(epic.getId());
        SubTask findSub = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(findTask, "Задача должна существовать");
        assertEquals(task.getId(), findTask.getId(), "Id задач должны совпадать");

        assertNotNull(findEpic, "Эпик должен существовать");
        assertEquals(epic.getId(), findEpic.getId(), "Id эпиков должны совпадать");

        assertNotNull(subTask, "Подзадача должна существовать");
        assertEquals(subTask.getId(), findSub.getId(), "Id подзадач должны совпадать");
    }

    @Test
    public void epicShouldNotContainDeletedSubtask() {
        InMemoryTaskManager manager = new InMemoryTaskManager(Managers.getDefaultHistory());

        EpicTask epic = new EpicTask(manager.generateId(), "Epic", "Epic Description");
        manager.createEpicTask(epic);

        SubTask sub = new SubTask(manager.generateId(), "SubTask", "Sub Description", epic.getId());
        manager.createSubTask(sub);

        manager.deleteSubTaskById(sub.getId());

        assertFalse(epic.getSubTasks().contains(sub), "Эпик не должен содержать удалённую подзадачу");
    }
}