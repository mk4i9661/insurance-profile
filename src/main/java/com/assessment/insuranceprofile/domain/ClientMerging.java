package com.assessment.insuranceprofile.domain;

import java.util.Collection;
import java.util.stream.Stream;

public class ClientMerging {
    private final Collection<Client> sourceClients;

    public ClientMerging(Collection<Client> sourceClients) {
        this.sourceClients = sourceClients;
    }

    private static RiskProfile riskiestProfile(Stream<RiskProfile> profiles) {
        return profiles.max(RiskProfile.COMPARATOR)
                .orElseThrow(() -> new IllegalArgumentException("At least one profile should be specified."));
    }

    public Client merge() {
        return Client.newClient(
                riskiestProfile(
                        sourceClients.stream()
                                .map(Client::riskProfile)
                )
        );
    }
}
