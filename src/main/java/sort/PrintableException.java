package sort;

public class PrintableException extends RuntimeException{
    public PrintableException(String message) {
        super(message);
    }

    public PrintableException(String message, Throwable cause) {
        super(message, cause);
    }
}
