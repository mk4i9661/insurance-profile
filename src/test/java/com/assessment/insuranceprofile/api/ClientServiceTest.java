package com.assessment.insuranceprofile.api;

import com.assessment.insuranceprofile.domain.Client;
import com.assessment.insuranceprofile.domain.RiskProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.assessment.insuranceprofile.api.ClientServiceTest.Clients.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    private ClientService clientService;
    private final ClientRepository clientRepository = mock(ClientRepository.class);
    private final ClientEvents events = mock(ClientEvents.class);

    static class Clients {
        static final Client NORMAL_RISK_CLIENT = new Client(1L, RiskProfile.NORMAL);
        static final Client HIGH_RISK_CLIENT = new Client(2L, RiskProfile.HIGH);
        static final Client LOW_RISK_CLIENT = new Client(3L, RiskProfile.LOW);
    }

    @BeforeEach
    void initialize() {
        clientService = new ClientService(clientRepository, events);
    }

    @Test
    public void existingClients_should_return_a_list_of_clients() {
        when(clientRepository.existingClients(Pageable.unpaged()))
                .thenReturn(List.of(NORMAL_RISK_CLIENT, HIGH_RISK_CLIENT, LOW_RISK_CLIENT));

        assertThat(
                clientService.clients(Pageable.unpaged()),
                hasItems(NORMAL_RISK_CLIENT, HIGH_RISK_CLIENT, LOW_RISK_CLIENT)
        );
    }

    @Test
    public void findClient_should_return_existing_client() {
        when(clientRepository.findClient(NORMAL_RISK_CLIENT.id()))
                .thenReturn(Optional.of(NORMAL_RISK_CLIENT));

        Optional<Client> client = clientService.findClient(NORMAL_RISK_CLIENT.id());
        assertTrue(client.isPresent());
        assertThat(client.get(), is(NORMAL_RISK_CLIENT));
    }

    @Test
    public void createNewClient_should_create_a_new_client() {
        when(clientRepository.createNewClient(any()))
                .thenReturn(NORMAL_RISK_CLIENT);

        var client = clientService.createNewClient(Client.newClient(RiskProfile.NORMAL));
        assertEquals(NORMAL_RISK_CLIENT, client);
    }

    @Test
    public void updateExistingClient_should_update_existing_client() {
        when(clientRepository.findClient(NORMAL_RISK_CLIENT.id()))
                .thenReturn(Optional.of(NORMAL_RISK_CLIENT));

        var updateResult = NORMAL_RISK_CLIENT.withRiskProfile(RiskProfile.LOW);
        when(clientRepository.updateClient(any()))
                .thenReturn(updateResult);

        var client = clientService.updateExistingClient(updateResult);

        assertEquals(updateResult, client);
    }

    @Test
    public void updateExistingClient_should_emit_client_not_found_event() {
        when(clientRepository.findClient(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.updateExistingClient(NORMAL_RISK_CLIENT.withRiskProfile(RiskProfile.LOW))
        );
    }

    @Test
    public void deleteExistingClient_should_delete_existing_client() {
        when(clientRepository.findClient(anyLong()))
                .thenReturn(Optional.of(NORMAL_RISK_CLIENT));

        var deletedClient = clientService.deleteExistingClient(NORMAL_RISK_CLIENT.id());
        assertEquals(NORMAL_RISK_CLIENT, deletedClient);
    }

    @Test
    public void mergeClients_should_merge_existing_clients() {
        List<Client> existingClients = List.of(NORMAL_RISK_CLIENT, HIGH_RISK_CLIENT, LOW_RISK_CLIENT);
        when(clientRepository.findClients(any()))
                .thenReturn(existingClients);

        when(clientRepository.createNewClient(any()))
                .thenAnswer(
                        answer -> new Client(
                                100L,
                                answer.getArgument(0, Client.class).riskProfile()
                        )
                );

        var mergedClient = clientService.mergeClients(
                existingClients.stream()
                        .map(Client::id)
                        .collect(Collectors.toSet())
        );

        verify(clientRepository).deleteClients(eq(existingClients));

        assertEquals(new Client(100L, RiskProfile.HIGH), mergedClient);
    }

    @Test
    public void mergeClients_should_emit_not_all_clients_exist_event() {
        List<Client> existingClients = List.of(NORMAL_RISK_CLIENT, HIGH_RISK_CLIENT);
        existingClients.forEach(client -> {
            when(clientRepository.findClient(client.id()))
                    .thenReturn(Optional.of(client));
        });
        when(clientRepository.findClient(LOW_RISK_CLIENT.id()))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> clientService.mergeClients(
                        Set.of(NORMAL_RISK_CLIENT.id(), HIGH_RISK_CLIENT.id(), LOW_RISK_CLIENT.id())
                )
        );
    }
}