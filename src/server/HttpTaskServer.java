package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;
import manager.Manager;
import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpTaskManager manager;
    private static final Charset DEFAULT_CHARSET = UTF_8;
    private static Gson gson;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException, InterruptedException {
        HttpTaskServer.manager = (HttpTaskManager) manager;
        gson = Manager.getGson();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new HttpTaskServer(Manager.getDefault());
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String uri = exchange.getRequestURI().toString();
                String id = "";
                if (uri.contains("=")) {
                    id = uri.substring(uri.indexOf("=") + 1);
                }
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();

                switch (method) {
                    case ("GET"): {
                        if (Pattern.matches("^/tasks/task$", path) && (id.equals(""))) {
                            manager.getAllTasks();
                            String responseTasks = gson.toJson(InMemoryTaskManager.allTasks);
                            sendText(exchange, responseTasks);
                            return;
                        }
                        if (Pattern.matches("^/tasks/epic$", path)) {
                            manager.getAllTasks();
                            String responseEpics = gson.toJson(InMemoryTaskManager.allEpics);
                            sendText(exchange, responseEpics);
                            return;
                        }
                        if (Pattern.matches("^/tasks/subtask$", path)) {
                            manager.getAllTasks();
                            String responseSubtasks = gson.toJson(InMemoryTaskManager.allSubTasks);
                            sendText(exchange, responseSubtasks);
                            return;
                        }
                        if (Pattern.matches("^/tasks/task$", path) && !(id.equals(""))) {
                            int num = getId(id);
                            if (num != -1) {
                                String response = gson.toJson(manager.TaskById(num));
                                sendText(exchange, response);
                            } else {
                                System.out.println("Передан некорректный id");
                                exchange.sendResponseHeaders(405, 0);
                            }
                        }
                        if (Pattern.matches("^/tasks/subtask/epic$", path) && !(id.equals(""))) {
                            int num = getId(id);
                            if (num != -1) {
                                String response = gson.toJson(manager.getEpicSubTasks(num));
                                sendText(exchange, response);
                            } else {
                                System.out.println("Передан некорректный id epica");
                                exchange.sendResponseHeaders(405, 0);
                            }
                        }
                        if (Pattern.matches("^/tasks/history$", path)) {
                            String response = gson.toJson(manager.getHistory());
                            sendText(exchange, response);
                        }
                        if (Pattern.matches("^/tasks$", path)) {
                            String response = gson.toJson(manager.getPrioritizedTasks());
                            sendText(exchange, response);
                        }
                        break;
                    }
                    case ("POST"): {
                        if (Pattern.matches("^/tasks/task$", path)) {
                            String body = readText(exchange);
                            try {
                                Task task = gson.fromJson(body, Task.class);
                                manager.create(task);
                                String response = gson.toJson(task);
                                sendText(exchange, response);
                                System.out.println("Создали задачу: " + response);
                            } catch (JsonSyntaxException e) {
                                exchange.sendResponseHeaders(405, 0);
                            }
                        }
                        if (Pattern.matches("^/tasks/epic$", path)) {
                            String body = readText(exchange);
                            try {
                                Epic epic = gson.fromJson(body, Epic.class);
                                manager.create(epic);
                                String response = gson.toJson(epic);
                                sendText(exchange, response);
                                System.out.println("Создали задачу: " + response);
                            } catch (JsonSyntaxException e) {
                                exchange.sendResponseHeaders(405, 0);
                            }
                        }
                        if (Pattern.matches("^/tasks/subtask$", path)) {
                            String body = readText(exchange);
                            try {
                                SubTask subtask = gson.fromJson(body, SubTask.class);
                                manager.create(subtask);
                                String response = gson.toJson(subtask);
                                sendText(exchange, response);
                                System.out.println("Создали задачу: " + response);
                            } catch (JsonSyntaxException e) {
                                exchange.sendResponseHeaders(405, 0);
                            }
                        }
                        break;
                    }
                    case ("DELETE"): {
                        if (Pattern.matches("^/tasks/task$", path) && (id.equals(""))) {
                            manager.deleteTasks();
                            System.out.println("Все задачи удалены");
                            exchange.sendResponseHeaders(200, 0);
                        }
                        if (Pattern.matches("^/tasks/task$", path) && !(id.equals(""))) {
                            int num = getId(id);
                            if (num != -1) {
                                manager.deleteByTaskId(num);
                                System.out.println("Задача с id " + id + " удалена");
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                System.out.println("При удалении передан некорректный id");
                                exchange.sendResponseHeaders(405, 0);
                            }
                        }
                        break;
                    }
                    default: {
                        System.out.println("Обработка данного метода не предусмотрена " + method);
                        exchange.sendResponseHeaders(405, 0);
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        public int getId(String num) {
            int id;
            try {
                id = Integer.parseInt(num);
            } catch (NumberFormatException e) {
                return id = -1;
            }
            return id;
        }
    }

    private void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
    }

    private String readText(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }
    public void stop() {
        System.out.println("Приостанавливаем работу сервера на порту " + PORT);
        httpServer.stop(0);
    }
}
