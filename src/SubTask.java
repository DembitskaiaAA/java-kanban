public class SubTask extends Task {
    int epicsId;
    public SubTask(String name, String description, int id, String status, int epicsId) {
        super(name, description, id, status);
        this.epicsId = epicsId;
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
