package com.assessment.insuranceprofile.infrastructure;

import com.assessment.insuranceprofile.api.ClientRepository;
import com.assessment.insuranceprofile.domain.Client;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

interface ClientDatabaseRepository extends ClientRepository, PagingAndSortingRepository<ClientDbRepresentation, Long> {
    @Override
    default List<Client> existingClients(Pageable pageable) {
        return StreamSupport.stream(this.findAll(pageable).spliterator(), false)
                .map(ClientDbRepresentation::toClient)
                .collect(Collectors.toList());
    }

    @Override
    default Optional<Client> findClient(long id) {
        return findById(id)
                .map(ClientDbRepresentation::toClient);
    }

    @Override
    default List<Client> findClients(Collection<Long> clientIdentifiers) {
        return StreamSupport.stream(findAllById(clientIdentifiers).spliterator(), false)
                .map(ClientDbRepresentation::toClient)
                .collect(Collectors.toList());
    }

    @Override
    default Client createNewClient(Client client) {
        return save(ClientDbRepresentation.ofClient(client))
                .toClient();
    }

    @Override
    default Client updateClient(Client client) {
        return save(ClientDbRepresentation.ofClient(client))
                .toClient();
    }

    @Override
    default void deleteClient(Long id) {
        deleteById(id);
    }

    @Override
    default void deleteClients(Collection<Client> clients) {
        this.deleteAll(
                clients.stream()
                        .map(ClientDbRepresentation::ofClient)
                        .collect(Collectors.toSet())
        );
    }
}
