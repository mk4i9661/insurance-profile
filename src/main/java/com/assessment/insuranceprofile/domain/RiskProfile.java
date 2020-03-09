package com.assessment.insuranceprofile.domain;

import lombok.AllArgsConstructor;

import java.util.Comparator;

@AllArgsConstructor
public enum RiskProfile implements Comparable<RiskProfile> {
    LOW(1),
    NORMAL(2),
    HIGH(3);

    private final int riskAssessmentValue;

    public static Comparator<RiskProfile> COMPARATOR = new RiskProfileComparator();

    private static class RiskProfileComparator implements Comparator<RiskProfile> {
        @Override
        public int compare(RiskProfile o1, RiskProfile o2) {
            return Integer.compare(o1.riskAssessmentValue, o2.riskAssessmentValue);
        }
    }
}
