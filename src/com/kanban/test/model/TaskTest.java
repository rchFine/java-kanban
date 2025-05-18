package com.kanban.test.model;

import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.model.Task;
import com.kanban.tracker.util.Managers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Task 1", "Description 1");
        Task task2 = new Task(1, "Task 2", "Description 2");

        assertEquals(task1, task2, "Такси с одинаковым id должны быть равны");
    }

    @Test
    public void taskShouldRemainUnchangedWhenAddedToManager() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task(1, "Task", "Task Description");

        taskManager.createTask(task);
        Task testTask = taskManager.getTaskById(task.getId());

        assertEquals(task.getTaskName(), testTask.getTaskName(), "Название задачи должно совпадать");
        assertEquals(task.getDescription(), testTask.getDescription(), "Описание задачи должно совпадать");
        assertEquals(task.getStatus(), testTask.getStatus(), "Статус задач должен совпадать");
    }

    @Test
    public void tasksWithGivenAndGeneratedIdsShouldNotConflict() {
        TaskManager taskManager = Managers.getDefault();

        Task taskGivenId = new Task(10, "Task 1", "Task 1 Description");
        taskManager.createTask(taskGivenId);

        Task taskGeneratedId = new Task(taskManager.generateId(), "Task 2", "Task 2 Description");
        taskManager.createTask(taskGeneratedId);

        assertNotEquals(taskGivenId.getId(), taskGeneratedId.getId());
    }
}