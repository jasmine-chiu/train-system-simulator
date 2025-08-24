package unsw.exceptions;

public class UNSWException extends Exception {
    private final String type;
    private final int statusCode;

    public UNSWException(String message, String type, int statusCode) {
        super(message);
        this.type = type;
        this.statusCode = statusCode;
    }

    public String getType() {
        return type;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
