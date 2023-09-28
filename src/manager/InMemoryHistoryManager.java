package manager;

import task.Node;
import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    public static List<Node<Task>> history = new ArrayList<>();
    public static Map<Integer, Node<Task>> tasks = new HashMap<>();
    public CustomLinkedList tasksCatalog = new CustomLinkedList();
    public static List<Task> result = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (tasks.containsKey(task.id)) {
            removeNode(tasks.get(task.id));
            tasks.remove(task.id);
            tasksCatalog.linkLast(task);
        } else {
            tasksCatalog.linkLast(task);
        }
    }

    public void removeNode(Node<Task> node) {
        CustomLinkedList.size--;
        //123 3 Перепривязка ссылок Node
        if ((node.next == null) && (node.prev != null)) {
            Node<Task> prevObject = node.prev;
            prevObject.next = null;
            CustomLinkedList.tail = prevObject;
            //1 1
        } else if ((node.prev == null) && (node.next == null)) {
            CustomLinkedList.head = null;
            CustomLinkedList.tail = null;
            //123 1
        } else if ((node.prev == null) && (node.next != null)) {
            Node<Task> nextObject = node.next;
            nextObject.prev = null;
            CustomLinkedList.head = nextObject;
            //123 2
        } else if ((node.prev != null) && (node.next != null)) {
            Node<Task> prevObject = node.prev;
            Node<Task> nextObject = node.next;
            prevObject.next = nextObject;
            nextObject.prev = prevObject;
        }
    }

    public void updateNode(Task task) {
        if (tasks.containsKey(task.id)) {
            Node<Task> node = tasks.get(task.id);
            node.data = task;
        } else {
            return;
        }
    }

    @Override
    public void remove(int id) {
        removeNode(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void removeAll() {
        CustomLinkedList.head = null;
        CustomLinkedList.tail = null;
        CustomLinkedList.size = 0;
        history.clear();
        tasks.clear();
        result.clear();
    }

    @Override
    public List<Task> getHistory() {
        if (CustomLinkedList.head == null) {
            result.clear();
            return result;
        }
        tasksCatalog.getTasks();
        result.clear();
        for (Node<Task> node : history) {
            result.add(node.data);
        }
        return result;
    }

    public static class CustomLinkedList {
        private static Node<Task> head;
        private static Node<Task> tail;
        private static int size = 0;

        public void linkLast(Task element) {
            Node<Task> oldTail = tail;
            Node<Task> newNodeTail = new Node<>(oldTail, element, null);
            tail = newNodeTail;
            if (oldTail == null) {
                head = newNodeTail;
            } else {
                oldTail.next = newNodeTail;
            }
            size++;
            tasks.put(element.id, newNodeTail);
        }

        public void getTasks() {
            history.clear();
            history.add(head);
            if (head.data == tail.data) {
                return;
            } else {
                for (int i = 0; i < size - 1; i++) {
                    history.add(history.get(i).next);
                }
            }
        }
    }
}