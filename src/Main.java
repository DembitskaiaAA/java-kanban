import manager.InMemoryTaskManager;
import manager.Manager;
import manager.Status;
import task.*;

public class Main {
    public static void main(String[] args) {
        var manager = (InMemoryTaskManager) Manager.getDefault();
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                Status.NEW);

        Task epic1 = new Epic("День рождение", "Организовать др", manager.countId(), null);
        Task subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                Status.IN_PROGRESS, epic1.id);
        Task subTask1_1 = new SubTask("Гости", "Позвать гостей", manager.countId(), Status.NEW,
                epic1.id);

        Task epic2 = new Epic("Квартира", "Продать квартиру", manager.countId(), null);
        Task subTask2 = new SubTask("Встретиться с риелтором", "Отдать ключи", manager.countId(),
                Status.NEW, epic2.id);

        Task epic3 = new Epic("Аэропорт", "Встретить друга", manager.countId(), null);

        Task subTask3 = new SubTask("Такси", "Заказать такси", manager.countId(),
                Status.IN_PROGRESS, epic3.id);

        manager.create(task1);

        manager.create(epic1);
        manager.create(subTask1);
        manager.create(subTask1_1);

        manager.create(epic2);
        manager.create(subTask2);

        manager.create(epic3);
        manager.create(subTask3);

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(3);
        manager.getTaskById(4);
        manager.getTaskById(5);
        manager.getTaskById(6);
        manager.getTaskById(7);
        manager.getTaskById(8);
        manager.getTaskById(1);
        manager.getTaskById(2);

        manager.getTaskById(3);

        System.out.println(manager.getHistory());
    }
}
