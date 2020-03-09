package com.assessment.insuranceprofile.api.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
class ExceptionBody {
    @JsonProperty
    private final String message;
}
