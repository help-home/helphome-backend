package com.example.helphomebackend.exception;

public class DuplicateResourceException extends BusinessException{
    public DuplicateResourceException(String message) {
        super(message);
    }
}
