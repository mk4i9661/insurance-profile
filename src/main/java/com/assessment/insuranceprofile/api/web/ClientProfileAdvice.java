package com.assessment.insuranceprofile.api.web;

import com.assessment.insuranceprofile.api.ClientEvents;
import com.assessment.insuranceprofile.api.ClientNotFoundException;
import com.assessment.insuranceprofile.api.events.ClientDoesNotExist;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
class ClientProfileAdvice {

    private final ClientEvents events;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public ExceptionBody illegalArgumentExceptionHandler(Exception e) {
        return new ExceptionBody(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ClientNotFoundException.class)
    public ExceptionBody clientNotFoundExceptionHandler(ClientNotFoundException e) {
        events.announce(new ClientDoesNotExist(e.id()));
        return new ExceptionBody(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ExceptionBody fallbackExceptionHandler(Exception e) {
        log.error("An error occurred while executing request", e);
        return new ExceptionBody(e.getMessage());
    }
}
