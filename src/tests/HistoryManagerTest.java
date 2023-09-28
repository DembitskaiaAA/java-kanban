package tests;

import manager.*;
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

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
            Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
            Duration.ofMinutes(30));
    Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
            null, null);
    Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
            Status.IN_PROGRESS, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
            Duration.ofMinutes(30));
    Task epic2 = new Epic("Квартира", "Продать квартиру", manager.countId(), null,
            null, null);
    Task subTask2 = new SubTask("Встретиться с риелтором", "Отдать ключи", manager.countId(),
            Status.NEW, epic2.id, null, null);

    @BeforeEach
    void clearHistory() {
        historyManager.removeAll();
    }


    @Test
    void addWithNormalConditions() {
        historyManager.add(task1); //id 1
        historyManager.add(epic1); //id 2
        historyManager.add(subTask1); //id 3
        historyManager.add(epic2); //id 4
        historyManager.add(subTask2); //id 5
        historyManager.add(task1); //id 1
        historyManager.add(subTask1); //id 3
        ArrayList<Integer> myCheck = new ArrayList<>(List.of(2, 4, 5, 1, 3));

        List<Task> history = historyManager.getHistory();
        ArrayList<Integer> numHistory = new ArrayList<>();
        for (Task id : history) {
            numHistory.add(id.id);
        }
        assertIterableEquals(numHistory, myCheck, "Коллекции с историями не совпали");
    }

    @Test
    void emptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertEquals(history.size(), 0, "Пустые коллекции с историями не совпали");
    }


    @Test
    void removeFirstElementTest() {
        historyManager.add(task1); //id 1
        historyManager.add(epic1); //id 2
        historyManager.add(subTask1); //id 3
        historyManager.add(epic2); //id 4
        historyManager.add(subTask2); //id 5
        historyManager.add(task1); //id 1
        historyManager.add(subTask1); //id 3
        ArrayList<Integer> myCheck = new ArrayList<>(List.of(4, 5, 1, 3));

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        ArrayList<Integer> numHistory = new ArrayList<>();
        for (Task id : history) {
            numHistory.add(id.id);
        }
        assertIterableEquals(numHistory, myCheck, "Коллекция с удаленным первым элементом не совпала");
    }

    @Test
    void removeMiddleElementTest() {
        historyManager.add(task1); //id 1
        historyManager.add(epic1); //id 2
        historyManager.add(subTask1); //id 3
        historyManager.add(epic2); //id 4
        historyManager.add(subTask2); //id 5
        historyManager.add(task1); //id 1
        historyManager.add(subTask1); //id 3
        ArrayList<Integer> myCheck = new ArrayList<>(List.of(2, 4, 1, 3));

        historyManager.remove(5);

        List<Task> history = historyManager.getHistory();
        ArrayList<Integer> numHistory = new ArrayList<>();
        for (Task id : history) {
            numHistory.add(id.id);
        }
        assertIterableEquals(numHistory, myCheck, "Коллекция с удаленным в середине элементом не совпала");
    }

    @Test
    void removeLastElementTest() {
        historyManager.add(task1); //id 1
        historyManager.add(epic1); //id 2
        historyManager.add(subTask1); //id 3
        historyManager.add(epic2); //id 4
        historyManager.add(subTask2); //id 5
        historyManager.add(task1); //id 1
        historyManager.add(subTask1); //id 3
        ArrayList<Integer> myCheck = new ArrayList<>(List.of(2, 4, 5, 1));

        historyManager.remove(3);

        List<Task> history = historyManager.getHistory();
        ArrayList<Integer> numHistory = new ArrayList<>();
        for (Task id : history) {
            numHistory.add(id.id);
        }
        assertIterableEquals(numHistory, myCheck, "Коллекция с удаленным последним элементом не совпала");
    }

    @Test
    void getHistoryWithDuplicatesTest() {
        historyManager.add(task1); //id 1
        historyManager.add(epic1); //id 2
        historyManager.add(subTask1); //id 3
        historyManager.add(epic2); //id 4
        historyManager.add(subTask2); //id 5
        historyManager.add(task1); //id 1
        historyManager.add(subTask1); //id 3
        historyManager.add(subTask2); //id 5
        ArrayList<Integer> myCheck = new ArrayList<>(List.of(2, 4, 1, 3, 5));


        List<Task> history = historyManager.getHistory();
        ArrayList<Integer> numHistory = new ArrayList<>();
        for (Task id : history) {
            numHistory.add(id.id);
        }
        assertIterableEquals(numHistory, myCheck, "Коллекция с удаленным последним элементом не совпала");
    }

    @Test
    void removeALlTest() {
        historyManager.add(task1); //id 1
        historyManager.add(epic1); //id 2
        historyManager.add(subTask1); //id 3
        historyManager.add(epic2); //id 4
        historyManager.add(subTask2); //id 5
        historyManager.add(task1); //id 1
        historyManager.add(subTask1); //id 3
        historyManager.add(subTask2); //id 5

        List<Task> history = historyManager.getHistory();
        historyManager.removeAll();

        assertEquals(history.size(), 0, "Размер коллекции со всеми удаленными элементами не = 0");
    }
}