package tests;

import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.Status;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    Task epic1;
    Task subTask1;
    Task subTask1_1;

    public void createTasks(Status status1, Status status2, boolean isCreateSubTasks) {
        manager.deleteAllTasks();
        epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                status1, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        subTask1_1 = new SubTask("Гости", "Позвать гостей", manager.countId(), status2,
                epic1.id, LocalDateTime.of(2023, 1, 1, 11, 00),
                Duration.ofMinutes(30));
        manager.create(epic1);
        if (isCreateSubTasks) {
            manager.create(subTask1);
            manager.create(subTask1_1);
        }
    }

    @Test
    public void testEpicStatusWithoutSubTasks() {
        createTasks(null, null, false);
        assertEquals(Status.NEW, epic1.status, "При отсутствии подзадач статус не равняется New");
    }

    @Test
    public void testEpicStatusWithSubTasksStatusNew() {
        createTasks(Status.NEW, Status.NEW, true);
        assertEquals(Status.NEW, epic1.status, "При статусе подзадач NEW статус у Epic не равняется New");
    }

    @Test
    public void testEpicStatusWithSubTasksStatusDone() {
        createTasks(Status.DONE, Status.DONE, true);
        assertEquals(epic1.status, Status.DONE, "При статусе подзадач DONE статус у Epic " +
                "не равняется DONE");
    }

    @Test
    public void testEpicStatusWithSubTasksStatusNewAndDone() {
        createTasks(Status.DONE, Status.NEW, true);
        assertEquals(epic1.status, Status.IN_PROGRESS, "При статусе подзадач DONE," +
                "NEW статус у Epic не равняется IN_PROGRESS");
    }

    @Test
    public void testEpicStatusWithSubTasksStatusINPROGRESS() {
        createTasks(Status.IN_PROGRESS, Status.IN_PROGRESS, true);
        assertEquals(epic1.status, Status.IN_PROGRESS, "При статусе подзадач IN_PROGRESS" +
                " статус у Epic не равняется IN_PROGRESS");
    }
}