package com.assessment.insuranceprofile.api;

import com.assessment.insuranceprofile.api.events.ClientCreated;
import com.assessment.insuranceprofile.api.events.ClientDeleted;
import com.assessment.insuranceprofile.api.events.ClientUpdated;
import com.assessment.insuranceprofile.api.events.ClientsWereMerged;
import com.assessment.insuranceprofile.domain.Client;
import com.assessment.insuranceprofile.domain.ClientMerging;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
@Slf4j
public class ClientService {
    private final ClientRepository repository;
    private final ClientEvents events;

    public List<Client> clients(Pageable pageable) {
        return repository.existingClients(pageable);
    }

    public Optional<Client> findClient(Long id) {
        return repository.findClient(id);
    }

    public Client createNewClient(Client client) {
        log.info("Creating a new client with risk profile = {}", client.riskProfile());
        Client newClient = repository.createNewClient(client);
        events.announce(new ClientCreated(newClient));
        return newClient;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Client updateExistingClient(Client client) {
        log.info("Updating a client with id = {}", client.id());
        Client updatedClient = repository.findClient(client.id())
                .map(repository::updateClient)
                .orElseThrow(() -> new ClientNotFoundException(client.id()));

        events.announce(new ClientUpdated(updatedClient));
        return client;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Client deleteExistingClient(Long id) {
        log.info("Deleting a client with id = {}", id);
        Client deletedClient = repository.findClient(id)
                .map(existingClient -> {
                    repository.deleteClient(id);
                    return existingClient;
                })
                .orElseThrow(() -> new ClientNotFoundException(id));

        events.announce(new ClientDeleted(deletedClient));
        return deletedClient;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Client mergeClients(Set<Long> clientIds) {
        log.info("Merging clients with id = {}", clientIds);
        var existingClients = repository.findClients(clientIds);
        if (existingClients.size() != clientIds.size()) {
            throw new IllegalArgumentException("Only existing clients can be merged.");
        }

        Client mergedClient = createNewClient(new ClientMerging(existingClients).merge());
        repository.deleteClients(existingClients);
        events.announce(new ClientsWereMerged(existingClients, mergedClient));
        return mergedClient;
    }
}

