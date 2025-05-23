package com.kanban.tracker.controllers;

import com.kanban.tracker.model.EpicTask;
import com.kanban.tracker.model.SubTask;
import com.kanban.tracker.model.Task;
import com.kanban.tracker.exceptions.ManagerSaveException;
import com.kanban.tracker.util.TaskStatus;
import com.kanban.tracker.util.TaskType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,startTime,duration\n");

            for (Task task : tasks.values()) {
                writer.write(task.toCSVString() + "\n");
            }

            for (EpicTask epic : epicTasks.values()) {
                writer.write(epic.toCSVString() + "\n");
            }

            for (SubTask sub : subTasks.values()) {
                writer.write(sub.toCSVString() + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла: " + file.getName());
        }
    }

    @Override
    public int createTask(Task task) {
        if (hasIntersection(task)) {
            throw new IllegalArgumentException("Невозможно создать задачу. Пересечение с другой задачей");
        }
        currentId = Math.max(currentId, task.getId() + 1);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        save();
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null) {
            prioritizedTasks.remove(oldTask);
        }

        if (hasIntersection(task)) {
            if (oldTask != null) {
                prioritizedTasks.add(oldTask);
            }
            throw new IllegalArgumentException("Невозможно создать подзадачу. Пересечение с другой задачей");
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public int createEpicTask(EpicTask epic) {
        currentId = Math.max(currentId, epic.getId() + 1);
        epicTasks.put(epic.getId(), epic);
        save();
        return epic.getId();
    }

    @Override
    public void updateEpicTask(EpicTask epic) {
        super.updateEpicTask(epic);
        save();
    }

    @Override
    public void deleteEpicTaskById(int id) {
        super.deleteEpicTaskById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public int createSubTask(SubTask sub) {
        if (hasIntersection(sub)) {
            throw new IllegalArgumentException("Невозможно создать подзадачу. Пересечение с другой задачей");
        }

        EpicTask epic = epicTasks.get(sub.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id " + sub.getEpicId() + " не существует ");
        }

        currentId = Math.max(currentId, sub.getId() + 1);
        subTasks.put(sub.getId(), sub);
        prioritizedTasks.add(sub);

        epic.addSubTask(sub);
        updateStatus(epic);
        epic.updateTimeAndDuration();
        save();
        return sub.getId();
    }

    @Override
    public void updateSubTask(SubTask sub) {
        SubTask oldSub = subTasks.get(sub.getId());
        if (oldSub != null) {
            prioritizedTasks.remove(oldSub);
        }

        if (hasIntersection(sub)) {
            if (oldSub != null) {
                prioritizedTasks.add(oldSub);
            }
            throw new IllegalArgumentException("Пересечение задач по времени");
        }

        subTasks.put(sub.getId(), sub);
        prioritizedTasks.add(sub);
        EpicTask epic = epicTasks.get(sub.getEpicId());

        if (epic != null) {
            epic.removeSubTask(oldSub);
            epic.addSubTask(sub);
            updateStatus(epic);
            epic.updateTimeAndDuration();
        }

        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String allLines = Files.readString(file.toPath());
            String[] splitAllLines = allLines.split("\n");

            for (int i = 1; i < splitAllLines.length; i++) {
                String line = splitAllLines[i].trim();
                if (line.isEmpty()) continue;
                Task task = fromString(line);

                switch (task.getType()) {
                    case TASK -> manager.tasks.put(task.getId(), task);
                    case EPIC -> manager.epicTasks.put(task.getId(), (EpicTask) task);
                    case SUBTASK -> manager.subTasks.put(task.getId(), (SubTask) task);
                }

                manager.currentId = Math.max(manager.currentId, task.getId() + 1);
            }

            for (SubTask sub : manager.subTasks.values()) {
                EpicTask epic = manager.epicTasks.get(sub.getEpicId());
                if (epic != null) {
                    epic.addSubTask(sub);
                }
            }

            for (EpicTask epic : manager.epicTasks.values()) {
                epic.updateTimeAndDuration();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + file.getName());
        }

        return manager;
    }

    private static Task fromString(String value) {
        String[] splitValue = value.split(",", -1);
        int id = Integer.parseInt(splitValue[0]);
        TaskType type = TaskType.valueOf(splitValue[1]);
        String name = splitValue[2];
        TaskStatus status = TaskStatus.valueOf(splitValue[3]);
        String description = splitValue[4];
        String startTimeString = splitValue.length > 6 ? splitValue[6] : "";
        String durationString = splitValue.length > 7 ? splitValue[7] : "";
        LocalDateTime startTime = (startTimeString.isEmpty() || startTimeString.equals("0")
                ? null
                : LocalDateTime.parse(startTimeString));
        Duration duration = (durationString.isEmpty() || durationString.equals("0")
                ? null : Duration.ofMinutes(Long.parseLong(durationString)));

        switch (type) {
            case TASK -> {
                Task task = new Task(id, name, description, startTime, duration);
                task.setStatus(status);
                return task;
            }
            case EPIC -> {
                EpicTask epic = new EpicTask(id, name, description);
                epic.setStatus(status);
                return epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(splitValue[5]);
                SubTask sub = new SubTask(id, name, description, epicId, startTime, duration);
                sub.setStatus(status);
                return sub;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
