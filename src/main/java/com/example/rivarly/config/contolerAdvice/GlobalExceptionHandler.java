package com.example.rivarly.config.contolerAdvice;

import com.example.rivarly.exception.CompetitionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CompetitionException.class)
    public ResponseEntity<ErrorResponse> handleCompetitionException(CompetitionException ex, Locale locale) {
        String requestId = MDC.get("requestId");
        String localizedMessage = messageSource.getMessage(ex.getMessageCode(), ex.getArgs(), locale);

        log.error("Competition error [ID: {}]: {}", requestId, ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                localizedMessage,
                requestId,
                "https://sports-platform.com/docs/errors/" + ex.getErrorCode()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, Locale locale) {
        String requestId = MDC.get("requestId");
        log.error("Critical system error [ID: {}]", requestId, ex);

        String userMsg = messageSource.getMessage("error.internal", null, locale);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                userMsg,
                requestId,
                null
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

record ErrorResponse(int status, String message, String traceId, String helpUrl) {}