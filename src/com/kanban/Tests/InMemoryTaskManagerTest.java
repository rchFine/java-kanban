package com.kanban.Tests;

import com.kanban.TaskTracker.controller.TaskManager;
import com.kanban.TaskTracker.model.*;
import com.kanban.TaskTracker.util.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    public void ShouldAddAndFindTasksById() {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task(taskManager.generateId(), "Task", "Task Description");
        EpicTask epic = new EpicTask(taskManager.generateId(), "Epic", "Epic Description");
        SubTask subTask = new SubTask(taskManager.generateId(), "SubTask", "Sub Description",
                epic.getId());

        taskManager.createTask(task);
        taskManager.createEpicTask(epic);
        taskManager.createSubTask(subTask);

        assertEquals(task, taskManager.getTaskById(task.getId()), "Задача должна быть найдена по id");
        assertEquals(epic, taskManager.getEpicTaskById(epic.getId()), "Эпик должен быть найден по id");
        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()), "Подзадача должна быть найдена по id");
    }
}