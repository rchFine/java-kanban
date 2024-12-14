package com.kanban.TaskTracker;

import com.kanban.TaskTracker.controller.*;
import com.kanban.TaskTracker.model.*;

public class Main {

    public static void main(String[] args) {

        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager manager = new InMemoryTaskManager(historyManager);


        Task task1 = new Task(manager.generateId(), "Задача №1", "её описание");
        Task task2 = new Task(manager.generateId(), "Задача №2 ", "описание 2-ой задачи");

        manager.createTask(task1);
        manager.createTask(task2);

        EpicTask relocate = new EpicTask(manager.generateId(), "Организация переезда", "Список задач для переезда");
        manager.createEpicTask(relocate);

        SubTask packBoxes = new SubTask(manager.generateId(), "Собрать коробки", "Положить всё в коробки",
                relocate.getId());
        SubTask packCat = new SubTask(manager.generateId(), "Упаковать кошку", "В переноску",
                relocate.getId());

        manager.createSubTask(packBoxes);
        manager.createSubTask(packCat);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        printTasksHistory(historyManager);

    }

    private static void printTasksHistory(HistoryManager historyManager) {
        System.out.println("История:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }
}
