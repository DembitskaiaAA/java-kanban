import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    HashMap <Integer, Task> taskHashMap = new HashMap<>();
    HashMap <Integer, Epic> epicHashMap = new HashMap<>();
    HashMap <Integer, SubTask> subTaskHashMap = new HashMap<>();

    ArrayList <String> tasksList = new ArrayList<>();
    ArrayList <String> epicsList = new ArrayList<>();
    ArrayList <String> subTasksList = new ArrayList<>();
    int counter = 0;

    public void getAllTasks() {
        for (Task obj : taskHashMap.values()) {
            if (tasksList.contains(obj.name)) {
                int index = tasksList.indexOf(obj.name);
                tasksList.set(index, obj.name);
            } else {
                tasksList.add(obj.name);
            }
        }
        for (Epic obj : epicHashMap.values()) {
            if (epicsList.contains(obj.name)) {
                int index = epicsList.indexOf(obj.name);
                epicsList.set(index, obj.name);
            } else {
                epicsList.add(obj.name);
            }
        }
        for (SubTask obj : subTaskHashMap.values()) {
            if (subTasksList.contains(obj.name)) {
                int index = subTasksList.indexOf(obj.name);
                subTasksList.set(index, obj.name);
            } else {
                subTasksList.add(obj.name);
            }
        }
    }

    public void deleteAllTasks() {
        taskHashMap.clear();
        epicHashMap.clear();
        subTaskHashMap.clear();
    }

    public Object getTaskById(int id) {
        Object obj = null;
        if (taskHashMap.containsKey(id)) {
            obj = taskHashMap.get(id);
        } else if (epicHashMap.containsKey(id)) {
            obj = epicHashMap.get(id);
        } else if (subTaskHashMap.containsKey(id)) {
            obj = subTaskHashMap.get(id);
        }
            return obj;
        }

    public void create(Object object) {
        if (object.getClass() == Task.class){
            Task obj = (Task) object;
            taskHashMap.put(obj.id, obj);
        }
        if (object.getClass() == Epic.class) {
            Epic obj = (Epic) object;
            epicHashMap.put(obj.id, obj);
            if (epicHashMap.get(obj.id).subTasksInEpic.size() == 0) {
                epicHashMap.get(obj.id).status = "New";
            }
        }
        if (object.getClass() == SubTask.class){
            SubTask obj = (SubTask) object;
            subTaskHashMap.put(obj.id, obj);
            epicHashMap.get(obj.epicsId).subTasksInEpic.add(obj);

            int score = 0;
            for (SubTask exp : epicHashMap.get(obj.epicsId).subTasksInEpic) {
                if (exp.status.equals("In progress")) {
                    epicHashMap.get(obj.epicsId).status = "In progress";
                    return;
                } else if (exp.status.equals("New")) {
                    score++;
                }
                }
            if (score == epicHashMap.get(obj.epicsId).subTasksInEpic.size()) {
                epicHashMap.get(obj.epicsId).status = "New";
                return;
            } else if (score == 0) {
                epicHashMap.get(obj.epicsId).status = "Done";
                return;
            } else if (score > 0) {
                epicHashMap.get(obj.epicsId).status = "In progress";
                return;
            }
        }
    }


    public void update(Object object) {
        if (object.getClass() == Task.class){
            Task obj = (Task) object;
            taskHashMap.put(obj.id, obj);
        }
        if (object.getClass() == Epic.class){
            Epic obj = (Epic) object;
            ArrayList<SubTask> temporaryArray = epicHashMap.get(obj.id).subTasksInEpic;
            epicHashMap.put(obj.id, obj);
            epicHashMap.get(obj.id).subTasksInEpic = temporaryArray;

            int score = 0;
            for (SubTask exp : epicHashMap.get(obj.id).subTasksInEpic) {
                if (exp.status.equals("In progress")) {
                    epicHashMap.get(obj.id).status = "In progress";
                    return;
                } else if (exp.status.equals("New")) {
                    score++;
                }
            }
            if (score == epicHashMap.get(obj.id).subTasksInEpic.size()) {
                epicHashMap.get(obj.id).status = "New";
                return;
            } else if (score == 0) {
                epicHashMap.get(obj.id).status = "Done";
                return;
            } else if (score > 0) {
                epicHashMap.get(obj.id).status = "In progress";
                return;
            }
        }
        if (object.getClass() == SubTask.class){
            SubTask obj = (SubTask) object;
            subTaskHashMap.put(obj.id, obj);
            ArrayList<SubTask> temporaryArray  = epicHashMap.get(obj.epicsId).subTasksInEpic;
            for (SubTask exp : temporaryArray) {
                if (obj.equals(exp)) {
                    int index = temporaryArray.indexOf(exp);
                    epicHashMap.get(obj.epicsId).subTasksInEpic.set(index, obj);
                }
            }
            int score = 0;
            for (SubTask exp : epicHashMap.get(obj.epicsId).subTasksInEpic) {
                if (exp.status.equals("In progress")) {
                    epicHashMap.get(obj.epicsId).status = "In progress";
                    return;
                } else if (exp.status.equals("New")) {
                    score++;
                }
            }
            if (score == epicHashMap.get(obj.epicsId).subTasksInEpic.size()) {
                epicHashMap.get(obj.epicsId).status = "New";
                return;
            } else if (score == 0) {
                epicHashMap.get(obj.epicsId).status = "Done";
                return;
            } else if (score > 0) {
                epicHashMap.get(obj.epicsId).status = "In progress";
                return;
            }
        }
    }


    public void deleteById(int id) {
        if (taskHashMap.containsKey(id)) {
            taskHashMap.remove(id);
        } else if (epicHashMap.containsKey(id)) {
            epicHashMap.remove(id);
        } else if (subTaskHashMap.containsKey(id)) {
            subTaskHashMap.remove(id);
            for (Epic exp : epicHashMap.values()){
                for (SubTask num : exp.subTasksInEpic) {
                    if (num.id == id) {
                        int index = 0;
                        index = exp.subTasksInEpic.indexOf(num);
                        exp.subTasksInEpic.remove(index);
                    }
                }
            }
        }
    }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        ArrayList<SubTask> subTasks = epicHashMap.get(id).subTasksInEpic;
        return subTasks;
    }

    public int countId() {
        counter++;
        return counter;
    }


}
