package task;
import manager.Status;

import java.util.ArrayList;
public class Epic extends Task {
    public ArrayList<Integer> subTasksInEpic;

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
        subTasksInEpic = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Task.Epic{" +
                "name='" + name +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", subTasksInEpic=" + subTasksInEpic + '\'' +
                '}';
    }
}
