package com.stackly.organization.exception;

public class OrganizationAlreadyExistsException extends RuntimeException {

    public OrganizationAlreadyExistsException(String message) {
        super(message);
    }
}
