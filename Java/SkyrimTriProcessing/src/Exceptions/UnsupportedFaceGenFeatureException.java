package Exceptions;

import java.io.IOException;

public class UnsupportedFaceGenFeatureException extends IOException {
    public UnsupportedFaceGenFeatureException(String message) {
        super(message);
    }
}