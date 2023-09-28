package exceptions;

import task.Task;

public class NegativeOrNullTasksIdException extends RuntimeException {

    public NegativeOrNullTasksIdException(final String message) {
        super(message);
    }

    public String getInfo() {

        return getMessage();
    }
}
