package com.assessment.insuranceprofile.domain;

import lombok.Value;
import lombok.With;

@Value
@With
public class Client {
    private final Long id;
    private final RiskProfile riskProfile;

    public static Client newClient(RiskProfile riskProfile) {
        return new Client(null, riskProfile);
    }
}

