package com.kanban.test.controllers;

import com.kanban.tracker.controllers.InMemoryTaskManager;
import com.kanban.tracker.controllers.TaskManager;
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
    protected InMemoryTaskManager createTaskManager(){
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }
    @Test
    public void shouldAddAndFindTasksById() {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task(taskManager.generateId(), "Task", "Task Description", null, Duration.ZERO);
        EpicTask epic = new EpicTask(taskManager.generateId(), "Epic", "Epic Description");

        taskManager.createTask(task);
        taskManager.createEpicTask(epic);

        SubTask subTask = new SubTask(taskManager.generateId(), "SubTask", "Sub Description",
                epic.getId(), null, Duration.ZERO);
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
}
