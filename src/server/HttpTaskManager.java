package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.FileBackedTasksManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Manager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final String url;
    private final String Tasks = "Tasks";
    private final String Epics = "Epics";
    private final String Subtasks = "Subtasks";
    private final String History = "History";
    private final Gson gson = Manager.getGson();

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        this.url = url;
        client = new KVTaskClient(url);
    }

    void create(Task task) {
        super.create(task);
        saveToServer();
    }
    Task TaskById(int id) {
        Task task = super.getTaskById(id);
        saveToServer();
        return task;
    }

    void deleteTasks() {
        super.deleteAllTasks();
        saveToServer();
    }

    void deleteByTaskId(int id) {
        super.deleteById(id);
        saveToServer();
    }

    public void saveToServer() {
        List<Task> tasksForSave = new ArrayList<>();
        getTasks().values().stream().collect(Collectors.toCollection(() -> tasksForSave));
        String jsonTasks = gson.toJson(tasksForSave);

        List<Epic> epicsForSave = new ArrayList<>();
        getEpics().values().stream().collect(Collectors.toCollection(() -> epicsForSave));
        String jsonEpics = gson.toJson(epicsForSave);

        List<SubTask> subtasksForSave = new ArrayList<>();
        getSubTasks().values().stream().collect(Collectors.toCollection(() -> subtasksForSave));
        String jsonSubtasks = gson.toJson(subtasksForSave);

        List<Task> history = new ArrayList<>((getHistory()));
        String jsonHistory = gson.toJson(history);
        try {
            client.put(Tasks, jsonTasks);
            client.put(Epics, jsonEpics);
            client.put(Subtasks, jsonSubtasks);
            client.put(History, jsonHistory);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromServer() throws IOException, InterruptedException {
        JsonElement jsonElementTasks = JsonParser.parseString(client.load(Tasks));
        JsonArray jsonTasks = jsonElementTasks.getAsJsonArray();
        for (int i = 0; i < jsonTasks.size(); i++) {
            Task task = gson.fromJson(jsonTasks.get(i), Task.class);
            InMemoryTaskManager.tasks.put(task.id, task);
        }
        JsonElement jsonElementEpics = JsonParser.parseString(client.load(Epics));
        JsonArray jsonEpics = jsonElementEpics.getAsJsonArray();
        for (int i = 0; i < jsonEpics.size(); i++) {
            Epic task = gson.fromJson(jsonEpics.get(i), Epic.class);
            InMemoryTaskManager.epics.put(task.id, task);
        }
        JsonElement jsonElementSubtasks = JsonParser.parseString(client.load(Subtasks));
        JsonArray jsonSubtasks = jsonElementSubtasks.getAsJsonArray();
        for (int i = 0; i < jsonSubtasks.size(); i++) {
            SubTask task = gson.fromJson(jsonSubtasks.get(i), SubTask.class);
            InMemoryTaskManager.subTasks.put(task.id, task);
        }
        JsonElement jsonElementHistory = JsonParser.parseString(client.load(History));
        JsonArray jsonHistory = jsonElementHistory.getAsJsonArray();
        for (int i = 0; i < jsonHistory.size(); i++) {
            Task task = gson.fromJson(jsonHistory.get(i), Task.class);
            InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Manager.getDefaultHistory();
            if (InMemoryTaskManager.tasks.containsKey(task.id)) {
                historyManager.add(InMemoryTaskManager.tasks.get(task.id));

            } else if (InMemoryTaskManager.epics.containsKey(task.id)) {
                historyManager.add(InMemoryTaskManager.epics.get(task.id));

            } else if (InMemoryTaskManager.subTasks.containsKey(task.id)) {
                historyManager.add(InMemoryTaskManager.subTasks.get(task.id));
            }
        }
    }
}
