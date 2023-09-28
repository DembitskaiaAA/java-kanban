package exceptions;

import task.Task;

public class NegativeDurationException extends RuntimeException {
    public NegativeDurationException(final String message) {
        super(message);
    }

    public String getInfo() {
        return getMessage();
    }
}