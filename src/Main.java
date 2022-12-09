public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("Практикум", "Решить задачу", manager.countId(),
                "New");

        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), "");
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                "New", epic1.id);
        SubTask subTask1_1 = new SubTask("Гости", "Позвать гостей", manager.countId(), "New",
                epic1.id);

        Epic epic2 = new Epic("Квартира", "Продать квартиру", manager.countId(), "");
        SubTask subTask2 = new SubTask("Встретиться с риелтором", "Отдать ключи", manager.countId(),
                "New", epic2.id);

        Epic epic3 = new Epic("Аэропорт", "Встретить друга", manager.countId(), "");

    }
}
