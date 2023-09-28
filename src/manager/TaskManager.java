package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public interface TaskManager {
    void getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void create(Object object);

    void update(Object object);

    void deleteById(int id);

    ArrayList<SubTask> getEpicSubTasks(int id);

    List<Task> getHistory();

    int countId();

    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, SubTask> getSubTasks();
    SortedSet getPrioritizedTasks();
}

