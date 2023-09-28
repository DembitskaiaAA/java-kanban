package exceptions;

public class RepeatingIdException extends RuntimeException {

    public RepeatingIdException(final String message) {
        super(message);
    }

    public String getInfo() {
        return (getMessage());
    }
}
