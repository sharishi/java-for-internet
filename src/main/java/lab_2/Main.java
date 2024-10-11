package lab_2;


public class Main {
    public static void main(String[] args) {
        DatabaseModel model = new DatabaseModel();
        DatabaseView view = new DatabaseView();
        DatabaseController controller = new DatabaseController(model, view);

        controller.run();
    }
}
