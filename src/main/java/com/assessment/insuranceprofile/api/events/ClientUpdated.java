package com.assessment.insuranceprofile.api.events;

import com.assessment.insuranceprofile.domain.Client;
import lombok.Value;

@Value
public class ClientUpdated implements ClientEvent {
    private final Client client;

    @Override
    public String description() {
        return String.format("Client with id = %s has been updated", client.id());
    }
}
