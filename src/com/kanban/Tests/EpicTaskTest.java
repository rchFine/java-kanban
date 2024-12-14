package com.kanban.Tests;

import com.kanban.TaskTracker.model.EpicTask;
import com.kanban.TaskTracker.model.SubTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {
    @Test
    public void epicShouldNotAddItselfAsSubTask() {
        EpicTask epic = new EpicTask(1, "Epic Task", "Epic Description");

        SubTask subTask = new SubTask(1, "Epic Task Sub", "Epic Description", 1);

        epic.addSubTask(subTask);

        assertFalse(epic.getSubTasks().contains(epic), "EpicTask содержит себя как подзадачу");
    }

}