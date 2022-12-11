package task;
public class SubTask extends Task {
    public int epicsId;
    public SubTask(String name, String description, int id, String status, int epicsId) {
        super(name, description, id, status);
        this.epicsId = epicsId;
    }

    @Override
    public String toString() {
        return "Task.SubTask{" +
                "epicsId=" + epicsId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}