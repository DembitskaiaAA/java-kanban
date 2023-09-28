package manager;

import java.util.List;
import task.Task;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    void removeAll();

    List<Task> getHistory();
    //Создан метод для обновления задачи
    void updateNode(Task task);
}

