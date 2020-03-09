package com.assessment.insuranceprofile.api.events;

import com.assessment.insuranceprofile.domain.Client;
import lombok.Value;

import java.util.Collection;

@Value
public class ClientsWereMerged implements ClientEvent {
    private final Collection<Client> sourceClients;
    private final Client client;

    @Override
    public String description() {
        return String.format("Clients has been merged, resulting client id = %s", client.id());
    }
}
