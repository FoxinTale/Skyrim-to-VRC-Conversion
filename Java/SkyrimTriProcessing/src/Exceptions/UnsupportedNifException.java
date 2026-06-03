package Exceptions;

public class UnsupportedNifException extends Exception {

    public UnsupportedNifException(String message) {
        super(message);
    }

    public UnsupportedNifException(String message, Throwable cause) {
        super(message, cause);
    }
}