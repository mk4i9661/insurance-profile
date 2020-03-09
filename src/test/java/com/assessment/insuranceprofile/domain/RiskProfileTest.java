package com.assessment.insuranceprofile.domain;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskProfileTest {

    @Test
    public void comparator_should_return_riskiest_profile() {
        assertEquals(
                RiskProfile.HIGH,
                Stream.of(RiskProfile.values()).max(RiskProfile.COMPARATOR).get()
        );
    }
}