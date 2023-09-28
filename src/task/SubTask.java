package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SubTask extends Task {
    public int epicsId;

    public SubTask(String name, String description, int id, Status status, Integer epicsId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicsId = epicsId;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        String line = "";
        if (startTime != null && duration != null) {
            line = "SubTask{" +
                    "epicsId=" + epicsId +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", id=" + id +
                    ", status=" + status +
                    ", startTime=" + startTime.format(formatter) +
                    ", duration=" + duration.toMinutes() +
                    ", endTime=" + endTime.format(formatter) +
                    '}';
        } else {
            line = "SubTask{" +
                    "epicsId=" + epicsId +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", id=" + id +
                    ", status=" + status +
                    '}';
        }
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicsId == subTask.epicsId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicsId);
    }
}
