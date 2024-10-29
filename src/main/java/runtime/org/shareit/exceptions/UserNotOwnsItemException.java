package runtime.org.shareit.exceptions;

public class UserNotOwnsItemException extends RuntimeException {
    public UserNotOwnsItemException(String message) {
        super(message);
    }
}
