package manager;

import exceptions.*;
import task.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public FileBackedTasksManager() {
        this(new File( "tasks.CSV"));
    }

    @Override
    public int countId() {
        super.countId();
        return counter;
    }

    @Override
    public void create(Object object) throws NegativeDurationException, CrossedTimeException, RepeatingIdException,
            NegativeOrNullTasksIdException {
        super.create(object);
        save();
    }

    @Override
    public void update(Object object) throws NegativeDurationException, CrossedTimeException, RepeatingIdException {
        try {
            super.update(object);
        } catch (NegativeDurationException e) {
            System.out.println(e.getInfo());
        } catch (CrossedTimeException e) {
            System.out.println(e.getInfo());
        } catch (NegativeOrNullTasksIdException e) {
            System.out.println(e.getInfo());
        } catch (RepeatingIdException e) {
            System.out.println(e.getInfo());
        }
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void deleteById(int id) {
        super.deleteById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    public void save() {
        try (Writer wr = new FileWriter(file)) {
            wr.write("id,type,name,status,description,epic,startTime,duration,endTime");
            for (int i = 1; i <= tasks.size() + epics.size() + subTasks.size(); i++) {
                if (tasks.containsKey(i)) {
                    wr.write(toString(tasks.get(i)));
                } else if (epics.containsKey(i)) {
                    wr.write(toString(epics.get(i)));
                } else if (subTasks.containsKey(i)) {
                    wr.write(toString(subTasks.get(i)));
                }
            }
            if (!historyToString(Manager.getDefaultHistory()).equals("")) {
                wr.write("\n\n");
                wr.write(historyToString(Manager.getDefaultHistory()));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        } catch (ManagerSaveException e) {
            e.getInfo();
        }
    }

    public String toString(Task task) {
        String line = null;

        if (task.getClass() == Task.class) {
            line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", task.id, Type.TASK, task.name, task.status,
                    task.description, " ", task.startTime == null ? "null" : task.startTime.format(formatter),
                    task.duration == null ? "null" : task.duration.toMinutes(),
                    task.endTime == null ? "null" : task.endTime.format(formatter));
        } else if (task.getClass() == Epic.class) {
            line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", task.id, Type.EPIC, task.name, task.status,
                    task.description, " ", task.startTime == null ? "null" : task.startTime.format(formatter),
                    task.duration == null ? "null" : task.duration.toMinutes(),
                    task.endTime == null ? "null" : task.endTime.format(formatter));
        } else if (task.getClass() == SubTask.class) {
            line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", task.id, Type.SUBTASK, task.name, task.status,
                    task.description, ((SubTask) task).epicsId,
                    task.startTime == null ? "null" : task.startTime.format(formatter),
                    task.duration == null ? "null" : task.duration.toMinutes(),
                    task.endTime == null ? "null" : task.endTime.format(formatter));
        }
        return "\n" + line;
    }

    public static String historyToString(HistoryManager manager) {
        String numHistory = "";
        if (InMemoryHistoryManager.tasks.size() != 0) {
            List<Task> taskHistory = manager.getHistory();
            for (int i = 0; i < taskHistory.size(); i++) {
                if (i != taskHistory.size() - 1) {
                    numHistory = numHistory + taskHistory.get(i).id + ",";
                } else {
                    numHistory = numHistory + taskHistory.get(i).id;
                }
            }
        }
        return numHistory;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager rdManager = new FileBackedTasksManager(file);
        try (Reader rd = new FileReader(file); BufferedReader bf = new BufferedReader(rd)) {
            boolean firstLine = true;
            while ((bf.ready())) {
                String line = bf.readLine();
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                if (rdManager.fromString(line) != null) {
                    if (Task.class == rdManager.fromString(line).getClass()) {
                        tasks.put(rdManager.fromString(line).id, (Task) rdManager.fromString(line));
                        //Заполнить
                        // Manager.getDefaultHistory().add((Task) rdManager.fromString(line));
                    } else if (Epic.class == rdManager.fromString(line).getClass()) {
                        epics.put(rdManager.fromString(line).id, (Epic) rdManager.fromString(line));
                        // Manager.getDefaultHistory().add((Epic) rdManager.fromString(line));
                    } else if (SubTask.class == rdManager.fromString(line).getClass()) {
                        SubTask task = (SubTask) rdManager.fromString(line);
                        subTasks.put(task.id, task);
                        epics.get(task.epicsId).subTasksInEpic.add(task.id);
                        // Manager.getDefaultHistory().add((SubTask) rdManager.fromString(line));
                    }
                } else {
                    InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Manager.getDefaultHistory();
                    List<Integer> numHistory = historyFromString(line);
                    for (Integer id : numHistory) {
                        if (InMemoryTaskManager.tasks.containsKey(id)) {
                            historyManager.add(InMemoryTaskManager.tasks.get(id));

                        } else if (InMemoryTaskManager.epics.containsKey(id)) {
                            historyManager.add(InMemoryTaskManager.epics.get(id));

                        } else if (InMemoryTaskManager.subTasks.containsKey(id)) {
                            historyManager.add(InMemoryTaskManager.subTasks.get(id));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rdManager;
    }

    public Task fromString(String value) {
        Task task = null;
        String[] line = value.split(",");
        //Проверка на отсутствие задачи в массиве
        if (line.length > 1) {
            if (line[1].equals(String.valueOf(Type.TASK))) {
                task = new Task(line[2], line[4], Integer.parseInt(line[0]), Status.valueOf(line[3]),
                        line[6].equals("null") ? null : LocalDateTime.parse(line[6], formatter),
                        line[7].equals("null") ? null : Duration.ofMinutes(Integer.parseInt(line[7])));
                task.getEndTime();
                //1,TASK,Практикум,NEW,Решить задачу, ,01.01.2023 - 09:00,30,01.01.2023 - 09:30
                //Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),

            } else if (line[1].equals(String.valueOf(Type.EPIC))) {
                task = new Epic(line[2], line[4], Integer.parseInt(line[0]), Status.valueOf(line[3]),
                        line[6].equals("null") ? null : LocalDateTime.parse(line[6], formatter),
                        line[7].equals("null") ? null : Duration.ofMinutes(Integer.parseInt(line[7])));
                task.getEndTime();

            } else if (line[1].equals(String.valueOf(Type.SUBTASK))) {
                task = new SubTask(line[2], line[4], Integer.parseInt(line[0]), Status.valueOf(line[3]),
                        Integer.parseInt(line[5]),
                        line[6].equals("null") ? null : LocalDateTime.parse(line[6], formatter),
                        line[7].equals("null") ? null : Duration.ofMinutes(Integer.parseInt(line[7])));
                //6,SUBTASK,Встретиться с риелтором,NEW,Отдать ключи,5,null,null,null
                task.getEndTime();
            }
        }
        return task;
    }

    static List<Integer> historyFromString(String value) {
        String[] line = value.split(",");
        List<Integer> nums = new ArrayList<>();
        for (String id : line) {
            nums.add(Integer.parseInt(id));
        }
        if (nums.size() == 0) {
            nums.add(Integer.parseInt(value));
        }
        return nums;
    }
}
