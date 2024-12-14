package com.kanban.TaskTracker.controller;

import com.kanban.TaskTracker.model.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    public int generateId();

    List<Task> getHistory();

    public ArrayList<Task> getAllTasks();

    public ArrayList<SubTask> getAllSubTasks();

    public ArrayList<EpicTask> getAllEpics();

    public int createTask(Task task);

    public Task getTaskById(int id);

    public void updateTask(Task task);

    public void deleteAllTasks();

    public void deleteTaskById(int id);

    public int createEpicTask(EpicTask epic);

    public EpicTask getEpicTaskById(int id);

    public void updateEpicTask(EpicTask epic);

    public void deleteAllEpics();

    public void deleteAllSubTasks();

    public void deleteEpicTaskById(int id);

    public int createSubTask(SubTask sub);

    public void updateSubTask(SubTask sub);

    public SubTask getSubTaskById(int id);

    public ArrayList<SubTask> getSubTasksByEpic(int epicId);

    public void deleteSubTaskById(int id);

}
