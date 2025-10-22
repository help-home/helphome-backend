package com.example.helphomebackend.exception;

public class InvalidCategoryException extends BusinessException {
    public InvalidCategoryException(String category) {
        super("유효하지 않은 카테고리입니다: " + category);
    }
}
