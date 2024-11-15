public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

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

        System.out.println("Все задачи - " + manager.getAllTasks());
        System.out.println("Все эпики - " + manager.getAllEpics());
        System.out.println("Все подзадачи - " +manager.getAllSubTasks());

        packBoxes.setStatus(TaskStatus.DONE);
        manager.updateSubTask(packBoxes);

        packCat.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(packCat);

        System.out.println("После обновления статусов подзадач:");
        System.out.println("Эпики" + manager.getAllEpics());

        manager.deleteTaskById(task1.getId());
        manager.deleteEpicTaskById(relocate.getId());

        System.out.println("Задачи:");
        System.out.println("Задачи после удаления - " + manager.getAllTasks());
        System.out.println("Эпики после удаления - :" + manager.getAllEpics());
        System.out.println("Подзадачи после удаления - :" + manager.getAllSubTasks());
    }
}
