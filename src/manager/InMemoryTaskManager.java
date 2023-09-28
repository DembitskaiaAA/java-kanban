package manager;

import exceptions.CrossedTimeException;
import exceptions.NegativeDurationException;
import exceptions.NegativeOrNullTasksIdException;
import exceptions.RepeatingIdException;
import task.Epic;
import task.SubTask;
import task.Task;
import task.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public static HashMap<Integer, Task> tasks = new HashMap<>();
    public static HashMap<Integer, Epic> epics = new HashMap<>();
    public static HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public static ArrayList<Task> allTasks = new ArrayList<>();
    public static ArrayList<Epic> allEpics = new ArrayList<>();
    public static ArrayList<SubTask> allSubTasks = new ArrayList<>();
    int counter = 0;
    public TreeSet<Task> sorted = new TreeSet<>(new Comparator<Task>() {
        @Override
        public int compare(Task a, Task b) {
            boolean nullFirst = false;
            if (a.id == b.id) {
                return 0;
            } else if (a.startTime == null) {
                //Если оба null или startTime равны сортировка по типу задачи (1 - Epic, 2 - SubTask, 3 - Task)
                //Если классы равны - сортировка по id
                return (b.startTime == null) ? ((a.getClass() == b.getClass()) ? (a.id - b.id) :
                        a.getClass().toString().compareTo(b.getClass().toString())) : (nullFirst ? -1 : 1);
            } else if (b.startTime == null) {
                return nullFirst ? 1 : -1;
            } else {
                return a.startTime.compareTo(b.startTime) == 0 ? ((a.getClass() == b.getClass()) ?
                        (a.id - b.id) : a.getClass().toString().compareTo(b.getClass().toString())) :
                        a.startTime.compareTo(b.startTime);
            }
        }
    });

    @Override
    public void getAllTasks() {
        allTasks.clear();
        allEpics.clear();
        allSubTasks.clear();
        for (Task obj : tasks.values()) {
            if (allTasks.contains(obj)) {
                int index = allTasks.indexOf(obj.name);
                allTasks.set(index, obj);
            } else {
                allTasks.add(obj);
            }
        }
        for (Epic obj : epics.values()) {
            if (allEpics.contains(obj)) {
                int index = allEpics.indexOf(obj);
                allEpics.set(index, obj);
            } else {
                allEpics.add(obj);
            }
        }
        for (SubTask obj : subTasks.values()) {
            if (allSubTasks.contains(obj)) {
                int index = allSubTasks.indexOf(obj);
                allSubTasks.set(index, obj);
            } else {
                allSubTasks.add(obj);
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
        sorted.clear();
        Manager.getDefaultHistory().removeAll();
    }

    @Override
    public Task getTaskById(int id) {
        Task obj = null;
        if (tasks.containsKey(id)) {
            obj = tasks.get(id);
        } else if (epics.containsKey(id)) {
            obj = epics.get(id);
        } else if (subTasks.containsKey(id)) {
            obj = subTasks.get(id);
        }
        Manager.getDefaultHistory().add(obj);
        return obj;
    }

    @Override
    public void create(Object object) throws NegativeDurationException, CrossedTimeException,
            NegativeOrNullTasksIdException {
        if (object.getClass() == Task.class) {
            Task obj = (Task) object;
            if (obj.id == 0 || obj.id < 0 ) {
                throw new NegativeOrNullTasksIdException("При создании ошибка: отрицательный id у Task");
            }
            if (tasks.containsKey(obj.id)) {
                update(obj);
                return;
            }
            obj.getEndTime();
            if (obj.duration.isNegative()) {
                throw new NegativeDurationException("При создании ошибка: отрицательная длительность у Task");
            }
            if (epics.containsKey(obj.id) || subTasks.containsKey(obj.id)) {
                throw new RepeatingIdException("При создании ошибка: повторяющийся id у Task");
            }
            if (obj.startTime != null) {
                boolean checker = sorted.stream().filter(task -> task.startTime != null).allMatch(task ->
                        (task.startTime.isAfter(obj.endTime) || task.endTime.isBefore(obj.startTime)));
                if (!checker) {
                    throw new CrossedTimeException("При создании ошибка: обнаружено пересечение по времени в Task");
                }
            }
            tasks.put(obj.id, obj);
            tasks.put(obj.id, obj);
            sorted.add(obj);
        }
        if (object.getClass() == Epic.class) {
            Epic obj = (Epic) object;
            if (obj.id == 0 || obj.id < 0 ) {
                throw new NegativeOrNullTasksIdException("При создании ошибка: отрицательный id у Epic");
            }
            obj.status = Status.NEW;
            if (tasks.containsKey(obj.id) || subTasks.containsKey(obj.id)) {
                throw new RepeatingIdException("При создании ошибка: повторяющийся id у Epic");
            }
            if (epics.containsKey(obj.id)) {
                update(obj);
                return;
            }
            epics.put(obj.id, obj);
            sorted.add(obj);

        }
        if (object.getClass() == SubTask.class) {
            SubTask obj = (SubTask) object;
            if (obj.id == 0 || obj.id < 0 ) {
                throw new NegativeOrNullTasksIdException("При создании ошибка: отрицательный id у Subtask");
            }
            if (subTasks.containsKey(obj.id)) {
                update(obj);
                return;
            }
            //Определили конец выполнения задания
            obj.getEndTime();
            if (epics.containsKey(obj.id) || tasks.containsKey(obj.id)) {
                throw new RepeatingIdException("При создании ошибка: повторяющийся id у Subtask");
            }
            if (obj.duration != null && obj.duration.isNegative()) {
                throw new NegativeDurationException("При создании ошибка: отрицательная длительность у subTask");
            }
            if (obj.startTime != null) {
                boolean checker = sorted.stream().filter(task -> task.startTime != null).allMatch(task ->
                        (task.startTime.isAfter(obj.endTime) || task.endTime.isBefore(obj.startTime)));
                if (!checker) {
                    throw new CrossedTimeException("При создании ошибка: обнаружено пересечение по времени в subTask"
                    );
                }
            }
            subTasks.put(obj.id, obj);
            sorted.remove(epics.get(obj.epicsId));
            Epic epic = epics.get(obj.epicsId);
            if (epic.subTasksInEpic == null) {
                epic.subTasksInEpic = new ArrayList<>();
            }
            epic.subTasksInEpic.add(obj.id);
            //epics.get(obj.epicsId).subTasksInEpic.add(obj.id);

            int score = 0;

            LocalDateTime start = LocalDateTime.MAX;
            LocalDateTime end = LocalDateTime.MIN;

            //Определяется startTime и endTIme
            for (int num : epics.get(obj.epicsId).subTasksInEpic) {
                if (subTasks.get(num).startTime == null) {
                    continue;
                }
                if (subTasks.get(num).startTime.isBefore(start)) {
                    start = subTasks.get(num).startTime;
                }
                if (subTasks.get(num).endTime.isAfter(end)) {
                    end = subTasks.get(num).endTime;
                }
                Duration duration = Duration.between(start, end);
                epics.get(obj.epicsId).startTime = start;
                epics.get(obj.epicsId).endTime = end;
                epics.get(obj.epicsId).duration = duration;
            }

            //Определяется статус Task.Epic
            for (int num : epics.get(obj.epicsId).subTasksInEpic) {
                if (subTasks.get(num).status.equals(Status.IN_PROGRESS)) {
                    epics.get(obj.epicsId).status = Status.IN_PROGRESS;

                    sorted.add(epics.get(obj.epicsId));
                    sorted.add(obj);

                    return;
                } else if (subTasks.get(num).status.equals(Status.NEW)) {
                    score++;
                }
            }
            if (score == epics.get(obj.epicsId).subTasksInEpic.size()) {
                epics.get(obj.epicsId).status = Status.NEW;
            } else if (score == 0) {
                epics.get(obj.epicsId).status = Status.DONE;
            } else if (score > 0) {
                epics.get(obj.epicsId).status = Status.IN_PROGRESS;
            }
            sorted.add(epics.get(obj.epicsId));
            sorted.add(obj);
        }
    }

    @Override
    public void update(Object object) throws NegativeDurationException, CrossedTimeException {
        if (object.getClass() == Task.class) {
            Task obj = (Task) object;
            obj.getEndTime();
            sorted.remove(tasks.get(obj.id));
            if (obj.duration != null && obj.duration.isNegative()) {
                throw new NegativeDurationException("При обновлении ошибка: отрицательная длительность у Task");
            }
            if (obj.startTime != null) {
                boolean checker = sorted.stream().filter(task -> task.startTime != null).allMatch(task ->
                        (task.startTime.isAfter(obj.endTime) || task.endTime.isBefore(obj.startTime)));
                if (!checker) {
                    throw new CrossedTimeException("При обновлении ошибка: обнаружено пересечение по времени в Task"
                    );
                }
            }
            tasks.put(obj.id, obj);
            Manager.getDefaultHistory().updateNode(obj);
            sorted.add(obj);
        }
        if (object.getClass() == Epic.class) {
            Epic obj = (Epic) object;
            sorted.remove(epics.get(obj.id));
            //При обновлении epic список его подзадач, статус и даты сохраняются
            ArrayList<Integer> temporaryArray = epics.get(obj.id).subTasksInEpic;
            Status temporaryStatus = epics.get(obj.id).status;
            LocalDateTime temporaryStart = epics.get(obj.id).startTime;
            LocalDateTime temporaryEnd = epics.get(obj.id).endTime;
            Duration temporaryDuration = epics.get(obj.id).duration;
            epics.put(obj.id, obj);
            epics.get(obj.id).subTasksInEpic = temporaryArray;
            epics.get(obj.id).status = temporaryStatus;
            epics.get(obj.id).startTime = temporaryStart;
            epics.get(obj.id).endTime = temporaryEnd;
            epics.get(obj.id).duration = temporaryDuration;
            Manager.getDefaultHistory().updateNode(obj);
            sorted.add(epics.get(obj.id));
        }
        if (object.getClass() == SubTask.class) {
            SubTask obj = (SubTask) object;
            sorted.remove(epics.get(obj.epicsId));
            sorted.remove(subTasks.get(obj.id));
            if (obj.duration != null && obj.duration.isNegative()) {
                throw new NegativeDurationException("При обновлении ошибка: отрицательная длительность у subTask");
            }
            if (obj.startTime != null) {
                boolean checker = sorted.stream().filter(task -> task.startTime != null).allMatch(task ->
                        (task.startTime.isAfter(obj.getEndTime()) || task.endTime.isBefore(obj.startTime)));
                if (!checker) {
                    throw new CrossedTimeException("При обновлении ошибка: обнаружено пересечение по времени в subTask"
                    );
                }
            }
            subTasks.put(obj.id, obj);
            int score = 0;
            for (int num : epics.get(obj.epicsId).subTasksInEpic) {
                if (subTasks.get(num).status.equals(Status.IN_PROGRESS)) {
                    epics.get(obj.epicsId).status = Status.IN_PROGRESS;
                    return;
                } else if (subTasks.get(num).status.equals(Status.NEW)) {
                    score++;
                }
            }
            if (score == epics.get(obj.epicsId).subTasksInEpic.size()) {
                epics.get(obj.epicsId).status = Status.NEW;
            } else if (score == 0) {
                epics.get(obj.epicsId).status = Status.DONE;
            } else if (score > 0) {
                epics.get(obj.epicsId).status = Status.IN_PROGRESS;
            }
            //Обновляем Epic(предварительно удаляя его из sorted) и удаляем subTask
            //Изменяем старт и конец эпика в зависимости от subTasks.
            // Если у subTasks время null, то у эпик время null, если у subTasks есть время, то находим max и min
            LocalDateTime start = LocalDateTime.MAX;
            LocalDateTime end = LocalDateTime.MIN;
            epics.get(subTasks.get(obj.id).epicsId).startTime = null;
            epics.get(subTasks.get(obj.id).epicsId).duration = null;
            epics.get(subTasks.get(obj.id).epicsId).endTime = null;
            for (int subId : epics.get(subTasks.get(obj.id).epicsId).subTasksInEpic) {
                if (subTasks.get(subId).startTime != null) {
                    subTasks.get(subId).getEndTime();
                    if (subTasks.get(subId).startTime.isBefore(start)) {
                        start = subTasks.get(subId).startTime;
                        epics.get(subTasks.get(obj.id).epicsId).startTime = start;
                    }
                    if (subTasks.get(subId).endTime.isAfter(end)) {
                        end = subTasks.get(subId).endTime;
                        epics.get(subTasks.get(obj.id).epicsId).endTime = end;
                        Duration duration = Duration.between(start, end);
                        epics.get(subTasks.get(obj.id).epicsId).duration = duration;
                    }
                }
            }
            sorted.add(epics.get(subTasks.get(obj.id).epicsId));
            sorted.add(subTasks.get(obj.id));
            Manager.getDefaultHistory().updateNode(obj);
        }
    }

    @Override
    public void deleteById(int id) {
        if (tasks.containsKey(id)) {
            sorted.remove(tasks.get(id));
            tasks.remove(id);
            if (InMemoryHistoryManager.tasks.containsKey(id)) {
                Manager.getDefaultHistory().remove(id);
            }
        } else if (epics.containsKey(id)) {
            for (Integer subId : epics.get(id).subTasksInEpic) {
                if (InMemoryHistoryManager.tasks.containsKey(subId)) {
                    Manager.getDefaultHistory().remove(subId);
                }
            }
            if (InMemoryHistoryManager.tasks.containsKey(id)) {
                Manager.getDefaultHistory().remove(id);
            }
            for (int i = 0; i < epics.get(id).subTasksInEpic.size(); i++) {
                sorted.remove(subTasks.get(epics.get(id).subTasksInEpic.get(i)));
                subTasks.remove(epics.get(id).subTasksInEpic.get(i));
            }
            sorted.remove(epics.get(id));
            epics.remove(id);
        } else if (subTasks.containsKey(id)) {
            //Подзадача удаляется из subTasks и из списка в epics
            for (int num : epics.get(subTasks.get(id).epicsId).subTasksInEpic) {
                if (id == num) {
                    int index = 0;
                    index = epics.get(subTasks.get(id).epicsId).subTasksInEpic.indexOf(num);
                    epics.get(subTasks.get(id).epicsId).subTasksInEpic.remove(index);
                    if (epics.get(subTasks.get(id).epicsId).subTasksInEpic.size() == 0) {
                        epics.get(subTasks.get(id).epicsId).status = Status.NEW;
                    }
                    if (InMemoryHistoryManager.tasks.containsKey(id)) {
                        Manager.getDefaultHistory().remove(id);
                    }
                    break;
                }
            }
            //Обновляем Epic(предварительно удаляя его из sorted) и удаляем subTask
            sorted.remove(epics.get(subTasks.get(id).epicsId));
            sorted.remove(subTasks.get(id));
            if (epics.get(subTasks.get(id).epicsId).subTasksInEpic.size() == 0) {
                epics.get(subTasks.get(id).epicsId).startTime = null;
                epics.get(subTasks.get(id).epicsId).duration = null;
                epics.get(subTasks.get(id).epicsId).endTime = null;
                sorted.add(epics.get(subTasks.get(id).epicsId));
                subTasks.remove(id);
                return;
            }
            //Изменяем время старта и конца у Epic в зависимости от subTasks
            // Если время subTask null, у Epic тоже null, если время есть, то находим max и min
            LocalDateTime start = LocalDateTime.MAX;
            LocalDateTime end = LocalDateTime.MIN;
            epics.get(subTasks.get(id).epicsId).startTime = null;
            epics.get(subTasks.get(id).epicsId).duration = null;
            epics.get(subTasks.get(id).epicsId).endTime = null;
            for (int subId : epics.get(subTasks.get(id).epicsId).subTasksInEpic) {
                if (subTasks.get(subId).startTime != null) {
                    if (subTasks.get(subId).startTime.isBefore(start)) {
                        start = subTasks.get(subId).startTime;
                        epics.get(subTasks.get(id).epicsId).startTime = start;
                    }
                    if (subTasks.get(subId).endTime.isAfter(end)) {
                        end = subTasks.get(subId).endTime;
                        epics.get(subTasks.get(id).epicsId).endTime = end;
                        Duration duration = Duration.between(start, end);
                        epics.get(subTasks.get(id).epicsId).duration = duration;
                    }
                }
            }
            sorted.add(epics.get(subTasks.get(id).epicsId));
            subTasks.remove(id);
        }
    }


    @Override
    public ArrayList<SubTask> getEpicSubTasks(int id) {
        ArrayList<Integer> idsSubTasks = epics.get(id).subTasksInEpic;
        ArrayList<SubTask> epicsSubTasks = new ArrayList<>();
        for (int i : idsSubTasks) {
            epicsSubTasks.add(subTasks.get(i));
        }
        return epicsSubTasks;
    }

    @Override
    public List<Task> getHistory() {
        return Manager.getDefaultHistory().getHistory();
    }

    @Override
    public int countId() {
        counter++;
        return counter;
    }

    @Override
    public SortedSet getPrioritizedTasks() {
        return sorted;
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }
}
