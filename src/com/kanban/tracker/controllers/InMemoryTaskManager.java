package com.kanban.tracker.controllers;

import com.kanban.tracker.exceptions.NotFoundException;
import com.kanban.tracker.model.*;
import com.kanban.tracker.util.*;
import java.time.LocalDateTime;
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
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    protected boolean hasIntersection(Task task) {
        return getPrioritizedTasks().stream()
                .filter(existingTask -> task.getId() == 0 || existingTask.getId() != task.getId())
                .anyMatch(existingTask -> isOverLapping(task, existingTask));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
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

        if (task.getTaskName() != null && task.getDuration() != null) {
            if (hasIntersection(task)) {
                throw new IllegalArgumentException("Невозможно создать задачу. Пересечение с другой задачей");
            }
            prioritizedTasks.add(task);
        }

        tasks.put(task.getId(), task);
        return id;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с id " + id + " не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new NotFoundException("Задача с id " + task.getId() + " не найдена");
        }
        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask);

        if (task.getStartTime() != null && task.getDuration() != null) {
            if (hasIntersection(task)) {
                prioritizedTasks.add(oldTask);
                throw new IllegalArgumentException("Невозможно обновить задачу. Пересечение с другой задачей.");
            }
            prioritizedTasks.add(task);
        }
        tasks.put(task.getId(), task);
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
        if (removedTask == null) {
            throw new NotFoundException("Задача с id " + id + " не найдена.");
        }
        prioritizedTasks.remove(removedTask);
        historyManager.remove(id);
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
        if (epic == null) {
            throw new NotFoundException("Эпик с id " + id + " не найден.");
        }
        historyManager.add(epic);
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
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }

        for (EpicTask epic : epicTasks.values()) {
            historyManager.remove(epic.getId());
        }

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
        if (epic == null) {
            throw new NotFoundException("Эпик с id " + id + " не найден");
        }

        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
            historyManager.remove(subTask.getId());
        }

        historyManager.remove(id);
    }

    @Override
    public int createSubTask(SubTask sub) {
        final int epicId = sub.getEpicId();
        EpicTask epic = epicTasks.get(epicId);

        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не существует");
        }

        if (sub.getStartTime() != null && sub.getDuration() != null) {
            if (hasIntersection(sub)) {
                throw new IllegalArgumentException("Невозможно создать подзадачу. Пересечение с другой задачей");
            }
            prioritizedTasks.add(sub);
        }

        final int id = generateId();
        sub.setId(id);
        subTasks.put(id, sub);
        epic.addSubTask(sub);
        epic.updateTimeAndDuration();
        updateStatus(epic);

        return id;
    }

    @Override
    public void updateSubTask(SubTask sub) {
        final int id = sub.getId();
        final int epicId = sub.getEpicId();

        if (!subTasks.containsKey(id)) {
            throw new IllegalArgumentException("Подзадача с id " + id + " не найдена.");
        }

        EpicTask epic = epicTasks.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с таким id " + epicId + " не существует");
        }

        SubTask oldSub = subTasks.get(id);
        prioritizedTasks.remove(oldSub);

        if (sub.getStartTime() != null && sub.getDuration() != null) {
            if (hasIntersection(sub)) {
                prioritizedTasks.add(oldSub);
                throw new IllegalArgumentException("Невозможно обновить подзадачу. Пересечение с другой задачей.");
            }
            prioritizedTasks.add(sub);
        }

        subTasks.put(id, sub);
        updateStatus(epic);
        epic.updateTimeAndDuration();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            throw new NotFoundException("Подзадача с id " + id + " не найдена");
        }
        historyManager.add(subTask);
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

        if (sub == null) {
            throw new NotFoundException("Подзадача с id " + id + " не найдена");
        }

        prioritizedTasks.remove(sub);
        EpicTask epic = epicTasks.get(sub.getEpicId());
        if (epic != null) {
            epic.removeSubTask(sub);
            updateStatus(epic);
            epic.updateTimeAndDuration();
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
