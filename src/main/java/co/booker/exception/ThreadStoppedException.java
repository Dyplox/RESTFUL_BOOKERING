package co.booker.exception;

public class ThreadStoppedException extends RuntimeException{
    public ThreadStoppedException(String message, Throwable cause) {
        super(message, cause);
    }
}
