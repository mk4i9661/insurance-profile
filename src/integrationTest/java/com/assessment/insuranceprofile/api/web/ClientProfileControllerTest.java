package com.assessment.insuranceprofile.api.web;

import com.assessment.insuranceprofile.BaseSpringBootTest;
import com.assessment.insuranceprofile.api.ClientService;
import com.assessment.insuranceprofile.domain.Client;
import com.assessment.insuranceprofile.domain.RiskProfile;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(name = "profile-controller-test")
class ClientProfileControllerTest extends BaseSpringBootTest {

    @Value("classpath:__files/clients/new-client-request.json")
    private File createClientRequest;

    @Value("classpath:__files/clients/update-client-request.json")
    private File updateClientRequest;

    @Value("classpath:__files/clients/merge-clients-request.json")
    private File mergeClientsRequest;

    @MockBean
    private ClientService clientService;

    @SneakyThrows
    @Test
    public void get_on_clients_should_return_a_list_of_clients() {
        when(clientService.clients(PageRequest.of(0, 100)))
                .thenReturn(
                        List.of(
                                new Client(1L, RiskProfile.HIGH),
                                new Client(2L, RiskProfile.LOW)
                        )
                );

        mockMvc.perform(
                get("/clients/")
                        .param("page", "0")
                        .param("size", "100")
        ).andExpect(
                matchAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.[0].id", CoreMatchers.is("1")),
                        jsonPath("$.[0].riskProfile", CoreMatchers.is("HIGH")),
                        jsonPath("$.[1].id", CoreMatchers.is("2")),
                        jsonPath("$.[1].riskProfile", CoreMatchers.is("LOW"))
                )
        );
    }

    @SneakyThrows
    @Test
    public void get_on_clients_with_id_should_return_existing_client() {
        when(clientService.findClient(1L))
                .thenReturn(Optional.of(new Client(1L, RiskProfile.HIGH)));

        mockMvc.perform(get("/clients/1")).andExpect(
                matchAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id", CoreMatchers.is("1")),
                        jsonPath("$.riskProfile", CoreMatchers.is("HIGH"))
                )
        );
    }

    @SneakyThrows
    @Test
    public void post_on_clients_should_create_a_new_client() {
        when(clientService.createNewClient(any()))
                .thenReturn(new Client(1L, RiskProfile.HIGH));

        mockMvc.perform(
                post("/clients/")
                        .content(Files.readAllBytes(createClientRequest.toPath()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                matchAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id", CoreMatchers.is("1")),
                        jsonPath("$.riskProfile", CoreMatchers.is("HIGH"))
                )
        );
    }

    @SneakyThrows
    @Test
    public void put_on_clients_with_id_should_update_existing_client() {
        Client updatedClient = new Client(1L, RiskProfile.NORMAL);
        when(clientService.updateExistingClient(any()))
                .thenReturn(updatedClient);

        mockMvc.perform(
                put("/clients/1")
                        .content(Files.readAllBytes(updateClientRequest.toPath()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                matchAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id", CoreMatchers.is("1")),
                        jsonPath("$.riskProfile", CoreMatchers.is("NORMAL"))
                )
        );
    }

    @SneakyThrows
    @Test
    public void delete_on_clients_with_id_should_delete_existing_client() {
        Client clientToDelete = new Client(1L, RiskProfile.HIGH);
        when(clientService.deleteExistingClient(clientToDelete.id()))
                .thenReturn(clientToDelete);

        mockMvc.perform(delete("/clients/1"))
                .andExpect(
                        status().is2xxSuccessful()
                );
    }

    @SneakyThrows
    @Test
    public void post_on_merging_with_specified_ids_should_return_a_merged_client() {
        Client mergedClient = new Client(3L, RiskProfile.HIGH);

        when(clientService.mergeClients(Set.of(1L, 2L)))
                .thenReturn(mergedClient);

        mockMvc.perform(
                post("/clients/merging")
                        .content(Files.readAllBytes(mergeClientsRequest.toPath()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                matchAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id", CoreMatchers.is("3")),
                        jsonPath("$.riskProfile", CoreMatchers.is("HIGH"))
                )
        );
    }
}