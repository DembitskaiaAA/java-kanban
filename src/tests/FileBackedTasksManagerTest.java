package tests;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.Status;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    File file;

    @BeforeEach
    public void beforeEach() {
        file = new File("test.CSV");
        manager = new FileBackedTasksManager(file);
        manager.deleteAllTasks();
    }

    @Test
    void saveAndReadTasksNormalConditionsTest() {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.IN_PROGRESS, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));

        manager.create(task1);
        manager.create(epic1);
        manager.create(subTask1);
        manager.getTaskById(1);
        manager.getTaskById(3);

        FileBackedTasksManager loader = FileBackedTasksManager.loadFromFile(file);

        assertEquals(task1, loader.getTasks().get(task1.id), "Записанная и считанная информация " +
                "по 1 Task отличается");
        assertEquals(epic1, loader.getEpics().get(epic1.id), "Записанная и считанная информация " +
                "по 2 Epic отличается");
        assertEquals(subTask1, loader.getSubTasks().get(subTask1.id), "Записанная и считанная информация " +
                "по 3 SubTask отличается");
        List<Task> history = manager.getHistory();
        assertEquals(history.get(0), task1, "task возвращаемый из истории из файла не соответствует исходной");
        assertEquals(history.get(1), subTask1, "subTask возвращаемый из истории из файла " +
                "не соответствует исходной");
    }

    @Test
    void saveAndReadEmptyListTest() {
        manager.save();
        String line = null;
        try (Reader rd = new FileReader(file); BufferedReader bf = new BufferedReader(rd)) {
            while (bf.ready()) {
                line = bf.readLine();
                break;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileBackedTasksManager loader = FileBackedTasksManager.loadFromFile(file);
        assertEquals(loader.getTasks().size(), 0, "Список Tasks не пуст");
        assertEquals(loader.getEpics().size(), 0, "Список Epics не пуст");
        assertEquals(loader.getSubTasks().size(), 0, "Список subTasks не пуст");

        assertEquals("id,type,name,status,description,epic,startTime,duration,endTime", line,
                "Записанная и считанная информация отличается");
    }

    @Test
    void saveAndReadEpicsWithoutSubTasksTest() {
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        Task epic2 = new Epic("Квартира", "Продать квартиру", manager.countId(), null,
                null, null);

        manager.create(epic1);
        manager.create(epic2);

        FileBackedTasksManager loader = FileBackedTasksManager.loadFromFile(file);

        assertEquals(epic1, loader.getEpics().get(epic1.id), "Записанная и считанная информация " +
                "по 1 Epic отличается");
        assertEquals(epic2, loader.getEpics().get(epic2.id), "Записанная и считанная информация " +
                "по 2 Epic отличается");

    }

    @Test
    void emptyHistoryListTest() {
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);

        manager.create(epic1);
        Task myCheck = manager.getTaskById(1);
        assertEquals(epic1, myCheck, "Задачи отличаются");

        manager.deleteById(1);
        List<Task> history = manager.getHistory();
        assertEquals(history.size(), 0, "Размер списка истории не = 0");

    }
}