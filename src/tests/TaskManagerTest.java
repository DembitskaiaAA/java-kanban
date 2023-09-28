package tests;

import exceptions.CrossedTimeException;
import exceptions.NegativeDurationException;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    public T manager;

    @Test
    void createNullTasksTest() {
        Task task1 = null;
        assertThrows(NullPointerException.class, () -> {
                    manager.create(task1);
                },
                "Null задача создалась. Ошибка: такую задачу нельзя создать");
    }

    @Test
    void createTaskNormalConditionsTest() {
        int id = manager.countId();
        Task task1 = new Task("Практикум", "Решить задачу", id,
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        manager.create(task1);
        Task savedTask = manager.getTaskById(id);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final Map<Integer, Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(id), "Задачи не совпадают.");
    }

    @Test
    void createWithNullTaskIdTest() {
        Integer id = null;
        assertThrows(NullPointerException.class, () -> {
                    new Task("Практикум", "Решить задачу", id,
                            Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                            Duration.ofMinutes(30));
                },
                "Задача c id = null создалась. Ошибка: такую задачу нельзя создать");
    }

    @Test
    void createEpicWithoutSubTasksTest() {
        int id = manager.countId();
        Task epic1 = new Epic("Практикум", "Решить задачу", id,
                null, null, null);
        manager.create(epic1);
        Task savedEpic = manager.getTaskById(id);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        final Map<Integer, Epic> epic = manager.getEpics();

        assertNotNull(epic1, "Задачи на возвращаются.");
        assertEquals(1, epic.size(), "Неверное количество задач.");
        assertEquals(epic1, epic.get(id), "Задачи не совпадают.");
        assertEquals(epic1.status, Status.NEW, "При отсутствии подзадач статус не равняется New");
    }

    @Test
    void createWithNullEpicIdTest() {
        Integer id = null;
        assertThrows(NullPointerException.class, () -> {
                    new Epic("Практикум", "Решить задачу", id,
                            null, null, null);
                    ;
                },
                "Задача c id = null создалась. Ошибка: такую задачу нельзя создать");
    }

    @Test
    void createEpicWithSubTasksStatusInProgressTest() {
        Task epic1 = new Epic("Практикум", "Решить задачу", manager.countId(), null, null,
                null);
        Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.IN_PROGRESS, epic1.id,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));
        manager.create(epic1);
        manager.create(subTask1);
        Task savedEpic = manager.getTaskById(1);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        final Map<Integer, Epic> epic = manager.getEpics();

        assertNotNull(epic1, "Задачи на возвращаются.");
        assertEquals(1, epic.size(), "Неверное количество задач.");
        assertEquals(epic1, epic.get(epic1.id), "Задачи не совпадают.");
        assertEquals(epic1.status, Status.IN_PROGRESS, "При отсутствии подзадач статус не равняется " +
                "IN_PROGRESS");
    }

    @Test
    void createEpicWithSubTasksStatusDoneTest() {
        Task epic1 = new Epic("Практикум", "Решить задачу", manager.countId(), null, null,
                null);
        Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.DONE, epic1.id,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));
        manager.create(epic1);
        manager.create(subTask1);
        Task savedEpic = manager.getTaskById(1);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        final Map<Integer, Epic> epic = manager.getEpics();

        assertNotNull(epic1, "Задачи на возвращаются.");
        assertEquals(1, epic.size(), "Неверное количество задач.");
        assertEquals(epic1, epic.get(epic1.id), "Задачи не совпадают.");
        assertEquals(epic1.status, Status.DONE, "При отсутствии подзадач статус не равняется DONE");
    }

    @Test
    void createSubTaskWithoutEpicTest() {
        assertThrows(NullPointerException.class, () -> {
                    new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                            Status.DONE, null,
                            LocalDateTime.of(2023, 1, 1, 10, 0),
                            Duration.ofMinutes(30));
                },
                "Создание subTask с epicsId - null работает неправильно. Такую подзадачу нельзя создать");
    }

    @Test
    void createWithNullSubTaskIdTest() {
        Integer id = null;
        assertThrows(NullPointerException.class, () -> {
                    new SubTask("Ресторан", "Посмотреть рестораны рядом", id,
                            Status.DONE, 1,
                            LocalDateTime.of(2023, 1, 1, 10, 0),
                            Duration.ofMinutes(30));
                },
                "Задача c id = null создалась. Ошибка: такую задачу нельзя создать");
    }

    @Test
    void createWithNegativeDurationTest() {
        assertThrows(NegativeDurationException.class, () -> {
                    manager.create(new Task("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                            Status.DONE, LocalDateTime.of(2023, 1, 1, 10, 0),
                            Duration.ofMinutes(-30)));
                },
                "Задача c duration < 0 создалась. Ошибка: такую задачу нельзя создать");
    }

    @Test
    void createWithCrossedTimeTest() {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        manager.create(task1);
        assertThrows(CrossedTimeException.class, () -> {
                    manager.create(new Task("День рождение", "Организовать др", manager.countId(),
                            Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 20),
                            Duration.ofMinutes(30)));
                },
                "Задача с пересекающемся временем создалась. Ошибка: такую задачу нельзя создать");
    }

    @Test
    void deleteAllTasksTest() {
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
        manager.deleteAllTasks();
        assertEquals(0, manager.getTasks().size(), "Tasks не удалены");
        assertEquals(0, manager.getEpics().size(), "Epics не удалены");
        assertEquals(0, manager.getSubTasks().size(), "SubTasks не удалены");
    }

    @Test
    void getTaskByIdNormalConditionsTest() {
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

        Task taskTest = manager.getTaskById(1);
        Task epicTest = manager.getTaskById(2);
        Task subTaskTest = manager.getTaskById(3);
        assertEquals(task1, taskTest, "Tasks не совпадают.");
        assertEquals(epic1, epicTest, "Epics не совпадают.");
        assertEquals(subTask1, subTaskTest, "Subtasks не совпадают.");
    }

    @Test
    void getTaskByWrongIdTest() {
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

        assertThrows(NullPointerException.class, () -> {
                    manager.getTaskById(-1);
                },
                "Задача c неверным id = -1 нашлась");
        assertThrows(NullPointerException.class, () -> {
                    manager.getTaskById(-2);
                },
                "Задача c неверным id = -2 нашлась");
        assertThrows(NullPointerException.class, () -> {
                    manager.getTaskById(-3);
                },
                "Задача c неверным id = -3 нашлась");
    }

    @Test
    void getHistoryNormalConditionsTest() {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.IN_PROGRESS, epic1.id, null, null);

        manager.create(task1);
        manager.create(epic1);
        manager.create(subTask1);

        manager.getTaskById(2);//id 2
        manager.getTaskById(1);//id 1
        manager.getTaskById(3);//id 3

        List<Task> history = manager.getHistory();
        assertEquals(epic1, history.get(0), "Epic загруженный из истории не соответствует исходному " +
                "или неправильный порядок вывода истории");
        assertEquals(task1, history.get(1), "Task загруженный из истории не соответствует исходному " +
                "или неправильный порядок вывода истории");
        assertEquals(subTask1, history.get(2), "SubTask загруженный из истории не соответствует исходному " +
                "или неправильный порядок вывода истории");
    }

    @Test
    void updateTasksWithNormalConditionsTest() {
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

        Task task1_1 = new Task("Тест Task", "Тест Task", 1,
                Status.IN_PROGRESS, LocalDateTime.of(2222, 1, 1, 9, 0),
                Duration.ofMinutes(40));
        Task epic1_1 = new Epic("Тест Epic", "Тест Epic", 2, null,
                null, null);
        Task subTask1_1 = new SubTask("Тест SubTask", "Тест SubTask", 3,
                Status.DONE, epic1_1.id, LocalDateTime.of(3333, 1, 1, 10, 0),
                Duration.ofMinutes(60));

        manager.update(task1_1);
        manager.update(epic1_1);
        manager.update(subTask1_1);

        assertEquals(1, manager.getTasks().size(), "Неверное количество задач в tasks");
        assertEquals(task1_1.name, manager.getTaskById(1).name, "Название в task не изменилось");
        assertEquals(task1_1.description, manager.getTaskById(1).description, "Описание в task не изменилось");
        assertEquals(task1_1.status, manager.getTaskById(1).status, "Статус в task не изменился");
        assertEquals(task1_1.startTime, manager.getTaskById(1).startTime, "Время старта задачи в task " +
                "не изменилось");
        assertEquals(task1_1.duration, manager.getTaskById(1).duration, "Длительность выполнения " +
                "задачи в task не изменилось");
        assertEquals(task1_1.endTime, manager.getTaskById(1).endTime, "Время завершения задачи не изменилось");

        assertEquals(1, manager.getEpics().size(), "Неверное количество задач в epics");
        assertEquals(epic1_1.name, manager.getTaskById(2).name, "Название в epic не изменилось");
        assertEquals(epic1_1.description, manager.getTaskById(2).description, "Описание в epic не изменилось");
        assertEquals(epic1_1.status, manager.getTaskById(2).status, "Статус в epic не изменился");
        assertEquals(epic1_1.startTime, manager.getTaskById(2).startTime, "Время старта задачи в epic" +
                " не изменилось");
        assertEquals(epic1_1.duration, manager.getTaskById(2).duration, "Длительность выполнения задачи " +
                "в epic не изменилось");
        assertEquals(epic1_1.endTime, manager.getTaskById(2).endTime, "Время завершения задачи в epic" +
                " не изменилось");

        assertEquals(1, manager.getSubTasks().size(), "Неверное количество задач в subTasks");
        assertEquals(subTask1_1.name, manager.getTaskById(3).name, "Название в subTask не изменилось");
        assertEquals(subTask1_1.description, manager.getTaskById(3).description, "Описание в subTask " +
                "не изменилось");
        assertEquals(subTask1_1.status, manager.getTaskById(3).status, "Статус в subTask не изменился");
        assertEquals(subTask1_1.startTime, manager.getTaskById(3).startTime, "Время старта задачи в subTask " +
                "не изменилось");
        assertEquals(subTask1_1.duration, manager.getTaskById(3).duration, "Длительность выполнения задачи " +
                "в subTask не изменилось");
        assertEquals(subTask1_1.endTime, manager.getTaskById(3).endTime, "Время завершения задачи в subTask" +
                " не изменилось");
    }

    @Test
    void updateTasksWithWrongIdTest() {
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
        assertEquals(1, manager.getTasks().size(), "Tasks не созданы");
        assertEquals(1, manager.getEpics().size(), "Epics не созданы");
        assertEquals(1, manager.getSubTasks().size(), "SubTasks не созданы");

        Task task1_1 = new Task("Тест Task", "Тест Task", -1,
                Status.IN_PROGRESS, LocalDateTime.of(2222, 1, 1, 9, 0),
                Duration.ofMinutes(40));
        Task epic1_1 = new Epic("Тест Epic", "Тест Epic", -2, null,
                null, null);
        Task subTask1_1 = new SubTask("Тест SubTask", "Тест SubTask", -3,
                Status.DONE, epic1_1.id, LocalDateTime.of(3333, 1, 1, 10, 0),
                Duration.ofMinutes(60));

        assertThrows(NullPointerException.class, () -> {
                    manager.update(task1_1);
                },
                "Задача c неверным id = -1 обновилась");
        assertThrows(NullPointerException.class, () -> {
                    manager.update(epic1_1);
                },
                "Задача c неверным id = -2 обновилась");
        assertThrows(NullPointerException.class, () -> {
                    manager.update(subTask1_1);
                },
                "Задача c неверным id = -3 обновилась");
    }

    @Test
    void deleteByIdNormalConditions() {
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
        assertEquals(1, manager.getTasks().size(), "Tasks не созданы");
        assertEquals(1, manager.getEpics().size(), "Epics не созданы");
        assertEquals(1, manager.getSubTasks().size(), "SubTasks не созданы");


        manager.deleteById(1);
        manager.deleteById(2);
        manager.deleteById(3);
        assertEquals(0, manager.getTasks().size(), "Tasks не удалены");
        assertEquals(0, manager.getEpics().size(), "Epics не удалены");
        assertEquals(0, manager.getSubTasks().size(), "SubTasks не удалены");
    }

    @Test
    void deleteByWrongId() {
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
        assertEquals(1, manager.getTasks().size(), "Tasks не созданы");
        assertEquals(1, manager.getEpics().size(), "Epics не созданы");
        assertEquals(1, manager.getSubTasks().size(), "SubTasks не созданы");

        manager.deleteById(-1);
        manager.deleteById(-2);
        manager.deleteById(-3);
        assertEquals(1, manager.getTasks().size(), "Tasks удалены");
        assertEquals(1, manager.getEpics().size(), "Epics удалены");
        assertEquals(1, manager.getSubTasks().size(), "SubTasks удалены");
    }

    @Test
    void countTest() {
        assertEquals(1, manager.countId(), "Идентификатор не увеличился на 1");
        assertEquals(2, manager.countId(), "Идентификатор не увеличился на 2");
    }

    @Test
    void getTasksTest() {
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
        assertEquals(1, manager.getTasks().size(), "Map tasks не удалось получить");
        assertEquals(1, manager.getEpics().size(), "Map epics не удалось получить");
        assertEquals(1, manager.getSubTasks().size(), "Map subTasks не удалось получить");
    }

    @Test
    public void haveSubTasksEpicsTest() {
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

        assertEquals(epic1.id, manager.getSubTasks().get(subTask1.id).epicsId, "EpicsId у созданной " +
                "задачи subTask1 отличается");
        assertEquals(epic2.id, manager.getSubTasks().get(subTask2.id).epicsId, "EpicsId у созданной " +
                "задачи subTask2 отличается");
    }
}