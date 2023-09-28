package exceptions;

import task.Task;

public class CrossedTimeException extends RuntimeException {

    public CrossedTimeException(final String message) {
        super(message);
    }

    public String getInfo() {
        return getMessage();
    }
}