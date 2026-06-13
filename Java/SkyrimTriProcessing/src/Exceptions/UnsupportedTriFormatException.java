package Exceptions;

import java.io.IOException;

public class UnsupportedTriFormatException extends IOException {
    public UnsupportedTriFormatException(String message) {
        super(message);
    }
}