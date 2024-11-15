import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {

    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int currentId = 1;

    public int generateId() {
        return currentId++;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<EpicTask> getAllEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void createEpicTask(EpicTask epic) {
        epicTasks.put(epic.getId(), epic);
    }

    public EpicTask getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    public void updateEpicTask(EpicTask epic) {
        epicTasks.put(epic.getId(), epic);
    }

    public void deleteAllEpics() {
        epicTasks.clear();
    }

    public void deleteEpicTaskById(int id) {
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            for (SubTask sub : epic.getSubTasks()) {
                subTasks.remove(sub.getId());
            }
        }
    }

    public void createSubTask(SubTask sub) {
        subTasks.put(sub.getId(), sub);
        EpicTask epic = epicTasks.get(sub.getEpicId());
        if (epic != null) {
            epic.addSubTask(sub);
            updateStatus(epic);
        }
    }

    public void updateSubTask(SubTask sub) {
        subTasks.put(sub.getId(), sub);
        EpicTask epic = epicTasks.get(sub.getEpicId());
        if (epic != null) {
            updateStatus(epic);
        }
    }

    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        EpicTask epic = epicTasks.get(epicId);

        if (epic != null) {
            return new ArrayList<>(epic.getSubTasks());
        }

        return new ArrayList<>();
    }

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

    private void updateStatus(EpicTask epic) {
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
