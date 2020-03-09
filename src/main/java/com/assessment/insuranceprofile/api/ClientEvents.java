package com.assessment.insuranceprofile.api;

import com.assessment.insuranceprofile.api.events.ClientEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ClientEvents {

    private ApplicationEventPublisher eventPublisher;

    public void announce(ClientEvent event) {
        eventPublisher.publishEvent(event);
    }
}
