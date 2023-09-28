package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
        manager.deleteAllTasks();
    }

    @Test
    void getEpicSubTasksNormalConditionsTest() {
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.IN_PROGRESS, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));

        Task epic2 = new Epic("Квартира", "Продать квартиру", manager.countId(), null,
                null, null);
        Task subTask2 = new SubTask("Встретиться с риелтором", "Отдать ключи", manager.countId(),
                Status.NEW, epic2.id, null, null);

        manager.create(epic1);
        manager.create(subTask1);

        manager.create(epic2);
        manager.create(subTask2);

        List<SubTask> subTasksInEpic = manager.getEpicSubTasks(1);
        List<SubTask> myCheck = new ArrayList<>();
        myCheck.add((SubTask) subTask1);

        assertIterableEquals(subTasksInEpic, myCheck, "SubTasks не совпали");
    }

    @Test
    void getEpicSubTasksWithWrongIdTest() {
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.IN_PROGRESS, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));

        manager.create(epic1);
        manager.create(subTask1);

        assertThrows(NullPointerException.class, () -> {
            manager.getEpicSubTasks(-1);
        }, "Задача c неверным id = -1 нашлась");
    }

    @Test
    public void getPrioritizedTasksTest() {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 2, 8, 9, 0),
                Duration.ofMinutes(30));
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.IN_PROGRESS, epic1.id, LocalDateTime.of(2023, 2, 7, 9, 0),
                Duration.ofMinutes(30));

        Task epic2 = new Epic("Квартира", "Продать квартиру", manager.countId(), null,
                null, null);
        Task subTask2 = new SubTask("Встретиться с риелтором", "Отдать ключи", manager.countId(),
                Status.NEW, epic2.id, null, null);

        manager.create(task1);
        manager.create(epic1);
        manager.create(subTask1);
        manager.create(epic2);
        manager.create(subTask2);

        List<Integer> myCheck = new ArrayList<>(List.of(2, 3, 1, 4, 5));
        ;
        List<Integer> savedSortedSet = new ArrayList<>();
        manager.getPrioritizedTasks().stream().forEach((Consumer<Task>) task -> savedSortedSet.add(task.id));

        assertIterableEquals(savedSortedSet, myCheck, "Задачи неправильно отсортированы по времени");
    }

    @Test
    public void getAllTasksTest() {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 2, 8, 9, 0),
                Duration.ofMinutes(30));
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.IN_PROGRESS, epic1.id, LocalDateTime.of(2023, 2, 7, 9, 0),
                Duration.ofMinutes(30));

        Task epic2 = new Epic("Квартира", "Продать квартиру", manager.countId(), null,
                null, null);
        Task subTask2 = new SubTask("Встретиться с риелтором", "Отдать ключи", manager.countId(),
                Status.NEW, epic2.id, null, null);

        manager.create(task1);
        manager.create(epic1);
        manager.create(subTask1);
        manager.create(epic2);
        manager.create(subTask2);

        manager.getAllTasks();

        assertEquals(manager.allTasks.size(), 1, "Неверное количество Tasks сохранилось при вызове " +
                "метода getAllTasks()");
        assertEquals(manager.allEpics.size(), 2, "Неверное количество Epics сохранилось при вызове " +
                "метода getAllTasks()");
        assertEquals(manager.allSubTasks.size(), 2, "Неверное количество SubTasks сохранилось " +
                "при вызове метода getAllTasks()");

    }

}