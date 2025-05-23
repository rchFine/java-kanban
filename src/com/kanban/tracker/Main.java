package com.kanban.tracker;

import com.kanban.tracker.controllers.*;
import com.kanban.tracker.model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager manager = new InMemoryTaskManager(historyManager);


        Task task1 = new Task(manager.generateId(), "Задача №1", "её описание",
                LocalDateTime.of(2025,5,22,20,0), Duration.ofMinutes(60));
        Task task2 = new Task(manager.generateId(), "Задача №2 ", "описание 2-ой задачи",
                LocalDateTime.of(2025,5,22,22,0), Duration.ofMinutes(90));

        manager.createTask(task1);
        manager.createTask(task2);

        EpicTask relocate = new EpicTask(manager.generateId(), "Организация переезда", "Список задач для переезда");
        manager.createEpicTask(relocate);

        SubTask packBoxes = new SubTask(manager.generateId(), "Собрать коробки", "Положить всё в коробки",
                relocate.getId(), LocalDateTime.of(2025,5,24,10,0), Duration.ofMinutes(45));
        SubTask packCat = new SubTask(manager.generateId(), "Упаковать кошку", "В переноску",
                relocate.getId(), LocalDateTime.of(2025,5,24,12,0), Duration.ofMinutes(15));

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
