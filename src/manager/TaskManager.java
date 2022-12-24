package manager;

import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {


    void getAllTasks();
    void deleteAllTasks();
    Object getTaskById(int id);
    void create(Object object);
    void update(Object object);
    void deleteById(int id);
    ArrayList<SubTask> getEpicSubTasks(int id);
    List<Task> getHistory();

    int countId();
}

