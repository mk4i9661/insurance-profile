package com.assessment.insuranceprofile.api;

import com.assessment.insuranceprofile.api.events.ClientEvent;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
class InstrumentedEventSink {

    private final MeterRegistry registry;

    @EventListener
    public void log(ClientEvent event) {
        registry.counter("client_events", "name", event.getClass().getSimpleName()).increment();
        log.info("An event received, type = {}, description = {}", event.getClass().getSimpleName(), event.description());
    }
}
