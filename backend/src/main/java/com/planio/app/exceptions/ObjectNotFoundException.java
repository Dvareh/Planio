package com.planio.app.exceptions;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String object, Long id) {
        super(object + " not found with id: " + id);
    }

    public ObjectNotFoundException(String object, String value) {
        super(object + " not found: " + value);
    }
}
