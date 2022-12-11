package task;
import java.util.Objects;

public class Task {
    public String name;
    public String description;
    public int id;
    public String status;

    public Task(String name, String description, int id, String status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Task otherClass = (Task) obj;
        return Objects.equals(name, otherClass.name) &&
                Objects.equals(description, otherClass.description) &&
                Objects.equals(id, otherClass.id) &&
                Objects.equals(status, otherClass.status);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }
}
