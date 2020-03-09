package com.assessment.insuranceprofile.infrastructure;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbClientRepository extends CrudRepository<ClientDbRepresentation, Long> {

}
