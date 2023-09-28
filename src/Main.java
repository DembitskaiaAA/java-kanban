import com.google.gson.Gson;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Manager;
import server.HttpTaskManager;
import server.HttpTaskServer;
import server.KVServer;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        HttpTaskManager manager;
        KVServer kvServer;
        HttpTaskServer server;
        Gson gson;
        try {
            gson = Manager.getGson();
            kvServer = new KVServer();
            kvServer.start();
            manager = (HttpTaskManager) Manager.getDefault();
            server = new HttpTaskServer(manager);

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

            System.out.println(manager.getTasks());
            System.out.println(manager.getEpics());
            System.out.println(manager.getSubTasks());

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

            //Очищаю хэш-мапы и историю
            InMemoryTaskManager.tasks.clear();
            InMemoryTaskManager.epics.clear();
            InMemoryTaskManager.subTasks.clear();
            Manager.getDefaultHistory().removeAll();
            server.stop();
            //Проверяю, что задачи и история удалились
            System.out.println(manager.getTasks());
            System.out.println(manager.getEpics());
            System.out.println(manager.getSubTasks());
            System.out.println(manager.getHistory());
            //Загружаю информацию с сервера
            manager.loadFromServer();
            System.out.println(manager.getTasks());
            System.out.println(manager.getEpics());
            System.out.println(manager.getSubTasks());
            System.out.println(manager.getHistory());
            kvServer.stop();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
