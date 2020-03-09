package com.assessment.insuranceprofile.api;

import lombok.Getter;

@Getter
public class ClientNotFoundException extends RuntimeException {
    private final Long id;

    public ClientNotFoundException(Long id) {
        super(String.format("No client exists with id = %s", id));
        this.id = id;
    }
}
