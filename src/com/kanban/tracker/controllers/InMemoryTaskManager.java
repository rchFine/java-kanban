package com.kanban.tracker.controllers;

import com.kanban.tracker.model.*;
import com.kanban.tracker.util.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;
    protected int currentId = 1;

    protected final Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return Integer.compare(task1.getId(), task2.getId());
        }
        if (task1.getStartTime() == null) return 1;
        if (task2.getStartTime() == null) return -1;
        int compare = task1.getStartTime().compareTo(task2.getStartTime());
        return compare != 0 ? compare : Integer.compare(task1.getId(), task2.getId());
    });

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private boolean isOverLapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null
                || task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }
        boolean noOverlap = task1.getEndTime().isEqual(task2.getStartTime()) || task1.getEndTime().isBefore(task2.getStartTime())
                || task1.getStartTime().isEqual(task2.getEndTime()) || task1.getStartTime().isAfter(task2.getEndTime());

        return !noOverlap;
    }

    private boolean hasIntersection(Task task) {
        return getPrioritizedTasks().stream()
                .filter(existingTask -> existingTask.getId() != task.getId())
                .anyMatch(existingTask -> isOverLapping(task, existingTask));
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        if (hasIntersection(task)) {
            throw new IllegalArgumentException("Невозможно создать задачу. Пересечение с другой задачей");
        }
        final int id = generateId();
        task.setId(id);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
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
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null) {
            prioritizedTasks.remove(oldTask);
        }

        if (hasIntersection(task)) {
            prioritizedTasks.add(oldTask);
            throw new IllegalArgumentException("Невозможно обновить задачу. Пересечение с другой задачей.");
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        Task removedTask = tasks.remove(id);
        if (removedTask != null) {
            prioritizedTasks.remove(removedTask);
            historyManager.remove(id);
        }
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
        updateStatus(epic);
        epicTasks.put(epic.getId(), epic);
        historyManager.add(epic);
    }

    @Override
    public void deleteAllEpics() {
        epicTasks.values().forEach(epic -> {
            historyManager.remove(epic.getId());
            epic.getSubTasks().stream()
                    .map(SubTask::getId)
                    .forEach(historyManager::remove);
        });
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        prioritizedTasks.removeAll(subTasks.values());
        subTasks.clear();

        epicTasks.values().forEach(epic -> {
            epic.getSubTasks().clear();
            updateEpicTask(epic);
            epic.updateTimeAndDuration();
        });
    }

    @Override
    public void deleteEpicTaskById(int id) {
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            epic.getSubTasks().stream()
                    .map(SubTask::getId)
                    .forEach(subId -> {
                        subTasks.remove(subId);
                        historyManager.remove(subId);
                    });
        }
        historyManager.remove(id);
    }

    @Override
    public int createSubTask(SubTask sub) {
        if (hasIntersection(sub)) {
            throw new IllegalArgumentException("Невозможно создать подзадачу. Пересечение с другой задачей");
        }

        EpicTask epic = epicTasks.get(sub.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id " + sub.getEpicId() + " не существует");
        }
        final int id = generateId();
        sub.setId(id);
        subTasks.put(id, sub);
        prioritizedTasks.add(sub);

        epic.addSubTask(sub);
        updateStatus(epic);
        epic.updateTimeAndDuration();

        return id;
    }

    @Override
    public void updateSubTask(SubTask sub) {
        SubTask oldSub = subTasks.get(sub.getId());
        if (oldSub != null) {
            prioritizedTasks.remove(oldSub);
        }
        if (hasIntersection(sub)) {
            throw new IllegalArgumentException("Невозможно обновить подзадачу. Пересечение с другой задачей.");
        }
        subTasks.put(sub.getId(), sub);
        prioritizedTasks.add(sub);
        EpicTask epic = epicTasks.get(sub.getEpicId());
        if (epic != null) {
            updateStatus(epic);
            epic.updateTimeAndDuration();
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
            prioritizedTasks.remove(sub);
            EpicTask epic = epicTasks.get(sub.getEpicId());
            if (epic != null) {
                epic.removeSubTask(sub);
                updateStatus(epic);
                epic.updateTimeAndDuration();
            }
            historyManager.remove(id);
        }
    }

    public void updateStatus(EpicTask epic) {
        List<SubTask> subList = epic.getSubTasks();

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
