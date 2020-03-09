package com.assessment.insuranceprofile.api.events;

import lombok.Value;

@Value
public class ClientDoesNotExist implements ClientEvent {
    private final Long id;

    @Override
    public String description() {
        return String.format("A client with id = %s does not exists", id);
    }
}
