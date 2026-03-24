package com.example.rivarly.exception;

import lombok.Getter;

@Getter
public class CompetitionException extends RuntimeException {
    private final String messageCode;
    private final String errorCode;
    private final Object[] args;

    public CompetitionException(String messageCode, String errorCode, Object... args) {
        super(messageCode);
        this.messageCode = messageCode;
        this.errorCode = errorCode;
        this.args = args;
    }
}