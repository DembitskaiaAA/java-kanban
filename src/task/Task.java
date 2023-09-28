package task;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    public String name;
    public String description;
    public int id;
    public Status status;
    public LocalDateTime startTime;
    public Duration duration;
    public LocalDateTime endTime;


    public Task(String name, String description, int id, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.id = id;
        getEndTime();

    }

    public LocalDateTime getEndTime() {
        endTime = null;
        if (startTime != null && duration != null) {
            endTime = startTime.plusMinutes(duration.toMinutes());
        } else {
            endTime = null;
        }
        return endTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        String line = "";
        if (startTime != null && duration != null) {
            line = "Task{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", id=" + id +
                    ", status=" + status +
                    ", startTime=" + startTime.format(formatter) +
                    ", duration=" + duration.toMinutes() +
                    ", endTime=" + endTime.format(formatter) +
                    '}';
        } else {
            line = "Task{" +
                    "name='" + name + '\'' +
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
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, startTime, duration, endTime);
    }

}
