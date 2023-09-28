package exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message) {
        super(message);
    }

    public String getInfo() {
        return getMessage();
    }
}
