package tests;

import server.HttpTaskManager;
import server.HttpTaskServer;
import server.KVServer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private Gson gson;
    private HttpTaskServer server;
    private KVServer kvServer;
    private HttpTaskManager manager;

    @BeforeEach
    void createServer() throws IOException {
        try {
            if (server == null) {
                kvServer = new KVServer();
                kvServer.start();
                manager = (HttpTaskManager) Manager.getDefault();
                server = new HttpTaskServer(manager);
                gson = Manager.getGson();
            }
            manager.deleteAllTasks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void closeServer() {
        server.stop();
        kvServer.stop();
    }

    @Test
    void getAllTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Task task2 = new Task("Дока2", "Выиграть игру", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 2, 10, 0),
                Duration.ofMinutes(30));
        String json = gson.toJson(task1);
        String json2 = gson.toJson(task2);
        //Создаем две задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(2, manager.getTasks().size(), "2 tasks не созданы");
        //Запрашиваем 2 задачи и сравниваем с исходными задачами
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response3.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Task taskDeserialized1 = gson.fromJson(jsonArray.get(0), Task.class);
        Task taskDeserialized2 = gson.fromJson(jsonArray.get(1), Task.class);
        System.out.println(taskDeserialized1);
        System.out.println(taskDeserialized2);
        assertEquals(task1, taskDeserialized1, "1 task не равняется исходной задаче");
        assertEquals(task2, taskDeserialized2, "2 task не равняется исходной задаче");
    }

    @Test
    void getAllEpicsTest() throws IOException, InterruptedException {
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), Status.NEW,
                null, null);
        Task epic2 = new Epic("Квартира", "Продать квартиру", manager.countId(), Status.NEW,
                null, null);

        String json = gson.toJson(epic1);
        String json2 = gson.toJson(epic2);
        //Создаем 2 задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(2, manager.getEpics().size(), "2 epics не созданы");

        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request3 = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response3.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Epic taskDeserialized1 = gson.fromJson(jsonArray.get(0), Epic.class);
        Epic taskDeserialized2 = gson.fromJson(jsonArray.get(1), Epic.class);
        System.out.println(taskDeserialized1);
        System.out.println(taskDeserialized2);
        assertEquals(epic1, taskDeserialized1, "1 epic не равняется исходной задаче");
        assertEquals(epic2, taskDeserialized2, "2 epic не равняется исходной задаче");
    }

    @Test
    void getAllSubTasksTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.NEW, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));

        String json = gson.toJson(epic1);
        String json2 = gson.toJson(subTask1);
        System.out.println(json2);

        HttpClient client = HttpClient.newHttpClient();
        //Создаю epic и subtask
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI uri = URI.create("http://localhost:8080/tasks/subtask");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).POST(body2).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(1, manager.getEpics().size(), "1 epic не создан");
        assertEquals(1, manager.getSubTasks().size(), "1 subtask не создан");

        //Сравниваю subtask, который получила с сервера, с исходной подзадачей
        URI urp = URI.create("http://localhost:8080/tasks/subtask/epic?id=1");
        HttpRequest request3 = HttpRequest.newBuilder().uri(urp).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        JsonElement jsonElement = JsonParser.parseString(response3.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        SubTask taskDeserialized = gson.fromJson(jsonArray.get(0), SubTask.class);
        System.out.println(taskDeserialized);

        assertEquals(subTask1, taskDeserialized, "subtask не равняется исходной задаче");
    }

    @Test
    void createAndDeleteTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Task task2 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 19, 0),
                Duration.ofMinutes(30));
        String json = gson.toJson(task1);
        String json2 = gson.toJson(task2);

        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

        URI url = URI.create("http://localhost:8080/tasks/task");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");

        HttpRequest request3 = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode(), "Не пришел код 200 от сервера при удалении 1 задачи");
        assertEquals(1, manager.getTasks().size(), "1 задача не удалена");
    }

    @Test
    void getTaskByIdTest() throws IOException, InterruptedException {
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(30));
        Task task2 = new Task("Дока2", "Выиграть игру", manager.countId(),
                Status.NEW, LocalDateTime.of(2023, 1, 2, 10, 0),
                Duration.ofMinutes(30));
        String json = gson.toJson(task1);
        String json2 = gson.toJson(task2);
        //Создаем две задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(2, manager.getTasks().size(), "2 tasks не созданы");
        ////Сравниваем исходную задачу с задачей, полученной по конкретному id
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request3 = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Task task3 = gson.fromJson(response3.body(), Task.class);
        System.out.println(task3);
        assertEquals(task1, task3, "Полученный task не равняется исходной задаче с id 1");
    }

    @Test
    void getEpicByIdTest() throws IOException, InterruptedException {
        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), Status.NEW,
                null, null);
        Task epic2 = new Epic("Квартира", "Продать квартиру", manager.countId(), Status.NEW,
                null, null);

        String json = gson.toJson(epic1);
        String json2 = gson.toJson(epic2);
        //Создаем 2 задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(2, manager.getEpics().size(), "2 epics не созданы");
        //Сравниваем исходную задачу с задачей, полученной по конкретному id
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request3 = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Epic task3 = gson.fromJson(response3.body(), Epic.class);
        System.out.println(task3);
        assertEquals(epic1, task3, "Полученный epic не равняется исходной задаче с id 1");
    }

    @Test
    void getSubTaskByIdTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null,
                null, null);
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.NEW, epic1.id, LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));

        String json = gson.toJson(epic1);
        String json2 = gson.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        //Создаю epic и subtask
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI uri = URI.create("http://localhost:8080/tasks/subtask");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).POST(body2).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(1, manager.getEpics().size(), "1 epic не создан");
        assertEquals(1, manager.getSubTasks().size(), "1 subtask не создан");

        ////Сравниваем исходную подзадачу с подзадачей, полученной по конкретному id
        URI uri2 = URI.create("http://localhost:8080/tasks/task?id=2");
        HttpRequest request3 = HttpRequest.newBuilder().uri(uri2).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        SubTask task3 = gson.fromJson(response3.body(), SubTask.class);
        System.out.println(task3);
        assertEquals(subTask1, task3, "Полученный subtask не равняется исходной задаче с id 2");
    }

    @Test
    void deleteAllTasksTest() throws IOException, InterruptedException {
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
        //Создаю task, epic и subtask
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

        assertEquals(1, manager.getTasks().size(), "1 task не создан");
        assertEquals(1, manager.getEpics().size(), "1 epic не создан");
        assertEquals(1, manager.getSubTasks().size(), "1 subtask не создан");

        //Удаляю все задачи и проверяю, что задачи удалились
        URI urlDel = URI.create("http://localhost:8080/tasks/task");
        HttpRequest requestDel = HttpRequest.newBuilder().uri(urlDel).DELETE().build();
        HttpResponse<String> responseDel = client.send(requestDel, HttpResponse.BodyHandlers.ofString());
        assertEquals(0, manager.getTasks().size(), "task не удален");
        assertEquals(0, manager.getEpics().size(), "epic не удален");
        assertEquals(0, manager.getSubTasks().size(), "subtask не удален");

    }
}
