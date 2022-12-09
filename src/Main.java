public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("Решить задачу", "Решить задачу в Практикуме", manager.countId(),
                "To do");
        Epic epic1 = new Epic("День рождение", "Организовать др", manager.countId(), "");
        SubTask subTask1 = new SubTask("Ресторан", "Посмотреть рестораны рядом", manager.countId(),
                "Done", epic1.id);
        SubTask subTask1_1 = new SubTask("Гости", "Позвать гостей", manager.countId(), "New",
                epic1.id);

        Epic epic2 = new Epic("Свадьба", "Организовать свадьбу", manager.countId(), "");
        SubTask subTask2 = new SubTask("Найти молодожен", "Посмореть вокруг", manager.countId(),
                "Done", epic2.id);

        manager.create(task1);
        manager.create(epic1);
        manager.create(epic2);
        manager.create(subTask1);
        manager.create(subTask1_1);
        manager.create(subTask2);

        System.out.println(manager.getEpicSubTasks(epic1.id));

        manager.getAllTasks();
        System.out.println(manager.tasksList);
        System.out.println(manager.epicsList);
        System.out.println(manager.subTasksList);

        manager.deleteById(5);
        System.out.println(epic1);
        System.out.println(epic2);


    }
}
