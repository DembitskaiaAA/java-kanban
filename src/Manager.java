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
        tasksList.clear();
        epicsList.clear();
        subTasksList.clear();
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
            obj.status = "New";
            epicHashMap.put(obj.id, obj);
        }
        if (object.getClass() == SubTask.class){
            SubTask obj = (SubTask) object;
            subTaskHashMap.put(obj.id, obj);
            epicHashMap.get(obj.epicsId).subTasksInEpic.add(obj.id);
                //Определяется статус Epic
            int score = 0;
            for (int num : epicHashMap.get(obj.epicsId).subTasksInEpic) {
                if (subTaskHashMap.get(num).status.equals("In progress")) {
                    epicHashMap.get(obj.epicsId).status = "In progress";
                    return;
                } else if (subTaskHashMap.get(num).status.equals("New")) {
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
        if (object.getClass() == Task.class) {
            Task obj = (Task) object;
            taskHashMap.put(obj.id, obj);
        }

        if (object.getClass() == Epic.class) {
            Epic obj = (Epic) object;
            //При обновлении Epic список его подзадач и статус сохраняются
            ArrayList<Integer> temporaryArray = epicHashMap.get(obj.id).subTasksInEpic;
            String temporaryStatus = epicHashMap.get(obj.id).status;
            epicHashMap.put(obj.id, obj);
            epicHashMap.get(obj.id).subTasksInEpic = temporaryArray;
            epicHashMap.get(obj.id).status = temporaryStatus;
        }

            if (object.getClass() == SubTask.class) {
                SubTask obj = (SubTask) object;
                subTaskHashMap.put(obj.id, obj);

                int score = 0;
                for (int num : epicHashMap.get(obj.epicsId).subTasksInEpic) {
                    if (subTaskHashMap.get(num).status.equals("In progress")) {
                        epicHashMap.get(obj.epicsId).status = "In progress";
                        return;
                    } else if (subTaskHashMap.get(num).status.equals("New")) {
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
            epicHashMap.get(id).subTasksInEpic.clear();
            epicHashMap.remove(id);
        } else if (subTaskHashMap.containsKey(id)) {
            //Подзадача удаляется из subTaskHashMap и из списка в epicHashMap
                for (int num : epicHashMap.get(subTaskHashMap.get(id).epicsId).subTasksInEpic) {
                    if (id == num) {
                        int index = 0;
                        index = epicHashMap.get(subTaskHashMap.get(id).epicsId).subTasksInEpic.indexOf(num);
                        epicHashMap.get(subTaskHashMap.get(id).epicsId).subTasksInEpic.remove(index);

                        if (epicHashMap.get(subTaskHashMap.get(id).epicsId).subTasksInEpic.size() == 0) {
                            epicHashMap.get(subTaskHashMap.get(id).epicsId).status = "New";
                        }
                        subTaskHashMap.remove(id);
                        break;
                    }
                }
            }
        }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        ArrayList<Integer> idsSubTasks = epicHashMap.get(id).subTasksInEpic;
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (int i : idsSubTasks) {
            subTasks.add(subTaskHashMap.get(i));
        }
        return subTasks;
    }

    public int countId() {
        counter++;
        return counter;
    }


}
