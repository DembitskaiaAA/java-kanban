package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    public ArrayList<Integer> subTasksInEpic;

    public Epic(String name, String description, int id, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        subTasksInEpic = new ArrayList<>();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        String line = "";
        if ((startTime != null && duration != null)) {
            line = "Epic{" +
                    "subTasksInEpic=" + subTasksInEpic +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", id=" + id +
                    ", status=" + status +
                    ", startTime=" + startTime.format(formatter) +
                    ", duration=" + duration.toMinutes() +
                    ", endTime=" + endTime.format(formatter) +
                    '}';
        } else {
            line = "Epic{" +
                    "subTasksInEpic=" + subTasksInEpic +
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
        Epic epic = (Epic) o;
        return Objects.equals(subTasksInEpic, epic.subTasksInEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksInEpic);
    }
}
