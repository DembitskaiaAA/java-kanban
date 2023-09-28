package tests;

import server.HttpTaskManager;
import server.HttpTaskServer;
import server.KVServer;
import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.Manager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kvServer;
    HttpTaskServer server;
    Gson gson;

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer(Manager.getDefault());
        manager = (HttpTaskManager) Manager.getDefault();
        manager.deleteAllTasks();
        gson = Manager.getGson();
    }

    @AfterEach
    public void close() {
        kvServer.stop();
        server.stop();
    }

    @Test
    void saveToServerAndLoadFromServerTest() throws IOException, InterruptedException {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.NEW, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));

        String json = gson.toJson(task1);
        String json1 = gson.toJson(epic1);
        String json2 = gson.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        //Создаю задачи
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url1 = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        //Очищаю хэш-мапы и загружаю информацию в них, используя данные с сервера
        InMemoryTaskManager.tasks.clear();
        InMemoryTaskManager.epics.clear();
        InMemoryTaskManager.subTasks.clear();

        manager.loadFromServer();

        assertEquals(manager.getTasks().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getSubTasks().size(), 1, "Список Tasks пуст");
        //Эпик не сравниваю, т.к. у него меняются поля при создании у него подзадачи
        assertEquals(task1, manager.getTasks().get(1), "Tasks не совпадет с исходной задачей");
        assertEquals(subTask1, manager.getSubTasks().get(3), "Subtask не совпадет с исходной задачей");
    }

    @Test
    void CheckHistoryTest() throws IOException, InterruptedException {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.NEW, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        Task subTask1_1 = new SubTask("Гости", "Позвать гостей", manager.countId(), Status.NEW,
                epic1.id, LocalDateTime.of(2023, 1, 1, 11, 00),
                Duration.ofMinutes(30));

        String json = gson.toJson(task1);
        String json1 = gson.toJson(epic1);
        String json2 = gson.toJson(subTask1);
        String json3 = gson.toJson(subTask1_1);

        HttpClient client = HttpClient.newHttpClient();
        //Создаю задачи
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url1 = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(json3);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        //Получаю задачи используя id для создания истории
        URI urlHistory3 = URI.create("http://localhost:8080/tasks/task?id=3");
        URI urlHistory1 = URI.create("http://localhost:8080/tasks/task?id=1");
        URI urlHistory2 = URI.create("http://localhost:8080/tasks/task?id=2");
        URI urlHistory4 = URI.create("http://localhost:8080/tasks/task?id=4");

        HttpRequest requestHistory = HttpRequest.newBuilder().uri(urlHistory3).GET().build();
        HttpRequest requestHistory1 = HttpRequest.newBuilder().uri(urlHistory1).GET().build();
        HttpRequest requestHistory2 = HttpRequest.newBuilder().uri(urlHistory2).GET().build();
        HttpRequest requestHistory3 = HttpRequest.newBuilder().uri(urlHistory4).GET().build();

        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseHistory1 = client.send(requestHistory1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseHistory2 = client.send(requestHistory2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseHistory3 = client.send(requestHistory3, HttpResponse.BodyHandlers.ofString());
        //Очищаю хэш-мапы и загружаю информацию с сервера
        InMemoryTaskManager.tasks.clear();
        InMemoryTaskManager.epics.clear();
        InMemoryTaskManager.subTasks.clear();

        manager.loadFromServer();

        List<Integer> history = new ArrayList<>();
        history.add(subTask1.id);
        history.add(task1.id);
        history.add(epic1.id);
        history.add(subTask1_1.id);
        //Проверяю загруженную историю по id задач. Если проверять непосредственно задачи, то у epic меняется значение
        //в subTasksInEpic(при создании добавляются подзадачи в данную переменную) и тест показывает,
        //что списки с историями не совпадают
        assertEquals(history, manager.getHistory().stream().map(task -> task.id).collect(Collectors.toList()),
                "Списки историй не совпадают");
    }

    @Test
    void CheckDeleteAllTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.NEW, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        Task subTask1_1 = new SubTask("Гости", "Позвать гостей", manager.countId(), Status.NEW,
                epic1.id, LocalDateTime.of(2023, 1, 1, 11, 00),
                Duration.ofMinutes(30));

        String json = gson.toJson(task1);
        String json1 = gson.toJson(epic1);
        String json2 = gson.toJson(subTask1);
        String json3 = gson.toJson(subTask1_1);

        HttpClient client = HttpClient.newHttpClient();
        //Создаю задачи
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url1 = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(json3);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(manager.getTasks().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Epics пуст");
        assertEquals(manager.getSubTasks().size(), 2, "Список Subtasks пуст");
        //Очищаю хэш-мапы и загружаю информацию с сервера
        InMemoryTaskManager.tasks.clear();
        InMemoryTaskManager.epics.clear();
        InMemoryTaskManager.subTasks.clear();
        manager.loadFromServer();
        assertEquals(manager.getTasks().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Epics пуст");
        assertEquals(manager.getSubTasks().size(), 2, "Список Subtasks пуст");
        //Удаляю все задачи и проверяю, что с сервера они также удалились
        URI urlDeleteTasks = URI.create("http://localhost:8080/tasks/task");
        HttpRequest requestDeleteTasks = HttpRequest.newBuilder().uri(urlDeleteTasks).DELETE().build();
        HttpResponse<String> responseDeleteTasks = client.send(requestDeleteTasks, HttpResponse.BodyHandlers.ofString());
        manager.loadFromServer();
        assertEquals(manager.getTasks().size(), 0, "Список Tasks не пуст");
        assertEquals(manager.getEpics().size(), 0, "Список Epics не пуст");
        assertEquals(manager.getSubTasks().size(), 0, "Список Subtasks не пуст");
        assertEquals(manager.getHistory().size(), 0, "Список истории не пуст");
    }

    @Test
    void CheckDeleteOneTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.NEW, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        Task subTask1_1 = new SubTask("Гости", "Позвать гостей", manager.countId(), Status.NEW,
                epic1.id, LocalDateTime.of(2023, 1, 1, 11, 00),
                Duration.ofMinutes(30));

        String json = gson.toJson(task1);
        String json1 = gson.toJson(epic1);
        String json2 = gson.toJson(subTask1);
        String json3 = gson.toJson(subTask1_1);

        HttpClient client = HttpClient.newHttpClient();
        //Создаю задачи
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url1 = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(json3);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(manager.getTasks().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Epics пуст");
        assertEquals(manager.getSubTasks().size(), 2, "Список Subtasks пуст");
        //Очищаю хэш-мапы и загружаю информацию с сервера
        InMemoryTaskManager.tasks.clear();
        InMemoryTaskManager.epics.clear();
        InMemoryTaskManager.subTasks.clear();
        manager.loadFromServer();
        assertEquals(manager.getTasks().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Epics пуст");
        assertEquals(manager.getSubTasks().size(), 2, "Список Subtasks пуст");
        //Удаляю задачу и проверяю, что с сервера она удалилась
        URI urlDeleteTask = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest requestDeleteTask = HttpRequest.newBuilder().uri(urlDeleteTask).DELETE().build();
        HttpResponse<String> responseDeleteTask = client.send(requestDeleteTask, HttpResponse.BodyHandlers.ofString());
        manager.loadFromServer();
        assertEquals(manager.getTasks().size(), 0, "Список Tasks не пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Epics пуст");
        assertEquals(manager.getSubTasks().size(), 2, "Список Subtasks пуст");
        assertEquals(manager.getHistory().size(), 0, "Список истории не пуст");
    }

    @Test
    void CheckDeleteEpicTest() throws IOException, InterruptedException {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.NEW, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        Task subTask1_1 = new SubTask("Гости", "Позвать гостей", manager.countId(), Status.NEW,
                epic1.id, LocalDateTime.of(2023, 1, 1, 11, 00),
                Duration.ofMinutes(30));

        String json = gson.toJson(task1);
        String json1 = gson.toJson(epic1);
        String json2 = gson.toJson(subTask1);
        String json3 = gson.toJson(subTask1_1);


        HttpClient client = HttpClient.newHttpClient();
        //Создаю задачи
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url1 = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(json3);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(manager.getTasks().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Epics пуст");
        assertEquals(manager.getSubTasks().size(), 2, "Список Subtasks пуст");
        //Очищаю хэш-мапы и загружаю информацию с сервера
        InMemoryTaskManager.tasks.clear();
        InMemoryTaskManager.epics.clear();
        InMemoryTaskManager.subTasks.clear();
        manager.loadFromServer();
        assertEquals(manager.getTasks().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Epics пуст");
        assertEquals(manager.getSubTasks().size(), 2, "Список Subtasks пуст");
        //Удаляю epic и проверяю, что с сервера удалились epic и его subtasks
        URI urlDeleteTask = URI.create("http://localhost:8080/tasks/task?id=2");
        HttpRequest requestDeleteTask = HttpRequest.newBuilder().uri(urlDeleteTask).DELETE().build();
        HttpResponse<String> responseDeleteTask = client.send(requestDeleteTask, HttpResponse.BodyHandlers.ofString());
        manager.loadFromServer();
        assertEquals(manager.getTasks().size(), 1, "Список Tasks не пуст");
        assertEquals(manager.getEpics().size(), 0, "Список Epics не пуст");
        assertEquals(manager.getSubTasks().size(), 0, "Список Subtasks не пуст");
        assertEquals(manager.getHistory().size(), 0, "Список истории не пуст");
    }

    @Test
    void CheckDeleteSubTaskAndGetHistoryTest() throws IOException, InterruptedException {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.NEW, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        Task subTask1_1 = new SubTask("Гости", "Позвать гостей", manager.countId(), Status.NEW,
                epic1.id, LocalDateTime.of(2023, 1, 1, 11, 00),
                Duration.ofMinutes(30));

        String json = gson.toJson(task1);
        String json1 = gson.toJson(epic1);
        String json2 = gson.toJson(subTask1);
        String json3 = gson.toJson(subTask1_1);

        HttpClient client = HttpClient.newHttpClient();
        //Создаю задачи
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url1 = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(json3);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(manager.getTasks().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Epics пуст");
        assertEquals(manager.getSubTasks().size(), 2, "Список Subtasks пуст");
        //Очищаю хэш-мапы и загружаю информацию с сервера
        InMemoryTaskManager.tasks.clear();
        InMemoryTaskManager.epics.clear();
        InMemoryTaskManager.subTasks.clear();
        manager.loadFromServer();
        assertEquals(manager.getTasks().size(), 1, "Список Tasks пуст");
        assertEquals(manager.getEpics().size(), 1, "Список Epics пуст");
        assertEquals(manager.getSubTasks().size(), 2, "Список Subtasks пуст");

        //Получаю задачи используя id для создания истории
        URI urlHistory3 = URI.create("http://localhost:8080/tasks/task?id=3");
        URI urlHistory1 = URI.create("http://localhost:8080/tasks/task?id=1");
        URI urlHistory2 = URI.create("http://localhost:8080/tasks/task?id=2");
        URI urlHistory4 = URI.create("http://localhost:8080/tasks/task?id=4");

        HttpRequest requestHistory = HttpRequest.newBuilder().uri(urlHistory3).GET().build();
        HttpRequest requestHistory1 = HttpRequest.newBuilder().uri(urlHistory1).GET().build();
        HttpRequest requestHistory2 = HttpRequest.newBuilder().uri(urlHistory2).GET().build();
        HttpRequest requestHistory3 = HttpRequest.newBuilder().uri(urlHistory4).GET().build();

        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseHistory1 = client.send(requestHistory1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseHistory2 = client.send(requestHistory2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseHistory3 = client.send(requestHistory3, HttpResponse.BodyHandlers.ofString());

        //Удаляю subtask и проверяю, что с сервера он также удалился
        URI urlDeleteTask = URI.create("http://localhost:8080/tasks/task?id=3");
        HttpRequest requestDeleteTask = HttpRequest.newBuilder().uri(urlDeleteTask).DELETE().build();
        HttpResponse<String> responseDeleteTask = client.send(requestDeleteTask, HttpResponse.BodyHandlers.ofString());

        //Очищаю хэш-мапы и загружаю информацию с сервера
        InMemoryTaskManager.tasks.clear();
        InMemoryTaskManager.epics.clear();
        InMemoryTaskManager.subTasks.clear();

        manager.loadFromServer();

        List<Integer> history = new ArrayList<>();
        history.add(task1.id);
        history.add(epic1.id);
        history.add(subTask1_1.id);
        //Проверяю загруженную историю по id задач. Если проверять непосредственно задачи, то у epic меняется значение
        //в subTasksInEpic(при создании добавляются подзадачи в данную переменную) и тест показывает,
        //что списки с историями не совпадают
        assertEquals(history, manager.getHistory().stream().map(task -> task.id).collect(Collectors.toList()),
                "Список Tasks пуст");
    }
}