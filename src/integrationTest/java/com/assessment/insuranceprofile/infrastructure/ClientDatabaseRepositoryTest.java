package com.assessment.insuranceprofile.infrastructure;

import com.assessment.insuranceprofile.BaseSpringBootTest;
import com.assessment.insuranceprofile.api.ClientRepository;
import com.assessment.insuranceprofile.domain.Client;
import com.assessment.insuranceprofile.domain.RiskProfile;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientDatabaseRepositoryTest extends BaseSpringBootTest {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private DbClientRepository dbClientRepository;

    @BeforeEach
    void initialize() {
        dbClientRepository.deleteAll();
    }

    @Test
    public void existingClients_should_return_a_list_of_clients() {
        dbClientRepository.saveAll(
                List.of(
                        new ClientDbRepresentation(null, "HIGH"),
                        new ClientDbRepresentation(null, "LOW"),
                        new ClientDbRepresentation(null, "NORMAL")
                )
        );

        assertThat(
                repository.existingClients(Pageable.unpaged()),
                is(
                        Streams.stream(dbClientRepository.findAll())
                                .map(ClientDbRepresentation::toClient).collect(Collectors.toList())
                )
        );
    }

    @Test
    public void findClient_should_return_existing_client() {
        ClientDbRepresentation result = dbClientRepository.save(new ClientDbRepresentation(null, "HIGH"));

        Optional<Client> client = repository.findClient(result.id());
        assertTrue(client.isPresent());

        assertThat(
                client.get(),
                is(new Client(result.id(), RiskProfile.HIGH))
        );
    }

    @Test
    public void findClients_should_return_existing_clients() {
        var existingClients = ImmutableList.copyOf(
                dbClientRepository.saveAll(
                        List.of(
                                new ClientDbRepresentation(null, "HIGH"),
                                new ClientDbRepresentation(null, "LOW"),
                                new ClientDbRepresentation(null, "NORMAL")
                        )
                )
        );

        List<Client> clients = repository.findClients(
                existingClients.stream().map(ClientDbRepresentation::id).collect(Collectors.toSet())
        );

        assertThat(
                clients,
                is(
                        existingClients.stream().map(ClientDbRepresentation::toClient).collect(Collectors.toList())
                )
        );
    }

    @Test
    public void createNewClient_should_create_a_new_client() {
        Client client = repository.createNewClient(Client.newClient(RiskProfile.LOW));

        assertThat(client.id(), is(notNullValue()));
        assertThat(
                client,
                is(dbClientRepository.findById(client.id()).get().toClient())
        );
    }

    @Test
    public void updateClient_should_update_existing_client() {
        ClientDbRepresentation existingClient = dbClientRepository.save(new ClientDbRepresentation(null, "LOW"));

        Client client = repository.updateClient(new Client(existingClient.id(), RiskProfile.HIGH));

        assertThat(
                client,
                is(dbClientRepository.findById(existingClient.id()).get().toClient())
        );
    }

    @Test
    public void deleteClient_should_delete_existing_client() {
        var existingClient = dbClientRepository.save(new ClientDbRepresentation(null, "LOW"));

        repository.deleteClient(existingClient.id());

        assertThat(
                dbClientRepository.findById(existingClient.id()).orElse(null),
                is(nullValue())
        );
    }

    @Test
    public void deleteClients_should_delete_existing_clients() {
        List<Client> existingClients = Streams.stream(
                dbClientRepository.saveAll(
                        List.of(
                                new ClientDbRepresentation(null, "LOW"),
                                new ClientDbRepresentation(null, "HIGH"),
                                new ClientDbRepresentation(null, "NORMAL")
                        )
                )
        ).map(ClientDbRepresentation::toClient).collect(Collectors.toList());
        repository.deleteClients(
                List.of(
                        existingClients.get(0),
                        existingClients.get(2)
                )
        );

        assertThat(
                Streams.stream(dbClientRepository.findAll()).map(ClientDbRepresentation::toClient)
                        .collect(Collectors.toList()),
                is(List.of(existingClients.get(1)))
        );
    }

}