package manager;
import task.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    HashMap <Integer, Task> tasks = new HashMap<>();
    HashMap <Integer, Epic> epics = new HashMap<>();
    HashMap <Integer, SubTask> subTasks = new HashMap<>();

    public ArrayList <String> allTasks = new ArrayList<>();
    public ArrayList <String> allEpics = new ArrayList<>();
    public ArrayList <String> allSubTasks = new ArrayList<>();
    int counter = 0;

    public void getAllTasks() {
        allTasks.clear();
        allEpics.clear();
        allSubTasks.clear();
        for (Task obj : tasks.values()) {
            if (allTasks.contains(obj.name)) {
                int index = allTasks.indexOf(obj.name);
                allTasks.set(index, obj.name);
            } else {
                allTasks.add(obj.name);
            }
        }
        for (Epic obj : epics.values()) {
            if (allEpics.contains(obj.name)) {
                int index = allEpics.indexOf(obj.name);
                allEpics.set(index, obj.name);
            } else {
                allEpics.add(obj.name);
            }
        }
        for (SubTask obj : subTasks.values()) {
            if (allSubTasks.contains(obj.name)) {
                int index = allSubTasks.indexOf(obj.name);
                allSubTasks.set(index, obj.name);
            } else {
                allSubTasks.add(obj.name);
            }
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
    }

    public Object getTaskById(int id) {
        Object obj = null;
        if (tasks.containsKey(id)) {
            obj = tasks.get(id);
        } else if (epics.containsKey(id)) {
            obj = epics.get(id);
        } else if (subTasks.containsKey(id)) {
            obj = subTasks.get(id);
        }
        return obj;
    }

    public void create(Object object) {
        if (object.getClass() == Task.class){
            Task obj = (Task) object;
            tasks.put(obj.id, obj);
        }
        if (object.getClass() == Epic.class) {
            Epic obj = (Epic) object;
            obj.status = "New";
            epics.put(obj.id, obj);
        }
        if (object.getClass() == SubTask.class){
            SubTask obj = (SubTask) object;
            subTasks.put(obj.id, obj);
            epics.get(obj.epicsId).subTasksInEpic.add(obj.id);
            //Определяется статус Task.Epic
            int score = 0;
            for (int num : epics.get(obj.epicsId).subTasksInEpic) {
                if (subTasks.get(num).status.equals("In progress")) {
                    epics.get(obj.epicsId).status = "In progress";
                    return;
                } else if (subTasks.get(num).status.equals("New")) {
                    score++;
                }
            }
            if (score == epics.get(obj.epicsId).subTasksInEpic.size()) {
                epics.get(obj.epicsId).status = "New";
                return;
            } else if (score == 0) {
                epics.get(obj.epicsId).status = "Done";
                return;
            } else if (score > 0) {
                epics.get(obj.epicsId).status = "In progress";
                return;
            }
        }
    }

    public void update(Object object) {
        if (object.getClass() == Task.class) {
            Task obj = (Task) object;
            tasks.put(obj.id, obj);
        }

        if (object.getClass() == Epic.class) {
            Epic obj = (Epic) object;
            //При обновлении epic список его подзадач и статус сохраняются
            ArrayList<Integer> temporaryArray = epics.get(obj.id).subTasksInEpic;
            String temporaryStatus = epics.get(obj.id).status;
            epics.put(obj.id, obj);
            epics.get(obj.id).subTasksInEpic = temporaryArray;
            epics.get(obj.id).status = temporaryStatus;
        }

        if (object.getClass() == SubTask.class) {
            SubTask obj = (SubTask) object;
            subTasks.put(obj.id, obj);

            int score = 0;
            for (int num : epics.get(obj.epicsId).subTasksInEpic) {
                if (subTasks.get(num).status.equals("In progress")) {
                    epics.get(obj.epicsId).status = "In progress";
                    return;
                } else if (subTasks.get(num).status.equals("New")) {
                    score++;
                }
            }
            if (score == epics.get(obj.epicsId).subTasksInEpic.size()) {
                epics.get(obj.epicsId).status = "New";
                return;
            } else if (score == 0) {
                epics.get(obj.epicsId).status = "Done";
                return;
            } else if (score > 0) {
                epics.get(obj.epicsId).status = "In progress";
                return;
            }
        }
    }

    public void deleteById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            epics.get(id).subTasksInEpic.clear();
            epics.remove(id);
        } else if (subTasks.containsKey(id)) {
            //Подзадача удаляется из subTasks и из списка в epics
            for (int num : epics.get(subTasks.get(id).epicsId).subTasksInEpic) {
                if (id == num) {
                    int index = 0;
                    index = epics.get(subTasks.get(id).epicsId).subTasksInEpic.indexOf(num);
                    epics.get(subTasks.get(id).epicsId).subTasksInEpic.remove(index);

                    if (epics.get(subTasks.get(id).epicsId).subTasksInEpic.size() == 0) {
                        epics.get(subTasks.get(id).epicsId).status = "New";
                    }
                    subTasks.remove(id);
                    break;
                }
            }
        }
    }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        ArrayList<Integer> idsSubTasks = epics.get(id).subTasksInEpic;
        ArrayList<SubTask> epicsSubTasks = new ArrayList<>();
        for (int i : idsSubTasks) {
            epicsSubTasks.add(subTasks.get(i));
        }
        return epicsSubTasks;
    }

    public int countId() {
        counter++;
        return counter;
    }
}
