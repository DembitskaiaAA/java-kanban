import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<SubTask> subTasksInEpic;

    public Epic(String name, String description, int id, String status) {
        super(name, description, id, status);
        subTasksInEpic = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksInEpic=" + subTasksInEpic +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
