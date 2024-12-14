package com.kanban.TaskTracker.controller;

import com.kanban.TaskTracker.model.*;
import com.kanban.TaskTracker.util.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;

    private int currentId = 1;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public int generateId() {
        return ++currentId;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<EpicTask> getAllEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public int createTask(Task task) {
        final int id = generateId();
        task.setId(id);
        tasks.put(task.getId(), task);
        historyManager.add(task);
        return id;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        historyManager.add(task);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public int createEpicTask(EpicTask epic) {
        final int id = generateId();
        epic.setId(id);
        epicTasks.put(epic.getId(), epic);
        historyManager.add(epic);
        return id;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        EpicTask epic = epicTasks.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void updateEpicTask(EpicTask epic) {
        epicTasks.put(epic.getId(), epic);
        historyManager.add(epic);
    }

    @Override
    public void deleteAllEpics() {
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (EpicTask epic : epicTasks.values()) {
            epic.getSubTasks().clear();
            updateEpicTask(epic);
        }
        subTasks.clear();
    }

    @Override
    public void deleteEpicTaskById(int id) {
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            for (SubTask sub : epic.getSubTasks()) {
                subTasks.remove(sub.getId());
            }
        }
    }

    @Override
    public int createSubTask(SubTask sub) {
        final int id = generateId();
        sub.setId(id);
        subTasks.put(sub.getId(), sub);
        EpicTask epic = epicTasks.get(sub.getEpicId());
        if (epic != null) {
            epic.addSubTask(sub);
            updateStatus(epic);
        }
        return id;
    }

    @Override
    public void updateSubTask(SubTask sub) {
        subTasks.put(sub.getId(), sub);
        historyManager.add(sub);
        EpicTask epic = epicTasks.get(sub.getEpicId());
        if (epic != null) {
            updateStatus(epic);
        }
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }


    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        EpicTask epic = epicTasks.get(epicId);

        if (epic != null) {
            return new ArrayList<>(epic.getSubTasks());
        }

        return new ArrayList<>();
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask sub = subTasks.remove(id);
        if (sub != null) {
            EpicTask epic = epicTasks.get(sub.getEpicId());
            if (epic != null) {
                epic.removeSubTask(sub);
                updateStatus(epic);
            }
        }
    }

    public void updateStatus(EpicTask epic) {
        ArrayList<SubTask> subList = epic.getSubTasks();

        if (subList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean areAllDone = true;
        boolean areAllNew = true;

        for (SubTask sub : subList) {
            if (sub.getStatus() != TaskStatus.DONE) {
                areAllDone = false;
            }
            if (sub.getStatus() != TaskStatus.NEW) {
                areAllNew = false;
            }
        }

        if (areAllDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (areAllNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
