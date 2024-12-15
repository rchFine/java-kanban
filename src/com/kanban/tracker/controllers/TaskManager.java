package com.kanban.tracker.controllers;

import com.kanban.tracker.model.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int generateId();

    List<Task> getHistory();

    ArrayList<Task> getAllTasks();

    ArrayList<SubTask> getAllSubTasks();

    ArrayList<EpicTask> getAllEpics();

    int createTask(Task task);

    Task getTaskById(int id);

    void updateTask(Task task);

    void deleteAllTasks();

    void deleteTaskById(int id);

    int createEpicTask(EpicTask epic);

    EpicTask getEpicTaskById(int id);

    void updateEpicTask(EpicTask epic);

    void deleteAllEpics();

    void deleteAllSubTasks();

    void deleteEpicTaskById(int id);

    int createSubTask(SubTask sub);

    void updateSubTask(SubTask sub);

    SubTask getSubTaskById(int id);

    ArrayList<SubTask> getSubTasksByEpic(int epicId);

    void deleteSubTaskById(int id);

}
