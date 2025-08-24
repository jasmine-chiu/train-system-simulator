package unsw.exceptions;

public class InvalidRouteException extends UNSWException {
    public InvalidRouteException(String message) {
        super(message, "InvalidRouteException", 400);
    }
}
