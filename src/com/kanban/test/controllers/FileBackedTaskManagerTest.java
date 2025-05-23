package com.kanban.test.controllers;

import com.kanban.tracker.controllers.FileBackedTaskManager;
import com.kanban.tracker.exceptions.ManagerSaveException;
import com.kanban.tracker.model.EpicTask;
import com.kanban.tracker.model.SubTask;
import com.kanban.tracker.model.Task;
import com.kanban.tracker.util.TaskStatus;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            File tempFile = Files.createTempFile("FileManagerTest", ".csv").toFile();
            tempFile.deleteOnExit();
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось создать временный файл");
        }
    }

    @Test
    public void shouldSaveAndLoadEmptyFile() throws IOException {
        File file = File.createTempFile("EmptyFileTest", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager savedManager = new FileBackedTaskManager(file);
        Task task = new Task(1, "Task", "Task Description", null, Duration.ZERO);
        int taskId = savedManager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loaded = loadedManager.getTaskById(taskId);

        assertNotNull(loaded, "Задача должна быть загружена из файла");
        assertEquals("Task", loaded.getTaskName(), "Название задачи должно совпадать");
        assertEquals("Task Description", loaded.getDescription(), "Описание задачи должно совпадать");
    }

    @Test
    public void shouldSaveAndLoadSingleTask() throws IOException {
        File file = File.createTempFile("SingleTaskTest", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager savedManager = new FileBackedTaskManager(file);
        Task task = new Task(1, "Task", "Task Description", null, Duration.ZERO);
        task.setStatus(TaskStatus.IN_PROGRESS);
        savedManager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loaded = loadedManager.getTaskById(1);

        assertNotNull(loaded);
        assertEquals("Task", loaded.getTaskName());
        assertEquals("Task Description", loaded.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, loaded.getStatus());
    }

    @Test
    public void shouldSaveAndLoadEpicWithSubTasks() throws IOException {
        File file = File.createTempFile("EpicWithSubsTest", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager savedManager = new FileBackedTaskManager(file);

        EpicTask epic = new EpicTask(1, "Epic", "Epic Description");
        savedManager.createEpicTask(epic);

        SubTask sub1 = new SubTask(2, "SubTask1", "Sub Description1", 1, null, Duration.ZERO);
        SubTask sub2 = new SubTask(3, "SubTask2", "Sub Description2", 1, null, Duration.ZERO);
        savedManager.createSubTask(sub1);
        savedManager.createSubTask(sub2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        EpicTask loadedEpic = loadedManager.getEpicTaskById(1);
        SubTask loadedSub1 = loadedManager.getSubTaskById(2);
        SubTask loadedSub2 = loadedManager.getSubTaskById(3);

        assertNotNull(loadedEpic, "Epic должен быть загружен");
        assertEquals("Epic", loadedEpic.getTaskName());

        assertNotNull(loadedSub1, "SubTask1 должен быть загружен");
        assertEquals(1, loadedSub1.getEpicId());
        assertEquals("SubTask1", loadedSub1.getTaskName());

        assertNotNull(loadedSub2, "SubTask2 должен быть загружен");
        assertEquals(1, loadedSub2.getEpicId());
        assertEquals("SubTask2", loadedSub2.getTaskName());

        assertEquals(2, loadedEpic.getSubTasks().size(), "Epic должен содержать 2 подзадачи");
    }
}
