package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    public static List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() < 10) {
            history.add(task);
        } else {
            history.add(0, task);
            history.remove(10);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
