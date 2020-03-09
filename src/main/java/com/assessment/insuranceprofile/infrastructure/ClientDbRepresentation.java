package com.assessment.insuranceprofile.infrastructure;

import com.assessment.insuranceprofile.domain.Client;
import com.assessment.insuranceprofile.domain.RiskProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class ClientDbRepresentation {
    @Id
    @GeneratedValue
    private Long id;

    private String riskProfile;

    public Client toClient() {
        return new Client(id, RiskProfile.valueOf(riskProfile));
    }

    public static ClientDbRepresentation ofClient(Client client) {
        return new ClientDbRepresentation(client.id(), client.riskProfile().name());
    }
}
