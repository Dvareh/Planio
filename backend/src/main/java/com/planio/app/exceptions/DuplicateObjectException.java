package com.planio.app.exceptions;

public class DuplicateObjectException extends RuntimeException {

    public DuplicateObjectException(String object, String value) {
        super(object + " already exists: " + value);
    }
}
