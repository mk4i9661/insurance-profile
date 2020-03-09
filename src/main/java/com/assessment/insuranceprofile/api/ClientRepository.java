package com.assessment.insuranceprofile.api;

import com.assessment.insuranceprofile.domain.Client;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    List<Client> existingClients(Pageable pageable);

    Optional<Client> findClient(long id);

    List<Client> findClients(Collection<Long> clientIdentifiers);

    Client createNewClient(Client client);

    Client updateClient(Client client);

    void deleteClient(Long id);

    void deleteClients(Collection<Client> clients);
}
