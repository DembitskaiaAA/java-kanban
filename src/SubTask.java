import java.util.Objects;

public class SubTask extends Task {
    int epicsId;
    public SubTask(String name, String description, int id, String status, int epicsId) {
        super(name, description, id, status);
        this.epicsId = epicsId;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || this.getClass() == obj.getClass()) return false;
        SubTask other = (SubTask) obj;
        return Objects.equals(name, other.name) &&
                Objects.equals(description, other.description) &&
                Objects.equals(id, other.id) &&
                Objects.equals(status, other.status) &&
                Objects.equals(epicsId, other.epicsId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicsId=" + epicsId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
