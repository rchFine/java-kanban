package com.kanban.Tests;

import com.kanban.TaskTracker.controller.InMemoryTaskManager;
import com.kanban.TaskTracker.model.*;
import com.kanban.TaskTracker.util.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    public void taskSubclassesWithSameIdShouldBeEqual() {
        SubTask subTask1 = new SubTask(1, "SubTask 1", "Description 1", 5);
        SubTask subTask2 = new SubTask(1, "SubTask 2", "Description 2", 7);

        assertEquals(subTask1, subTask2);

        EpicTask epic1 = new EpicTask(1, "Epic 1", "Description 1");
        EpicTask epic2 = new EpicTask(1, "Epic 2", "Description 2");

        assertEquals(epic1, epic2, "Подклассы с одинаковым id не эквивалентны");
    }

    @Test
    public void subTaskShouldNotBeItsOwnEpic() {
        EpicTask epic = new EpicTask(1, "Epic Task", "Epic Description");
        SubTask subTask = new SubTask(2, "SubTask", "SubTask Description", epic.getId());

        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        taskManager.createEpicTask(epic);
        taskManager.createSubTask(subTask);

        assertTrue(taskManager.getSubTasksByEpic(subTask.getId()).isEmpty(), "Сабтаск не должен быть своим эпиком");
    }
}